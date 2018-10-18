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

/// <summary>
/// This will basically be a service (behavior) that is provided by the WebSocket Server
/// Takes care of what to do when communication occurs
/// </summary>
namespace BetaWatchService
{
	/// <summary>
	/// Responsible for setting up the websocket server for communication
	/// </summary>
	public static class startCommunication
	{
		public static WebSocketServer _wsServer;

		public static void startWebsocketServer(object state)
		{
			//Change Port and location later if neccessary
			_wsServer = new WebSocketServer(4649);

			//Create a path to the websocket server for communication, then start the server
			_wsServer.AddWebSocketService<Communicate>("/test");
			_wsServer.Start();

			//Continuosly Run, but check if the service has been stopped
			while (!BW_Service._stopping)
			{
				//Need at least one line of code here to be able to run this loop
				Thread.Sleep(1000);
			}

			BW_Service._stoppedEvent.Set();
		}
		//END CLASS
	}

	/// <summary>
	/// Responsible for how the websocket server behaves
	/// </summary>
	class Communicate : WebSocketBehavior
	{
		//Class member variables
		private string _serviceName;

		/*/////////////////////////////////////
		/ 
		/			Constructors
		/
		/////////////////////////////////////// */
		public Communicate() : this(null)
		{
			//Do Nothing
		}

		public Communicate(string sName)
		{
			//Setting up the server service name, just for testing. If value is empty, default will be "Default
			_serviceName = !_serviceName.IsNullOrEmpty() ? _serviceName : "Default:";
		}

		/*/////////////////////////////////////
		/ 
		/			Class Methods
		/
		/////////////////////////////////////// */

		//OnOpen, websocket server will do this. This is basically what happens when a new client connects
		protected override void OnOpen()
		{
			//For now, just send a confirmation that connection was made
			Send("Connected");
		}

		//OnClose, websocket server will do this. When a client disconnects...
		protected override void OnClose(CloseEventArgs e)
		{
			//For now, just send message that someone disconnected. Broadcast to all
			//DO NOTHING IN HERE AT THE MOMENT
			//Sessions.Broadcast("Disconnected");
		}

		//OnMessage, websocket server will do this
		/*IMPORTANT: this method is what is called when a client sends a message to the server*/
		protected override void OnMessage(MessageEventArgs e)
		{
			//Process the commands that was received by the server
			processCommands(e);
		}


		/*////////////////////////////////////////////////
		/ 
		/			Service Command Processing
		/
		/////////////////////////////////////////////////*/

		//This Method is responsible for processing the Received messages from the clients
		//CHANGE, change once communications protocol is setup for sure
		private void processCommands(MessageEventArgs e)
		{
			//Split up the command, CHANGE once we decide on what character to split by
			/*
			 * commandReceived[0] = Requester
			 * commandReceived[1] = Request/Command
			 * commandReceived[2] = RequestNumber
			 * commandReceived[3] = Parameters
			 */

			String[] commandRecieved = e.Data.Split(':');
			string response;

			//Use a switch statement that will decide what will happen
			switch (commandRecieved[1].ToLower())
			{
				//Query the current security level set
				case "request_securitylevel":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions._securityLevel));
					break;

				//Change the security level
				case "set_securitylevel":
					response = FireBaseFunctions.changeSecurityLevel(commandRecieved[3]);
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				/////////////////////////////////////
				// Requesting lists
				/////////////////////////////////////
				case "request_blacklistprograms":
					response = FireBaseFunctions.requestList("black", "programs");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_blacklistwebsites":
					response = FireBaseFunctions.requestList("black", "websites");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_whitelistprograms":
					response = FireBaseFunctions.requestList("white", "programs");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_whitelistwebsites":
					response = FireBaseFunctions.requestList("white", "websites");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				/////////////////////////////////////
				// Requesting removal from lists
				/////////////////////////////////////
				case "request_removefromblacklistprograms":
					response = FireBaseFunctions.removeFromList(commandRecieved[3].ToLower(), "black", "programs");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_removefromblacklistwebsites":
					response = FireBaseFunctions.removeFromList(commandRecieved[3].ToLower(), "black", "websites");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_removefromwhitelistprograms":
					response = FireBaseFunctions.removeFromList(commandRecieved[3].ToLower(), "white", "programs");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_removefromwhitelistwebsites":
					response = FireBaseFunctions.removeFromList(commandRecieved[3].ToLower(), "white", "websites");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;


