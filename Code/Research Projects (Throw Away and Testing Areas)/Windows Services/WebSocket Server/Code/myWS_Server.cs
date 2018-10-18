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

//This will basically be a service (behavior) that is provided by the WebSocket Server
namespace wsService
{
	class myWS_Server : WebSocketBehavior
	{
		//Class Member Variables
		private String _clientName;
		private static int _cNumber;        //Used for client number
		private String _serviceName;

		/*------------------------------/
		 *     Class Constructors
		 *------------------------------*/
		public myWS_Server() : this (null)
		{
			//Do Nothing
		}

		public myWS_Server(String sName)
		{
			//Setting up the server service name, just for testing. If value is empty, default will be "Default
			_serviceName = !_serviceName.IsNullOrEmpty() ? _serviceName : "Default:";

		}

		/*------------------------------/
		 *        Class Methods
		 *------------------------------*/

		//Setup Client Name
		private String assignClientName()
		{
			//Modify this later, to recieve name of module accessing this server, CHANGE
			var name = "Client";

			return name + getNumber();
		}

		//Keep track of client Number
		private static int getNumber()
		{
			//Increment the clientNumbers and return it
			return Interlocked.Increment(ref _cNumber);
		}

		//OnOpen, websocket server will do this. This is basically what happens when a new client connects
		protected override void OnOpen()
		{
			//CHANGE
			//For now, it will just assign the client name and send as a message
			_clientName = assignClientName();
			Send(String.Format("Hello, your name is {0}", _clientName));
		}

		//OnClose, websocket server will do this. When a client disconnects...
		protected override void OnClose(CloseEventArgs e)
		{
			//For now, just send message that someone disconnected. Broadcast to all
			Sessions.Broadcast( String.Format( "{0} disconnected...", _clientName ) );
		}

		//OnMessage, websocket server will do this
		/*IMPORTANT: this method is what is called when a client sends a message to the server*/
		protected override void OnMessage(MessageEventArgs e)
		{
			//Process the commands that was received by the server
			processCommands(e);

			//Parse Command, this will vary once we setup communications protocol. CHANGE
			//String[] commands = _commandRecieved.Split(':');

			//Call Method that will process the Commands

			//For Testing
			//Send("Hello");
			
		}

		/*------------------------------/
		 *        Service Commands
		 *------------------------------*/
		 
		//This Method is responsible for processing the Received messages from the clients
		//CHANGE, change once communications protocol is setup for sure
		private void processCommands(MessageEventArgs e)
		{
			//Split up the command, CHANGE once we decide on what character to split by
			/*
			 * commandReceived[0] = Name of Client
			 * commandReceived[1] = Request/Command
			 * commandReceived[2] = Arguments for command
			 */
			String [] commandReceived = e.Data.Split(':');

			//Use Switch Statement to decide what to run
			//CHANGE, ADD NEW COMMANDS ONCE WE DECIDE
			switch (commandReceived[1].ToUpper())
			{
				//Requesting BlackList
				case "REQUEST_BLACKLIST":
					requestBlacklist();
					break;

				//Adding Programs to Blacklist
				case "REQUEST_ADDTOBLACKLIST":
					addToBlacklist(commandReceived);
					break;

				//Removing Programs from blacklist
				//CHANGE, NOT WORKING PROPERLY
				case "REQUEST_REMOVEFROMBLACKLIST":
					removeFromBlacklist(commandReceived);
					break;

				//Removing Programs from Blacklist

				//Adding or Removing Time

				//Default case, send error Message
				default:
					Send("Error:Command not Found!");
					break;
			}


		}

		//Method that handles sending blacklist to client
		private void requestBlacklist()
		{
			//TESTING
			//Setup connection with firebase, look at blacklist JSON
			var fbRequest = (HttpWebRequest)WebRequest.Create("https://betawatch-e87e2.firebaseio.com/blacklist/programs.json");
			fbRequest.ContentType = "application/json: charset=utf-8";
			var fbResponse = fbRequest.GetResponse() as HttpWebResponse;

			using (var sr = new StreamReader(fbResponse.GetResponseStream()))
			{
				string programs = sr.ReadToEnd();
				Send(programs);
			}
		
		}

