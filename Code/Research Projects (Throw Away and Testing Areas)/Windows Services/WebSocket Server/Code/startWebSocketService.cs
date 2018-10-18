using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.ServiceProcess;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using WebSocketSharp;
using WebSocketSharp.Server;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json;

namespace wsService
{
	public partial class startWebSocketService : ServiceBase
	{
		//TESTING
		private Thread _wsServer;
		private Thread _bsMonitor;
		private WebSocketServer _testServer;
		private ManualResetEvent _shutdownEvent = new ManualResetEvent(false);

		public startWebSocketService()
		{
			InitializeComponent();
		}

		//Used for debugging purposes
		public void OnDebug()
		{
			OnStart(null);
			
		}

		//Once the service starts, do this
		protected override void OnStart(string[] args)
		{
			//Start a thread for the websocket servers
			_wsServer = new Thread(startWebsocketServer);
			_wsServer.IsBackground = true;
			_wsServer.Start();
		}

		//Once the service stops, do this
		protected override void OnStop()
		{
			//If websocket is not working correctly, abort both threads
			_shutdownEvent.Set();
			if (!_wsServer.Join(3000))
			{
				_wsServer.Abort();
			}

			_testServer.Stop();
		}

		//Responsible for starting websocket server for communications
		private void startWebsocketServer()
		{
			//TESTING Server 
			_testServer = new WebSocketServer(4649);

			//Default, No Path to Service
			_testServer.AddWebSocketService<myWS_Server>("/test");
			_testServer.Start();

			//Continuosly Run
			while (!_shutdownEvent.WaitOne(0))
			{
				Thread.Sleep(1000);
			}
		}


	}
}
