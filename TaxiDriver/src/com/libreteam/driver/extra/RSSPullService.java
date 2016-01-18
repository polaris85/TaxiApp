package com.libreteam.driver.extra;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.libreteam.driver.Taxi_System;

public class RSSPullService extends Service {
	private Handler handler;
	private Runnable runable;	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
    public void onCreate() {	    
		handler = new Handler();
		
	    runable = new Runnable() {
	    	@Override 
	    	public void run() {
	    		if ((Taxi_System.connectionStatus(getBaseContext())) && (!Socket.isConnected()))	{	    			
	    			Socket.socketDidConnect(getBaseContext());
	    		}	    		
	    		handler.postDelayed(this, 10000);
	    	} 
	    };
	    
	    handler.postDelayed(runable, 10000); 		
    }
	
	@Override
	public void onDestroy() {
		handler.removeCallbacks(runable);
	}
}
