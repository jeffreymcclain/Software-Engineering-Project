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

namespace taskMonitor
{
	public partial class Service1 : ServiceBase
	{

		//Thread Class used to continously listen to running processes
		Thread listenTask;
		AutoResetEvent StopRequest = new AutoResetEvent(false);

		public Service1()
		{
			InitializeComponent();
		}
		
		//For debuging purposes
		public void OnDebug()
		{
			OnStart(null);
		}

		protected override void OnStart(string[] args)
		{
			//Start a thread to monitor activity
			listenTask = new Thread(monitorPrograms);
			listenTask.Start();
		}

		protected override void OnStop()
		{
			//Stop listening thread
			StopRequest.Set();
			listenTask.Join();

		}

		private void monitorPrograms()
		{
			//Run a loop to continue running service every 15 seconds
			for(;;)
			{
				//Every 25 seconds, unless manually stopped
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
						//NOTE: CASE SENSITIVE, may be an issue. 
						//Figure out how user can enter program name correctly
						if (program.ProcessName.Contains(blacklisted[i]))
						{
							program.Kill();
						}
					}
				}
			}

			
		}



	}
}
