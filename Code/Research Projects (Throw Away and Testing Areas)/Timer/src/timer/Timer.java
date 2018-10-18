/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timer;

import java.util.Scanner;

public class Timer {
	private int hours, minutes, seconds;
	private static int numTimers = 0; //incremented in constructor, keeps track of the number of timers
	public final int ID; ////set to numTimers, ensuring each timer has a unique ID

	public Timer(int hours, int minutes, int seconds) {
		setHours(hours);
		setMinutes(minutes);
		setSeconds(seconds);
		numTimers++;
		ID = numTimers;
	}

	public Timer(int hours, int minutes) {
		this(hours, minutes, 0);
	}

	public Timer(int hours) {
		this(hours, 0);
	}

	public Timer() {
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

	//decrementers
	void decrementHours() {
		if (this.hours == 0)
			this.hours = 0;
		else
			this.hours--;
	}

	void decrementMinutes() {
		if (this.minutes == 0) {
			this.minutes = 59;
			decrementHours();
		} else {
			this.minutes--;
		}
	}

	void decrementSeconds() {
		if (this.seconds == 0) {
			this.seconds = 59;
			decrementMinutes();
		} else {
			this.seconds--;
		}
	}

	void incrementMinutes(int min) { //call this method to reward child when they answer SAT questions correctly
		if (min > 0 && min < 59)
			minutes += min;
		if (minutes > 59)  //maximum number of minutes is 59, can also be set to a different value or a variable
			minutes = 59;
	}

	boolean timesUp() { //returns true when the timer expires (e.g. the child uses up their play time)
		if (hours==0 && minutes==0 && seconds==0)
			return true;
		else
			return false;
	}

	public String toString() { //formats timer object to print in the form "h:mm:ss"
		String minuteString = "" + minutes;
		if (minutes < 10)
			minuteString = "0" + minuteString;
		String secondString = "" + seconds;
		if (seconds < 10)
			secondString = "0" + secondString;
		return hours + ":" + minuteString + ":" + secondString;
	}

	public static void main (String[] args) {
		Timer m = new Timer(0, 15, 5); //creates timer with initial value of 15 minutes, 5 seconds
		boolean x;

		for (int i = 0; i < 5000; i++) { //decrements and prints timer every second
			try {
				System.out.println(m);
				Thread.sleep(1000);
				m.decrementSeconds();
				x = m.timesUp();
				if (x == true) { //if the timer reaches zero (0:00:00)
					System.out.println("Back to work!");
					//activate "work mode"
					break;
				}
			}
			catch (Exception e) {
			}
		}
		//m.setHours(10);
		//System.out.println(m.getHours());
		//System.out.println(m.getMinutes());
		//System.out.println(m.getSeconds());
	}
}

