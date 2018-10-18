using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using WebSocketSharp;
using WebSocketSharp.Server;
using System.Web;
using System.Text.RegularExpressions;
using Newtonsoft.Json;
using System.Diagnostics;

namespace BetaWatchService
{
	/// <summary>
	/// Utility class that will handle monitoring functions
	/// </summary>
	public static class Monitor
	{
		//Will store currently running programs
		static Dictionary<string, Application> _programs;

		//Will store currently running websites
		static Dictionary<string, Website> _websites;
        static DateTime _serviceStart;

        static bool _informedOfShutdown;

        //Static constructor that will have initial running programs
        static Monitor()
		{
            _serviceStart = DateTime.Now;
            _informedOfShutdown = false;
            //Initial look at running programs
            _programs = new Dictionary<string, Application>();
			_websites = new Dictionary<string, Website>();

			//Check for only those with open window
			Process[] runningPrograms = Process.GetProcesses().Where(p => !String.IsNullOrEmpty(p.MainWindowTitle)).ToArray();
			foreach (Process p in runningPrograms)
			{
				//If it is not already inside the list, add it
				if (!_programs.ContainsKey(p.ProcessName.ToLower()))
				{
					//Query Whitelist from Firebase and store in a dictionary. URL MAY CHANGE
					Dictionary<string, double> whitelist = JsonConvert.DeserializeObject<Dictionary<string, double>>(FireBaseFunctions.queryList(FireBaseFunctions.getCurrentWhitelistURL() + "/programs.json"));

					//Modify to remove case sensitive when adding to dictionary
					string name = p.ProcessName.ToLower();
                    DateTime startTime;
                    try
                    {
                        startTime = p.StartTime;
                    }
                    catch(Exception ex)
                    {
                        startTime = DateTime.Now;
                    }
					if (!whitelist.ContainsKey(name)) 
						_programs[p.ProcessName.ToLower()] = new Application(p.ProcessName, p.MainWindowTitle, startTime, 0);
					else
						_programs[p.ProcessName.ToLower()] = new Application(p.ProcessName, p.MainWindowTitle, startTime, whitelist[name]);
				}
			}
		}

		//Method responsible for resetting dictionary once security level changes
		public static void resetDict()
		{
			_programs.Clear();
			_websites.Clear();
		}

		////////////////////////////////////////////////////
		//
		//              MONITORING THREADS
		//
		////////////////////////////////////////////////////

		//Responsible for updating runtimes and running programs
		//If a program is closed, it will temporarily stop updating for that program
		public static void monitorRuntimes(object state)
		{
			while(!BW_Service._stopping)
			{
				Thread.Sleep(100);
				//Update the running programs list
				//Check for only those with open window

                if((DateTime.Now - _serviceStart).TotalSeconds > FireBaseFunctions.queryComputerTime())
                {
                    if(Program.dontShutDown)
                    {
                        if (!_informedOfShutdown)
                        {
                            Console.WriteLine("Not shutting down on request");
                            _informedOfShutdown = true;
                        }
                    }
                    else
                    {
                        System.Diagnostics.Process.Start("shutdown", "/s /f /t 0");
                    }
                }

				Process[] runningPrograms = Process.GetProcesses().Where(p => !String.IsNullOrEmpty(p.MainWindowTitle)).ToArray();

				//Initial add
				foreach (Process p in runningPrograms)
				{
					//If it is not inside the dictionary, add it
					if (!_programs.ContainsKey(p.ProcessName.ToLower()))
					{
						//Query Whitelist from Firebase and store in a dictionary. URL MAY CHANGE
						Dictionary<string, double> whitelist = JsonConvert.DeserializeObject<Dictionary<string, double>>(FireBaseFunctions.queryList(FireBaseFunctions.getCurrentWhitelistURL() + "/programs.json"));

						//Modify to be able to ignore case sensitive
						string name = p.ProcessName.ToLower();

						if (!whitelist.ContainsKey(name))
							_programs[p.ProcessName.ToLower()] = new Application(p.ProcessName, p.MainWindowTitle, p.StartTime, 0);
						else
							_programs[p.ProcessName.ToLower()] = new Application(p.ProcessName, p.MainWindowTitle, p.StartTime, whitelist[name]);
					}
				}

				//Now loop through active programs, updating the runtimes
				foreach (KeyValuePair<string,Application> check in _programs)
				{
					foreach (Process p in runningPrograms)
					{
						//If it is running and the program is in the dictionary, update the runtime
						if (check.Key.Contains(p.ProcessName.ToLower()))
							check.Value.updateRuntime();
					}

					//If it is not running and the program is in the dictionary, update the startime
					check.Value.updateStartTime();
				}

			}

			BW_Service._stoppedEvent.Set();
		}

