using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using WebSocketSharp;

namespace BetaWatch
{
	public partial class Service2 : ServiceBase
	{
		//Class Variables
		//private Thread taskMonitor;
		private Thread wsComms;
		//AutoResetEvent StopRequest = new AutoResetEvent(false);

		//Class Constructor
		public Service2()
		{
			InitializeComponent();
		}

		//For Debugging purposes
		public void OnDebug()
		{
			OnStart(null);
		}

		//Once the service begins running, do this...
		protected override void OnStart(string[] args)
		{
			//Start a thread to communicate with websocket
			//taskMonitor = new Thread(monitorPrograms);
			wsComms = new Thread(monitorComms);
			wsComms.Start();
			//taskMonitor.Start();

		}

		//Once the service has stopped, do this...
		protected override void OnStop()
		{
			//Stop threads
			//StopRequest.Set();
			//taskMonitor.Join();
			wsComms.Join();
		}

		//Method responsible for monitoring running processes and killing blacklisted ones
		/*
		private void monitorPrograms()
		{
			//Run a loop to continue running service every 15 seconds
			for (;;)
			{
				//Every 15 seconds, unless manually stopped
				if (StopRequest.WaitOne(15000)) return;

				//Start monitoring programs
				//Create array to hold string value of blacklisted programs in txt file
				string[] blacklisted = System.IO.File.ReadAllLines(AppDomain.CurrentDomain.BaseDirectory + "blacklisted.txt");

				//Get array of current programs running
				Process[] runningPrograms = Process.GetProcesses();

				//Can be more efficient, rewrite later
				//Compare current running program with blacklisted program name, if same then kill process
				foreach (Process program in runningPrograms)
				{
					//Loop through array of blacklisted program names
					for (int i = 0; i < blacklisted.Length; i++)
					{
						//NOTE: CASE SENSITIVE, may be an issue. NOW FIXED WITH TOUPPER
						//Figure out how user can enter program name correctly
						if (program.ProcessName.ToUpper().Contains(blacklisted[i].ToUpper()))
						{
							program.Kill();
						}
					}
				}
			}
		}
		*/

		//Method responsible of Communicating through web socket
		private void monitorComms()
		{
			//Connect to websocket
			using (var ws = new WebSocket("ws://localhost:8887"))
			{
				//On Connecting to server...
				ws.OnOpen += (sender, e) => ws.Send("Connected!");

				//Once we receive message from server, process the commands
				ws.OnMessage += (sender, e) =>
				{
					//Store the text received from server, process command
					String message = e.Data;
					processCommands(message, ws);
				};

				//ADD onERROR LATER

				ws.Connect();

				//Wait for commands
				while(true)
				{
					Thread.Sleep(1000);
				}

			}
		}

		//-------------------------------------------------//
		//					Commands					   //
		//-------------------------------------------------//

		//AddProgram command, used to add programs to blacklisted
		private void addProgram(String [] command, WebSocket socket)
		{

			//Append program name, stored in [1], to blacklisted file
			using (StreamWriter sw = File.AppendText(AppDomain.CurrentDomain.BaseDirectory + "blacklisted.txt"))
			{
				sw.WriteLine(command[1]);
			}
		}

		//RemoveProgram command, used to remove program from blacklist
		private void removeProgram(String[] command, WebSocket socket)
		{
			//Read all lines to be able to see if program is already blacklisted
			string[] bsPrograms = System.IO.File.ReadAllLines(AppDomain.CurrentDomain.BaseDirectory + "blacklisted.txt");
			//bool programFound = false;

			//Rewrite lines to file, if program we are tyring to remove is found don't write to file
			using (StreamWriter sw = new StreamWriter(AppDomain.CurrentDomain.BaseDirectory + "blacklisted.txt"))
			{
				foreach (string pName in bsPrograms)
				{
					//Use ToUpper() Method to not worry about case sensitivity
					if (!pName.ToUpper().Contains(command[1].ToUpper()))		//CHANGE THIS TO EQUALS IF CASE SENSITIVE
						sw.WriteLine(pName);
					//else
						//programFound = true;
				}
			}

			//Communicate with client, if program was or was not in blacklist
			/*
			if (programFound == true)
				socket.Send("Program removed from blacklist!");
			else
				socket.Send("Program not found in blacklist!");
			*/
		}


		//Method responsible for server command processing
		//Should add as a seperate header file
		private void processCommands(String message, WebSocket socket)
		{
			//Parse command received by whitespace. IDEALLY [0]= COMMAND, [1]= PROGRAM, TIME, ETC.
			String[] commands = message.Split(null);

			//Different possible commands
			switch (commands[0].ToUpper())
			{
				//AddProgram command, used to add programs to blacklisted
				//ADDPROGRAM [PROGRAM_NAME]
				case "ADDPROGRAM":
					if (commands[1] == "")
						socket.Send("Incorrect input: AddProgram [ProgramName]");
					else
					{
						//Temporary Workaround for bug, delete program from list if alread inside and then write again
						//WILL WORK ON WORKAROUND FOR THIS
						removeProgram(commands, socket);
						addProgram(commands, socket);
						socket.Send("Program added to blacklist!");
					}
					break;

				//RemoveProgram command, used to remove program from blacklist
				//REMOVEPROGRAM [PROGRAM_NAME]
				case "REMOVEPROGRAM":
					if (commands[1] == "")
						socket.Send("Incorrect Input: removeProgram [ProgramName]");
					else
					{
						removeProgram(commands, socket);
						socket.Send("Program removed from blacklist!");
					}
					break;

				//Allows program to remove or add time to programs 
				//TIME [PROGRAMNAME] [MINUTES]
				case "TIME":
					int msConversion = 60000;
					int time = Convert.ToInt32(commands[2]);
					string program = commands[1];

					//Command[1] should be program
					//Commands[2] should contain minutes
					if (time >= 0)
					{
						//If positive time, remove from blacklisted program...wait x minutes and then blacklist
						//FOR TESTING PURPOSES, NOT TECHNICALLY DONE WITH THIS FEATURE. BETTER TO HAVE AN APP CLASS
						removeProgram(commands, socket);
						socket.Send("Time alloted!");
						Thread.Sleep(time * msConversion);  //Wait, WILL HAVE TO USE MINUTES
						addProgram(commands, socket);
					}
					else
					{
						//If negative time, this is where APP CLASS WOULD COME IN HANDY TO SHOW CURRENT MINUTES REMAINING
						//For now, immediately blacklist program if negative time
						removeProgram(commands, socket);
					}

					socket.Send("Time Done!");
					break;

				//ADD MORE COMMANDS
				case "":
					//Do nothing if blank message sent
					socket.Send("Nothing Received!");
					break;

				default:
					socket.Send("Error! Command not found!");
					break;
				

			}
		}

	}
}
