using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace BetaWatchService
{
	/// <summary>
	/// Utility Class that will help handling firebase functionalities
	/// </summary>
	public static class FireBaseFunctions
	{
		public static string _securityLevel;
        public static int _computerTime;

		//String used to access root directory of the database
		public static string databaseURL = "https://betawatch-e87e2.firebaseio.com/";

		//Static constructor that will have the initial security level mode
		static FireBaseFunctions()
		{
            queryComputerTime();
            queryCurrentMode();
            
        }


		//Method Responsible for getting link for current blacklist
		public static string getCurrentBlacklistURL()
		{
			return databaseURL + _securityLevel + "/blacklist";
		}

		//Method Responsible for getting link for current whitelist
		public static string getCurrentWhitelistURL()
		{
			return databaseURL + _securityLevel + "/whitelist";
		}

        //Method responsible for querying SecurityMode
        public static void queryCurrentMode()
        {
            //Query for initial security level
            //Setup connection with firebase, look for current Mode. URL may change
            var fbRequest = (HttpWebRequest)WebRequest.Create(databaseURL + "currentMode.json");
            fbRequest.ContentType = "application/json: charset=utf-8";
            var fbResponse = fbRequest.GetResponse() as HttpWebResponse;
            string fbSecurityLevel;

            //Read in data from Firebase
            using (var sr = new StreamReader(fbResponse.GetResponseStream()))
            {
                fbSecurityLevel = sr.ReadToEnd();
            }

            //Deserealize the mode, and store it
            _securityLevel = (string)JsonConvert.DeserializeObject(fbSecurityLevel);
        }

        //Method responsible for querying SecurityMode
        public static int queryComputerTime()
        {
            //Query for initial computerTime
            //Setup connection with firebase, look for current Mode. URL may change
            var fbRequest = (HttpWebRequest)WebRequest.Create(databaseURL + "computer_time.json");
            fbRequest.ContentType = "application/json: charset=utf-8";
            var fbResponse = fbRequest.GetResponse() as HttpWebResponse;
            string fbComputerTime;

            //Read in data from Firebase
            using (var sr = new StreamReader(fbResponse.GetResponseStream()))
            {
                fbComputerTime = sr.ReadToEnd();
            }

            //Deserealize the mode, and store it
            _computerTime = int.Parse(fbComputerTime);
            return _computerTime;
        }

        //Method responsible for querying FireBase lists
        public static string queryList ( string url )
		{
			//Setup connection with firebase, look at blacklist JSON
			var fbRequest = (HttpWebRequest)WebRequest.Create(url);
			fbRequest.ContentType = "application/json: charset=utf-8";
			var fbResponse = fbRequest.GetResponse() as HttpWebResponse;
			string programs;

			//Read in data from Firebase
			using (var sr = new StreamReader(fbResponse.GetResponseStream()))
			{
				programs = sr.ReadToEnd();
			}

			//Return the Serialized String from FireBase
			return programs;

			//END queryList method
		}

		//Method responsible for sending updates to Firebase. Using the REST API
		public static void updateFireBase(string url, JObject updatedItem)
		{
			//Request connection, this time to update the file
			var fbRequest = (HttpWebRequest)WebRequest.Create(url);
			fbRequest.Method = "PATCH";
			fbRequest.ContentType = "application/json: charset=utf-8";

			//create buffer to be able to send JSON contents
			var buffer = Encoding.UTF8.GetBytes(updatedItem.ToString());
			fbRequest.ContentLength = buffer.Length;
			fbRequest.GetRequestStream().Write(buffer, 0, buffer.Length);

			//Get Response from connection
			var fbResponse = fbRequest.GetResponse() as HttpWebResponse;
			var jsonResponse = (new StreamReader(fbResponse.GetResponseStream())).ReadToEnd();
		}


		/*/////////////////////////////////////
		/ 
		/			FIREBASE COMMANDS
		/
		/////////////////////////////////////// */
		
		//Method that handles returning a list, SPECIFICALLY FOR SENDING
		public static string requestList(string list, string type)
		{
			//Check if the correct list is being used
			if (list.ToLower() == "white" || list.ToLower() == "black")
			{
				//Hold the correct url
				string url;

				//Get the correct url
				if (list.ToLower() == "white")
					url = getCurrentWhitelistURL();
				else
					url = getCurrentBlacklistURL();

				//Append the type to the string, website or program
				url += "/" + type.ToLower();

				//Create a dictionary. URL MAY CHANGE HERE
				Dictionary<string, int> fbList = JsonConvert.DeserializeObject<Dictionary<string, int>>(queryList(url + ".json"));

				//Remove any characters that are not needed
				string fbListSend = string.Join(",", fbList.Select(p => p.Key).ToArray());

				//Format the list of websites if type is website
				if (type.ToLower() == "websites")
				{
					fbListSend = fbListSend.Replace('*', '.');
				}

				return fbListSend;
			}
			else
				return "list not found";

		//END requestList method
		}

		//Method responsible for adding items to a list
		public static string addToList(string programAdd, int value, string list, string type)
		{
			//Check if correct list is used
			if (list.ToLower() == "white" || list.ToLower() == "black")
			{
				//Save newProgram
				string newItem = programAdd.ToLower();

				//Hold the url to the correct list
				string url;

				//Get the correct url
				if (list.ToLower() == "white")
					url = getCurrentWhitelistURL();
				else
					url = getCurrentBlacklistURL();

				//Query firebase for the specified list
				string listItems = queryList(url + ".json");

				//Create a JObject to be able to add programs
				JObject json = JObject.Parse(listItems);
				JObject listCheck = (JObject)json[type.ToLower()];

				//Check if adding to program or website
				if (type == "websites")
					newItem = newItem.Replace('.', '*');

				//Check if program was in the list at all, if not add
				if (listCheck[newItem] == null)
				{
					//Add program from list
					if (list.ToLower() == "white")
						listCheck.Add(new JProperty(newItem, value));
					else
						listCheck.Add(new JProperty(newItem, 0));

					//Update Firebase
					updateFireBase(url + ".json", json);

					return "success";
				}
				else
					return "item already in list";

			}
			else
				return "Error!List not found";

		//END addToList method
		}

		//Method responsible for deleting from a list
		public static string removeFromList(string programDelete, string list, string type)
		{
			//Check if correct list is used
			if ( list.ToLower() == "white" || list.ToLower() == "black" )
			{
				//Hold the url to the correct list
				string url;

				//Get the correct url
				if (list.ToLower() == "white")
					url = getCurrentWhitelistURL();
				else
					url = getCurrentBlacklistURL();

				//Query firebase for the specified list
				string listItems = queryList(url + ".json");

				//Create a JObject to be able to remove programs
				JObject json = JObject.Parse(listItems);
				JObject listCheck = (JObject)json[type.ToLower()];

				//Check if removing to program or website
				if (type == "websites")
					programDelete = programDelete.Replace('.', '*');

				//Check if program was in the list at all, if so delete
				if (listCheck[programDelete.ToLower()] != null)
				{
					//Remove program from list
					listCheck.Remove(programDelete.ToLower());

					//Update Firebase
					updateFireBase(url + ".json", json);
					return "success";
				}
				else
					return "item not in list";

			}
			else
				return "Error!List not found";

		//END removeFromList method
		}


		//Method responsible for changing modes
		public static string changeSecurityLevel( string level )
		{
			//Check if current level has already been set, are the same
			if (_securityLevel == level.ToLower())
				return "same mode";
			//Check that the correct mode is being sent
			else if (level.ToLower() == "funmode" || level.ToLower() == "studymode" || level.ToLower() == "freereignn")
			{
				//Query the current mode
				string fbSecurityLevel = queryList(databaseURL + ".json");

				//Change the current mode, JSON
				JObject json = JObject.Parse(fbSecurityLevel);
				json["currentMode"] = level.ToLower();

				//Update in Firebase
				updateFireBase(databaseURL + ".json", json);

				//Change the mode in the utility class
				_securityLevel = level;

				//Reset the Monitoring dictionary
				Monitor.resetDict();

				return "set";
			}
			else
				return "mode not found";
		}

        //Method responsible for changing modes
        public static string changeComputerTime(int newTime)
        {
            
            //Check if newTime is positive
            if (newTime >= 0)
            {
                //Query the current mode
                string fbSecurityLevel = queryList(databaseURL + ".json");

                //Change the current mode, JSON
                JObject json = JObject.Parse(fbSecurityLevel);
                json["computer_time"] = newTime;

                //Update in Firebase
                updateFireBase(databaseURL + ".json", json);

                //Change the mode in the utility class
                _computerTime = newTime;

                //Reset the Monitoring dictionary
                Monitor.resetDict();

                return "set";
            }
            else
                return "cannot have negative computer time";
        }

        //Method responsible for querying and returning settings from the db
        public static string requestSettings(string setting)
		{
			string result = "";

			//Use a switch statement to distinguish what the user wants
			switch(setting.ToLower())
			{
				//Check if the parent can be emailed
				case "can_email":
					//Query db, deserialize and return it
					result = (string)JsonConvert.DeserializeObject(queryList(databaseURL + "settings/can_email.json"));
					break;
				case "can_sms":
					//Query db, deserialize and return it
					result = (string)JsonConvert.DeserializeObject(queryList(databaseURL + "settings/can_sms.json"));
					break;

				case "parent_email":
					//Query db for email, deserialize result and return it
					result = (string)JsonConvert.DeserializeObject(queryList(databaseURL + "settings/email.json"));
					break;

				case "parent_sms":
					//Query db for phone# deserialize result and return it
					result = (string)JsonConvert.DeserializeObject(queryList(databaseURL + "settings/sms.json"));
					break;
			}
			//Return the result
			return result;

		//END requestSetting method
		}


		//Method responsible for making changes to the settings branch in the database
		public static string updateSettings(string setting, string updatedField)
		{
			//Query the current settings
			string fbSecurityLevel = queryList(databaseURL + "/settings.json");

			//Modify the settings, JSON
			JObject json = JObject.Parse(fbSecurityLevel);
			json[setting] = updatedField.ToLower();

			//Update in Firebase
			updateFireBase(databaseURL + "/settings.json", json);

			return "success";
		}

	}
}