				/////////////////////////////////////
				// Requesting add to lists
				/////////////////////////////////////
				case "request_addtoblacklistprograms":
					response = FireBaseFunctions.addToList(commandRecieved[3].ToLower(), 0, "black", "programs");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_addtoblacklistwebsites":
					response = FireBaseFunctions.addToList(commandRecieved[3].ToLower(), 0, "black", "websites");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_addtowhitelistprograms":
                    String[] programToAdd = commandRecieved[3].Split(',');
                    response = FireBaseFunctions.addToList(programToAdd[0].ToLower(), Int32.Parse(programToAdd[1]), "white", "programs");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				case "request_addtowhitelistwebsites":
                    String[] websiteToAdd = commandRecieved[3].Split(',');
                    response = FireBaseFunctions.addToList(websiteToAdd[0].ToLower(), Int32.Parse(websiteToAdd[1]), "white", "websites");
					Send(String.Format("{0}:{1}", e.Data, response));
					break;

				/////////////////////////////////////
				// Monitoring  Commands
				/////////////////////////////////////
				case "request_programisrunning":
					Send(String.Format("{0}:{1}", e.Data, Monitor.isRunning(commandRecieved[3])));
					break;

				case "request_timespentprogram":
					Send(String.Format("{0}:{1}", e.Data, Monitor.programRuntime(commandRecieved[3])));
					break;

				case "request_timeremainingprogram":
					Send(String.Format("{0}:{1}", e.Data, Monitor.programRemainingTime(commandRecieved[3])));
					break;

                case "request_addTime":
                    Send(String.Format("{0}:{1}", e.Data, Monitor.programAddTime(commandRecieved[3].Split(',')[0], int.Parse(commandRecieved[3].Split(',')[1]))));
                    break;

                case "request_totaltimeallocatedprogram":
					Send(String.Format("{0}:{1}", e.Data, Monitor.allocatedTime("program",commandRecieved[3])));
					break;

				case "request_totaltimeallocatedwebsite":
					Send(String.Format("{0}:{1}", e.Data, Monitor.allocatedTime("website", commandRecieved[3])));
					break;

				case "request_timeremainingwebsite":
					Send(String.Format("{0}:{1}", e.Data, Monitor.websiteRemainingTime(commandRecieved[3])));
					break;

				case "request_timespentwebsite":
					Send(String.Format("{0}:{1}", e.Data, Monitor.websiteRuntime(commandRecieved[3])));
					break;

				/////////////////////////////////////
				// Parent Settings Commands
				/////////////////////////////////////
				case "request_notificationcanemail":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions.requestSettings("can_email")));
					break;

				case "request_notificationcansms":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions.requestSettings("can_sms")));
					break;

				case "request_notificationemailaddress":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions.requestSettings("parent_email")));
					break;

				case "request_notificationsmsnumber":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions.requestSettings("parent_sms")));
					break;

				case "set_notificationemailbool":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions.updateSettings("can_email", commandRecieved[3])));
					break;

				case "set_notificationsmsbool":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions.updateSettings("can_sms", commandRecieved[3])));
					break;

				case "set_notificationsms":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions.updateSettings("sms", commandRecieved[3])));
					break;

				case "set_notificationemail":
					Send(String.Format("{0}:{1}", e.Data, FireBaseFunctions.updateSettings("email", commandRecieved[3])));
					break;

				/////////////////////////////////////////////////////////////
				// Extensions Recieving data, update dict in monitoring class
				/////////////////////////////////////////////////////////////
				case "logstarttime":
					Monitor.addToWebsiteList(commandRecieved[4], commandRecieved[3]);
					//Send("ok");		//For Testing
					break;

				case "logendtime":
					Monitor.websiteListEndTime(commandRecieved[4], commandRecieved[3]);
					break;

				case "request_programlist":
					response = String.Join(",", new String[] { "Spotify", "MinecraftLauncher", "Chrome", "Firefox", "Calculator", "Notepad" });
					Send(String.Format("{0}:{1}", e.Data, response ));
					break;

                case "request_programsused":
                    response = String.Join(",", Monitor.getProgramsRan());
                    Send(String.Format("{0}:{1}", e.Data, response));
                    break;

                case "request_websitesvisited":
                    response = String.Join(",", Monitor.getWebsitesVisited());
                    Send(String.Format("{0}:{1}", e.Data, response));
                    break;

                /////////////////////////////////////////////////////////////
                // updated the total computer time
                /////////////////////////////////////////////////////////////

                case "request_totalcomputertime":
                    response = FireBaseFunctions.queryComputerTime().ToString();
                    Send(String.Format("{0}:{1}", e.Data, response));
                    break;
                case "request_remainingcomputertime":
                    response = Monitor.getRemainingComputerTime().ToString();
                    Send(String.Format("{0}:{1}", e.Data, response));
                    break;
                case "set_totalcomputertime":
                    response = FireBaseFunctions.changeComputerTime(int.Parse(commandRecieved[3]));
                    Send(String.Format("{0}:{1}", e.Data, response));
                    break;


                //Command not found, or incorrect format
                default:
					Send(String.Format("{0}:{1}", e.Data, "command not found"));
					break;


			}

		}





	}
}
