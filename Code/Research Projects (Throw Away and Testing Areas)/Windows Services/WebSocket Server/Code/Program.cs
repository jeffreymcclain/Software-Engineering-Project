using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;

namespace wsService
{
	static class Program
	{
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		static void Main()
		{
#if DEBUG

			//startWebSocketService test = new startWebSocketService();
			startWebSocketService test = new startWebSocketService();
			test.OnDebug();
			System.Threading.Thread.Sleep(System.Threading.Timeout.Infinite);

#else
			ServiceBase[] ServicesToRun;
			ServicesToRun = new ServiceBase[]
			{
				new startWebSocketService()
			};
			ServiceBase.Run(ServicesToRun);
#endif
		}
	}
}
