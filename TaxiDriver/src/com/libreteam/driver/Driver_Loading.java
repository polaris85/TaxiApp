package com.libreteam.driver;



import com.libreteam.driver.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class Driver_Loading extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_loading);
		
		Thread welcomeThread = new Thread() {
	            @Override
	            public void run() {
	                try {
	                    super.run();
	                    sleep(2000);  
	                } catch (Exception e) {

	                } finally {
                        {
		                    Intent i = new Intent(Driver_Loading.this,
		                            Driver_Fragment_Activity.class);
		                    startActivity(i);
		                    finish();
                        }
	                }
	            }
	        };
	        welcomeThread.start();
		}
}
