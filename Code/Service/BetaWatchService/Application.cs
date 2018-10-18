using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BetaWatchService
{
	class Application
	{
		string _fileName;
		string _processName;
		string _iconPath;
		double _timeLimit;
		TimeSpan _runtime;
		DateTime _startTime;

		public Application(string processName, string fileName, DateTime startTime, double limit)
		{
			_processName = processName;
			_fileName = fileName;
			_runtime = TimeSpan.Zero;
			_startTime = startTime;
			_timeLimit = limit;
		}

		public string getProcessName()
		{ return this._processName; }

		public string getFileName()
		{ return this._fileName; }

		public string getIconPath()
		{ return this._iconPath; }

		public DateTime getStartTime()
		{ return this._startTime; }

		public double getLimit()
		{ return this._timeLimit; }

		//Keeps track how long the program has been running
		public TimeSpan getRuntime()
		{
			return this._runtime;
		}

        //Keeps track how long the program has been running
        public void subtractRuntime(int seconds)
        {
            this._runtime -= new TimeSpan(0, 0, seconds);
        }

        //Update the startTime until the program is started again
        public void updateStartTime()
		{
			this._startTime = DateTime.Now;
		}

		//Used to keep track how long program has been running
		public void updateRuntime()
		{
			DateTime now = DateTime.Now;
			TimeSpan calcRunTime = now - this._startTime;

			//Update the starttime to keep track of how long it has been running
			this._startTime = now;

			this._runtime = this._runtime + calcRunTime;
		}

		/*
		public void updateRunTime()
		{
			this._runtime = this._runtime.Add(new TimeSpan(0, 0, 1));
		}
		*/

	}
}
