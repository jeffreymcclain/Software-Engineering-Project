using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using WebSocketSharp.Server;

namespace BetaWatchService
{
	public partial class BW_Service : ServiceBase
	{
		//Service variables
		public static bool _stopping;
		public static ManualResetEvent _stoppedEvent;

		//Class constructor
		public BW_Service()
		{
			InitializeComponent();

			_stopping = false;
			_stoppedEvent = new ManualResetEvent(false);
		}

		//Used for debugging purposes
		public void OnDebug()
		{
			OnStart(null);
		}

		//Once the service starts, do this
		protected override void OnStart(string[] args)
		{
			//Start a thread for the websocket server. ADD BACK IN
			ThreadPool.QueueUserWorkItem( new WaitCallback( startCommunication.startWebsocketServer) );

			//Start a thread for the Whitelist Monitor
			ThreadPool.QueueUserWorkItem(new WaitCallback(Monitor.monitorWhitelist));

			//Start a thread for the Blacklist Monitor
			ThreadPool.QueueUserWorkItem(new WaitCallback(Monitor.monitorBlacklist));

			//Start a thread for the Blacklist Monitor
			ThreadPool.QueueUserWorkItem(new WaitCallback(Monitor.monitorRuntimes));

			//END OnStart Method
		}

		//Once the service stops, do this
		protected override void OnStop()
		{
			//Signal that the service is stopping, and wait for execution of threads to finish
			_stopping = true;
			_stoppedEvent.WaitOne();

			//Stop the websocket server. ADD BACK IN
			startCommunication._wsServer.Stop();

		//END OnStop Method
		}
	}
}
