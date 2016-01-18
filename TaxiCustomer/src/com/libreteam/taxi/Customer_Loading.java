package com.libreteam.taxi;



import com.libreteam.taxi.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class Customer_Loading extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_loading);
		
		Thread welcomeThread = new Thread() {
	    	@Override
	        public void run() {
	    		try {
	    			super.run();
	    			sleep(2000);  
	    		} catch (Exception e) {
	    		} finally {
	    			Intent i = new Intent(Customer_Loading.this, Customer_Fragment_Activity.class);
	    			startActivity(i);
		        	finish();
	    		}
	    	}
		};
		welcomeThread.start();
	}
}
