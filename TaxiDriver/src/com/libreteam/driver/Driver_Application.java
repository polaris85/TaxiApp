package com.libreteam.driver;

import android.app.Application;

public class Driver_Application extends Application {
	public static boolean isActivityVisible() {
		return activityVisible;
	}  

	public static void activityResumed() {
	    activityVisible = true;
	}

	public static void activityStopped() {
		activityVisible = false;
	}
	
	private static boolean activityVisible;
}
