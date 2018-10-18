package clock;

import java.util.Scanner;
import java.time.ZonedDateTime;
//import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Clock {
	private int hours, minutes, seconds;
	private static int numClocks = 0; //incremented in constructor, keeps track of the number of clocks
	public final int ID; //set to numClocks, ensuring each clock has a unique ID

	public Clock(int hours, int minutes, int seconds) {
		setHours(hours);
		setMinutes(minutes);
		setSeconds(seconds);
		numClocks++;
		ID = numClocks;
	}

	public Clock(int hours, int minutes) {
		this(hours, minutes, 0);
	}

	public Clock(int hours) {
		this(hours, 0);
	}

	public Clock() {
		this(0); //calls above contructor w/ 1 param, which calls constructor w/ 2 params, which calls top contructor w/ 3 params
	}

	//setters
	void setHours(int hours) {
		if (hours > 23)
			hours = 23; //or hours %= 24;
		if (hours < 0)
			hours = 0;
		this.hours = hours; //could also just change variable name to h, then do hours = h;
	}

	void setMinutes(int minutes){
		if (minutes > 59)
			minutes = 59;
		if (minutes < 0)
			minutes = 0;
		this.minutes = minutes;
	}

	void setSeconds(int seconds) {
		if (seconds > 59)
			seconds = 59;
		if (seconds < 0)
			seconds = 0;
		this.seconds = seconds;
	}

	void incrementHours() {
		if (this.hours == 23)
			this.hours = 0;
		else
			this.hours++;
	}

	void incrementMinutes() {
		if (this.minutes == 59) {
			this.minutes = 0;
			incrementHours();
		} else {
			this.minutes++;
		}
	}

	void incrementSeconds() {
		if (this.seconds == 59) {
			this.seconds = 0;
			incrementMinutes();
		} else {
			this.seconds++;
		}
	}

	//getters
	int getHours() {
		return hours;
	}

	int getMinutes() {
		return minutes;
	}

	int getSeconds() {
		return seconds;
	}

	void setToCurrentTime() { //sets clock object to current time
		DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH");
		hours = Integer.parseInt(ZonedDateTime.now().format(FORMATTER));
		FORMATTER = DateTimeFormatter.ofPattern("mm");
		minutes = Integer.parseInt(ZonedDateTime.now().format(FORMATTER));
		FORMATTER = DateTimeFormatter.ofPattern("ss");
		seconds = Integer.parseInt(ZonedDateTime.now().format(FORMATTER));
	}

	/*void setWorkHours(int limit1, int limit2) {
		this.limit1 = limit1;
		this.limit2 = limit2;
	}*/

	void enforceWorkHours(int hours1, int hours2) { //time range from hours1 to hours2 (24 hour time) where "fun" list is banned
		if (hours1 < hours2) { //e.g. 20-23 (8pm - 11pm) or 1-7 (1am - 7am)
			if (hours >= hours1 && hours < hours2) {
				System.out.println("You can resume playing at " + hours2);
				//activate other program that closes specified applications and restricts websites
			}
		} else if (hours1 == hours2) {
			System.out.println("Error: time interval cannot be the same number twice");
		} else { //e.g. 22-7 (10pm to 7am)
			if (hours >= hours1 || hours < hours2) {
				System.out.println("You can resume playing at " + hours2);
				//activate other program that closes specified applications and restricts websites
			}
		}
	}

	public String toString() { //formats clock object to print in the form "hh:mm:ss AM/PM"
		boolean am = false;
		String minuteString = "" + minutes;
		if (minutes < 10)
			minuteString = "0" + minuteString;
		String secondString = "" + seconds;
		if (seconds < 10)
			secondString = "0" + secondString;
		String hourString;
		if (hours <= 11) {
			am = true;
			if (hours == 0) {
				hourString = "12";
			} else {
				hourString = "" + hours;
			}
		} else if (hours == 12) {
			hourString = "" + hours;
		} else {
			hourString = "" + (hours-12);
		}
			return hourString + ":" + minuteString + ":" + secondString + (am ? " AM" : " PM");
	}

	public static void main (String[] args) {
		Clock m = new Clock(14, 5, 58); //initializes clock m to 2:05:58 PM
		m.setToCurrentTime(); //sets clock to current time

		for (int i = 0; i < 5000; i++) { //increments and prints clock every second
			try {
				System.out.println(m);
				Thread.sleep(1000);
				m.incrementSeconds();
				//m.enforceWorkHours(20, 23); //does not allow "fun" mode from 8pm - 11pm. numbers can be changed to variables
			}
			catch (Exception e) {
			}
		}

		//m.setHours(10);
		//m.setMinutes(38);
		//m.setSeconds(49);
		//System.out.println(m.getHours());
		//System.out.println(m.getMinutes());
		//System.out.println(m.getSeconds());
	}
}