		//Method that handles monitoring Blacklist
		public static void monitorBlacklist(object state)
		{
			//Run a loop to continue running service every 15 seconds. CHANGE TIMING LATER
			while (!BW_Service._stopping)
			{
				Thread.Sleep(100);

				//Query Blacklist Programs from Firebase and store values in a dictionary. URL MAY CHANGE
				Dictionary<string, double> blacklist = JsonConvert.DeserializeObject<Dictionary<string, double>>(FireBaseFunctions.queryList(FireBaseFunctions.getCurrentBlacklistURL() + "/programs.json"));

				//Monitor the blacklist
				//Check for only those with open window
				Process[] runningPrograms = Process.GetProcesses().Where(p => !String.IsNullOrEmpty(p.MainWindowTitle)).ToArray();
				foreach (Process p in runningPrograms)
				{
					//Check if a running program is in the blacklist
					foreach (KeyValuePair<string, double> bs in blacklist)
					{
						//If program name is in the blacklist and it is running, kill it
						if (p.ProcessName.ToLower().Contains(bs.Key))
						{
							p.Kill();
						}
					}
				}
			}

			BW_Service._stoppedEvent.Set();

			//END monitorBlacklist Method 
		}


		//Method that handles monitoring Whitelist
		public static void monitorWhitelist(object state)
		{
			//Run a loop to continue running service every 15 seconds
			while (!BW_Service._stopping)
			{
				Thread.Sleep(100);
                List<KeyValuePair<string, Application>> toRemovePrograms = null;
                List<KeyValuePair<string, Website>> toRemoveWebsites = null;
                //Check Whitelist with current running programs
                foreach (KeyValuePair<string, Application> p in _programs)
				{
					//Compare the running times to the time limit. If Over the time limit, remove from whitelist and add to blacklist
					if ( p.Value.getLimit() != 0 && p.Value.getRuntime().TotalSeconds > p.Value.getLimit())
					{
						FireBaseFunctions.addToList(p.Key.ToLower(), 0, "black", "programs");
						FireBaseFunctions.removeFromList(p.Key.ToLower(), "white", "programs");
                        if(toRemovePrograms == null)
                        {
                            toRemovePrograms = new List<KeyValuePair<string, Application>>();
                            toRemovePrograms.Add(p);
                        }
                        

                    }
				}
                if (toRemovePrograms != null)
                {
                    foreach (KeyValuePair<string, Application> p in toRemovePrograms)
                    {
                        _programs.Remove(p.Key);
                    }
                }

                //Check Whitelist with current running websites
                foreach (KeyValuePair<string, Website> w in _websites)
                {
                    if (w.Value.getTimeLimit() != -600 && w.Value.getRuntime().TotalSeconds > w.Value.getTimeLimit())
                    {
                        string website = w.Value.getWebURL().ToLower().Replace("www.", "").Replace('.', '*');
                        FireBaseFunctions.addToList(website, 0, "black", "websites");
                        FireBaseFunctions.removeFromList(website, "white", "websites");
                        if (toRemoveWebsites == null)
                        {
                            toRemoveWebsites = new List<KeyValuePair<string, Website>>();
                            toRemoveWebsites.Add(w);
                        }
                    }
                }
                if (toRemoveWebsites != null)
                {
                    foreach (KeyValuePair<string, Website> w in toRemoveWebsites)
                    {
                        _websites.Remove(w.Key);
                    }
                }

            }

			BW_Service._stoppedEvent.Set();

			//END monitorWhitelist method
		}


		////////////////////////////////////////////////////
		//
		//              MONITORING Commands
		//
		////////////////////////////////////////////////////

