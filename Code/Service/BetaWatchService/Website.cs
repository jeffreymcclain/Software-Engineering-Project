using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BetaWatchService
{
	class Website
	{
		string _webName;
		string _webURL;
		double _timeLimit;
		string _startTime;
		string _endTime;

		public Website(string domain, string startTime)
		{

			_webURL = domain;
			_webName = returnWebName(domain);
			_startTime = startTime;
			_endTime = null;
			_timeLimit = setTimeLimit();

		}

		public string getWebName()
		{ return this._webName; }

		public string getWebURL()
		{ return this._webURL; }

		public double getTimeLimit()
		{ return this._timeLimit; }

		public DateTime getStartTime()
		{ return (convertToTime(this._startTime));}

		public DateTime getEndTime()
		{ return (convertToTime(this._endTime));}

		public void setStartTime(string time)
		{ this._startTime = time; }
	
		public void setEndTime(string time)
		{ this._endTime = time; }

		///////////////////////////////
		// Extra Class Methods, Testing
		////////////////////////////////

		//Convert the passed string into a DateTime object
		private DateTime convertToTime(string time)
		{
			string formatTime = time.Replace('*', ':');
			DateTime dateTime = DateTime.ParseExact(formatTime, "H:m:s", null, System.Globalization.DateTimeStyles.None);
			return dateTime;

		}

		//Set time limit
		private double setTimeLimit()
		{
			string limit = Monitor.allocatedTime("website", this._webURL);
			if (limit == "no limit set")
				return -600;		//Testing
			else
				return Convert.ToDouble(limit);
		}

		//Method used to calculate the runtime
		public TimeSpan getRuntime()
		{
			//Check if endtime has been set first
			if (this._endTime == null || this._endTime == "")
			{
				//Program is still running since no endtime was set
				DateTime now = DateTime.Now;
				TimeSpan runtime = now - getStartTime();
				return runtime;
			}
			else
			{
				//Endtime must have been set so calculate using that
				return getEndTime() - getStartTime();
			}
		}

		//Remaining Time
		public double getRemainingTime()
		{
			if (this._timeLimit == -600)
				return -600;
			else
			{
				return (this._timeLimit - getRuntime().TotalSeconds);
			}
		}

		//Method used to remove extra characters from the domain in order to get just the website name
		public string returnWebName(string domain)
		{
			string webName = domain.Replace("www.", "").Replace(".com", "").Replace(".net", "").Replace(".org", "");

			return webName;
		}

	}
}