		//Method that handles adding programs to the Blacklist
		//private async Task addToBlacklist( String [] commandReceived )
		private void addToBlacklist(String[] commandReceived)
		{
			//Error Handling
			if (commandReceived[2] == null || commandReceived[2] == "")
				Send(String.Format("REQUEST_ADDTOBLACKLIST:{0}:Failed!", commandReceived[2]));
			else
			{	
				//Setup connection with firebase, look at blacklist JSON
				var fbRequest = (HttpWebRequest)WebRequest.Create("https://betawatch-e87e2.firebaseio.com/.json");
				fbRequest.ContentType = "application/json: charset=utf-8";
				var fbResponse = fbRequest.GetResponse() as HttpWebResponse;

				//Store current JSON file
				string currentPrograms; 
				using (var sr = new StreamReader(fbResponse.GetResponseStream()))
				{
					currentPrograms = sr.ReadToEnd();
				}

				//Create a JObject and Jarray to be able to add programs
				JObject json = JObject.Parse(currentPrograms);
				JObject blacklist = (JObject) json["blacklist"];
				JArray program = (JArray)blacklist["programs"];

				//Add program to list of programs
				program.Add(commandReceived[2].ToLower());

				//Request connection, this time to update the file
				fbRequest = (HttpWebRequest)WebRequest.Create("https://betawatch-e87e2.firebaseio.com/.json");
				fbRequest.Method = "PATCH";
				fbRequest.ContentType = "application/json: charset=utf-8";

				//create buffer to be able to send JSON contents
				var buffer = Encoding.UTF8.GetBytes(json.ToString());
				fbRequest.ContentLength = buffer.Length;
				fbRequest.GetRequestStream().Write(buffer, 0, buffer.Length);

				//Get Response from connection
				fbResponse = fbRequest.GetResponse() as HttpWebResponse;
				var jsonResponse = (new StreamReader(fbResponse.GetResponseStream())).ReadToEnd();

				//Send response to client
				Send(String.Format("REQUEST_ADDTOBLACKLIST:{0}:Success!", commandReceived[2]));
			}
		}
		
		//CHANGE, NOT WORKING PROPERLY
		//Method that handles removing programs from the Blacklist
		private void removeFromBlacklist( String [] commandReceived )
		{
			//Error Handling
			if (commandReceived[2] == null || commandReceived[2] == "")
				Send(String.Format("REQUEST_REMOVEFROMBLACKLIST:{0}:Failed!", commandReceived[2]));
			else
			{
				//Setup connection with firebase, look at blacklist JSON
				var fbRequest = (HttpWebRequest)WebRequest.Create("https://betawatch-e87e2.firebaseio.com/.json");
				fbRequest.ContentType = "application/json: charset=utf-8";
				var fbResponse = fbRequest.GetResponse() as HttpWebResponse;

				//Store current JSON file
				string currentPrograms;
				using (var sr = new StreamReader(fbResponse.GetResponseStream()))
				{
					currentPrograms = sr.ReadToEnd();
					Send(currentPrograms);
				}

				//Create a JObject and Jarray to be able to add programs
				JObject json = JObject.Parse(currentPrograms);
				JObject blacklist = (JObject)json["blacklist"];
				JArray program = (JArray)blacklist["programs"];

				//Remove program from list of programs
				program.RemoveAt(0); //(commandReceived[2].ToLower());

				//Request connection, this time to update the file
				fbRequest = (HttpWebRequest)WebRequest.Create("https://betawatch-e87e2.firebaseio.com/.json");
				fbRequest.Method = "PATCH";
				fbRequest.ContentType = "application/json: charset=utf-8";

				//create buffer to be able to send JSON contents
				var buffer = Encoding.UTF8.GetBytes(json.ToString());
				fbRequest.ContentLength = buffer.Length;
				fbRequest.GetRequestStream().Write(buffer, 0, buffer.Length);

				//Get Response from connection
				fbResponse = fbRequest.GetResponse() as HttpWebResponse;
				var jsonResponse = (new StreamReader(fbResponse.GetResponseStream())).ReadToEnd();

				//Send response to client
				Send(String.Format("REQUEST_FROMFROMBLACKLIST:{0}:Success!", commandReceived[2]));
			}
		}



	}
}