		//Method responsible for checking if a specified program is running
		public static bool isRunning( string programName )
		{
			//Get the list of current running processes
			Process[] runningPrograms = Process.GetProcesses().Where(p => !String.IsNullOrEmpty(p.MainWindowTitle)).ToArray();

			//Loop to see if the program name exists
			foreach (Process p in runningPrograms)
			{
				if (p.ProcessName.ToLower().Contains(programName.ToLower()))
					return true;
			}

			//If we get this far, program was not running
			return false;
		}

		//Method responsible for checking the runtime of a specified program
		public static string programRuntime( string programName )
		{
			//Chech the dictionary to see if program exists. If it exists return runtime
			if (_programs.ContainsKey(programName.ToLower()))
			{
				return _programs[programName].getRuntime().TotalSeconds.ToString();
			}
			else
				return "program not found";

		}

		//Method responsible for returning how much time is remaining for a specific program
		public static string programRemainingTime( string programName )
		{
			//Chech the dictionary to see if program exists. If it exists return remainingTime
			if (_programs.ContainsKey(programName.ToLower()))
			{
				//If there was never a limit set, assume it 
				if (_programs[programName].getLimit() == 0)
					return "unlimited";
				else
					return ( _programs[programName].getLimit() - _programs[programName].getRuntime().TotalSeconds ).ToString() ;
			}
			else
				return "program not found";
		}

        //Method responsible for returning how much time is remaining for a specific program
        public static string programAddTime(string programName, int seconds)
        {
            //Chech the dictionary to see if program exists. If it exists return remainingTime
            if (_programs.ContainsKey(programName.ToLower()))
            {
                _programs[programName].subtractRuntime(seconds);
                //If there was never a limit set, assume it 
                return "success";
            }
            else
                return "program not found";
        }

        //Method responsible for returning allocated time in whitelist
        public static string allocatedTime( string type, string name )
		{
			//Check what we are querying, program or website
			string url;
			if (type == "website")
			{
				url = "/websites.json";
				name = name.ToLower().Replace("www.", "");
				name = name.Replace('.', '*');
			}
			else
			{
				url = "/programs.json";
				name = name.ToLower();
			}
			//Query
			Dictionary<string, string> whitelist = JsonConvert.DeserializeObject<Dictionary<string, string>>(FireBaseFunctions.queryList(FireBaseFunctions.getCurrentWhitelistURL() + url ));
			if (!whitelist.ContainsKey(name))
				return "no limit set";
			else
				return whitelist[name];
		}

		//Method responsible for returning remaining time website
		public static string websiteRemainingTime(string website)
		{
			website = website.ToLower().Replace("www.", "");

			//Chech the dictionary to see if Website exists. If it exists return remainingTime
			if (_websites.ContainsKey(website))
			{

				if (_websites[website].getRemainingTime() == -600)
					return "unlimited";
				else
					return _websites[website].getRemainingTime().ToString();
			}
			else
				return "website not found";
		}

		//Method responsible for returning runtime of webiste
		public static string websiteRuntime(string website)
		{
			website = website.ToLower().Replace("www.", "");

			//Chech the dictionary to see if program exists. If it exists return runtime
			if (_websites.ContainsKey(website))
			{
				return _websites[website].getRuntime().TotalSeconds.ToString();
			}
			else
				return "program not found";
		}

		//Method responsible for adding websites to dict
		public static void addToWebsiteList(string data_time, string data_domain)
		{
			//Check if the website is already in the list or not
			string websiteName = data_domain.ToLower().Replace("www.", "");
            if (!_websites.ContainsKey(websiteName))
            {
                _websites.Add(websiteName, new Website(data_domain, data_time));
            }
            else
            {
                _websites[websiteName].setStartTime(data_time);
                _websites[websiteName].setEndTime("");
            }
		}

		//Method responsible for updating dict
		public static void websiteListEndTime(string data_time, string data_domain)
		{
			string websiteName = data_domain.ToLower().Replace("www.", "");
			if (_websites.ContainsKey(websiteName))
				_websites[websiteName].setEndTime(data_time);
		}

        public static String[] getProgramsRan()
        {
            return _programs.Keys.ToArray<String>();
        }

        public static String[] getWebsitesVisited()
        {
            return _websites.Keys.ToArray<String>();
        }

        public static double getRemainingComputerTime()
        {
            return FireBaseFunctions._computerTime - ((DateTime.Now - _serviceStart).TotalSeconds);
        }

        //END MONITOR UTILITY CLASS
    }
}
