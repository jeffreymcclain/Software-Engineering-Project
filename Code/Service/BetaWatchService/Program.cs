using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading.Tasks;

namespace BetaWatchService
{
	static class Program
	{

        public static bool dontShutDown = true;
		/// <summary>
		/// The main entry point for the application.
		/// </summary>
		static void Main()
		{
#if DEBUG
			BW_Service test = new BW_Service();
			test.OnDebug();
			System.Threading.Thread.Sleep(System.Threading.Timeout.Infinite);
#else

			ServiceBase[] ServicesToRun;
			ServicesToRun = new ServiceBase[]
			{
				new BW_Service()
			};
			ServiceBase.Run(ServicesToRun);
#endif
		}
	}
}
