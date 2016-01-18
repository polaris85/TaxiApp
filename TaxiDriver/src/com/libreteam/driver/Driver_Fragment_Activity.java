package com.libreteam.driver;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.libreteam.driver.Driver_Information.DriverMenu;
import com.libreteam.driver.R;
import com.libreteam.driver.extra.RSSPullService;
import com.libreteam.driver.extra.Socket;
import com.libreteam.driver.extra.Taxi_Constants;
import com.libreteam.driver.extra.Socket.SocketRespond;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class Driver_Fragment_Activity extends FragmentActivity implements DriverMenu {
	private final String ACTIVITY_LOSS = "ACTIVITY_LOSS";
	private final String RIDE_OFFER_DATA = "RIDE_OFFER_DATA";
	private final String NOTIFICATION = "NOTIFICATION";
	private final String NOTIFICATION_ID = "NOTIFICATION_ID";
//	private final int RQS_GooglePlayServices = 1;
    private Context context;
//	private Button btMenu;
//	private TextView tvTitle;
    public boolean isMenu, isBackable, isInMenu;
	private String ride_id, customer_token, address, latlng, d_address, d_latlng;
	public Button button;
    
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		isMenu = false;
		isInMenu = false;
		context = getApplicationContext();
		
		setContentView(this.getLayoutInflater().inflate(R.layout.main_fragment_activity, null));
		Taxi_System.setContent((FrameLayout)findViewById(R.id.dummy), getApplicationContext(), 1, (float)0.07);
		Taxi_System.setContent((FrameLayout)findViewById(R.id.banner), getApplicationContext(), 1, (float)0.05);
		
		button = (Button)findViewById(R.id.menu);
		button.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.07);
		button.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
			    if(!isMenu)
			    	getSupportFragmentManager()
			    	.beginTransaction()
			    	.setCustomAnimations(R.anim.abc_fade_in, 0)
			    	.replace(R.id.menuframe, new Driver_Information(),"menu")
			    	.disallowAddToBackStack()
			    	.commitAllowingStateLoss();
			    else			    	
			    	getSupportFragmentManager()
			    	.beginTransaction()
			    	.setCustomAnimations(0, R.anim.abc_fade_out)
			    	.remove(getSupportFragmentManager()
			    	.findFragmentByTag("menu"))
			    	.disallowAddToBackStack()
			    	.commitAllowingStateLoss();
			    isMenu =! isMenu;
			}
		});
		
//		didAddFragment(new Driver_Login() , "code", new String[]{}, true);
		
		if(!Taxi_System.connectionStatus(context)) { 
			Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));  
			return;
		}
		Taxi_System.checkGPS(getApplicationContext(), this);
		
		setActivity();

		Socket.respond = new SocketRespond() {
			@Override
			public void respondData(String string) {
				Taxi_System.testLog("DRIVER-MAIN" + string);
				if(Taxi_System.getSystem(context, "type").equalsIgnoreCase("driver"))
				{
					try {
						didReceiveCode(Integer.valueOf(new JSONObject(string).getString("code").toString()),string);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		};
		setLanguage();
//		setActivity();
	}
	
	private void setLanguage() {
		String pos = Taxi_System.getSystem(this, Taxi_Constants.SPINNER_SELECTED);
		if(!pos.equals("")) {
			int position = Integer.parseInt(pos);
			Configuration configuration = new Configuration();
			if(position==0)
				configuration.locale = new Locale("en_US");
			else if(position==1)
				configuration.locale = new Locale("sl_SI");
			getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
		}
	}
	
	private void setActivity() {
		// new code
		String notification = getIntent().getStringExtra(NOTIFICATION);
		if(notification!=null) {
			if(notification.equals("YES")) {
				if(Taxi_System.getSystem(context, ACTIVITY_LOSS).toString().equals("YES")) {
					String string = Taxi_System.getSystem(context, RIDE_OFFER_DATA);
					if(string.length()>0) 
					{
						try {
							didGetRideOffer(parseJson(string));
							Taxi_System.addSystem(context, RIDE_OFFER_DATA, "");
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					Taxi_System.addSystem(context, ACTIVITY_LOSS, "NO");
				}
			}
		} else {//// 
			if(!Taxi_System.getSystem(context, "token").equalsIgnoreCase("") && Taxi_System.getSystem(context, "token") != null)
			{
				Taxi_System.addSystem(context, ACTIVITY_LOSS, "NO");
				Taxi_System.addSystem(context, RIDE_OFFER_DATA, "");
				didAddFragment(new Driver_Status() , "code", new String[]{"notShowMap"}, true);
				button.setVisibility(View.VISIBLE);
			}
			else {
				button.setVisibility(View.GONE);
				didAddFragment(new Driver_Login() , "code", new String[]{}, true);
			}
		}
	}
	
	public void removeNotification() {
		String notification_id = Taxi_System.getSystem(context, NOTIFICATION_ID);
		if(!notification_id.equals("")) {
			int id = Integer.parseInt(notification_id); 
			NotificationManager mNotificationManager = 
					(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		    mNotificationManager.cancel(id);     
			Taxi_System.addSystem(context, NOTIFICATION_ID, "");
		}
	}
	
	public void didReceiveCode(int code,String string)
	{
		switch(code)
		{
		case 0:
			break;
		case 1:
			if(Taxi_System.getSystem(context, "available").equalsIgnoreCase("0"))
			{
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable(){
					@Override
					public void run() {
						Driver_Status parentFragment = 
								(Driver_Status) getSupportFragmentManager().findFragmentByTag("com.libreteam.driver.Driver_Status");
						parentFragment.showMissedCall();
					} 
				});
				return;
			}
			JSONObject obj;
			try 
			{
				obj = new JSONObject(new JSONObject(string).getString("info"));
				ride_id = obj.getString("ride_id");
			    customer_token = obj.getString("customer_token");
			    address = new JSONObject(new JSONObject(string).getString("userinfo")).getString("address");
			    Taxi_System.addSystem(context, Taxi_Constants.PICK_UP, address+"");
			    latlng = new JSONObject(new JSONObject(string).getString("userinfo")).getString("latlng");
//			    username = new JSONObject(new JSONObject(string).getString("userinfo")).getString("username");
			    d_latlng = new JSONObject(new JSONObject(string).getString("userinfo")).getString("d_latlng");
			    d_address = new JSONObject(new JSONObject(string).getString("userinfo")).getString("d_address");
			    
			    String good = new JSONObject(new JSONObject(string).getString("userinfo")).getString("good");
			    String bad = new JSONObject(new JSONObject(string).getString("userinfo")).getString("bad");
			    Taxi_System.addSystem(context, Taxi_Constants.GOOD, good);
			    Taxi_System.addSystem(context, Taxi_Constants.BAD, bad);
			    if(Driver_Application.isActivityVisible())
			    	didGetRideOffer(new String[]{ride_id,customer_token,address,latlng,d_latlng,d_address});
			    else {
			    	didGetRideOffer(new String[]{ride_id,customer_token,address,latlng,d_latlng,d_address});
			    	Taxi_System.addSystem(context, ACTIVITY_LOSS, "YES");
			    	Taxi_System.addSystem(context, RIDE_OFFER_DATA, string);
			    	notificationShow();
			    }
			} catch (JSONException e) {
				e.printStackTrace();
			}   
			break;  
		case 2:
		case 3:
			if(Taxi_System.getSystem(context, "available").equalsIgnoreCase("0")) 
				return;
			didCancelRide();
			break;
		}
	}
	
	private String[] parseJson(String string) throws JSONException {
		JSONObject obj = new JSONObject(new JSONObject(string).getString("info"));
		String ride_id = obj.getString("ride_id");
	    String customer_token = obj.getString("customer_token");
	    String address = new JSONObject(new JSONObject(string).getString("userinfo")).getString("address");
	    String latlng = new JSONObject(new JSONObject(string).getString("userinfo")).getString("latlng");
	    String d_latlng = new JSONObject(new JSONObject(string).getString("userinfo")).getString("d_latlng");
	    String d_address = new JSONObject(new JSONObject(string).getString("userinfo")).getString("d_address");
	    return new String[]{ride_id,customer_token,address,latlng,d_latlng,d_address};
	}
	
	private void notificationShow() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Customer notification")
		        .setContentText("Hello Driver!")
//		        .setAutoCancel(true)
		        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
		
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, Driver_Fragment_Activity.class);
		resultIntent.putExtra(NOTIFICATION, "YES");
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(Driver_Fragment_Activity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		int id = 69;
		Taxi_System.addSystem(context, NOTIFICATION_ID, id+"");
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());
	}
	
	//DRIVER_GETCANCEL_REJECT_RIDE_POPBACKSTACK
	public void didPopBackStack()
	{
		getSupportFragmentManager().popBackStack();
	}
	
	public void didFinish()
	{
		didAddFragment(new Driver_Status(), "code",new String[]{"notShowMap"},true);
//		getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	private void didCancelRide()
	{
		didReceiveAlert(new String[]{"User has cancelled this ride","0"});
		didAddFragment(new Driver_Status(), "code",new String[]{"notShowMap"},true);
//		getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	public void didRejectRide()
	{
		Taxi_System.addSystem(context, ACTIVITY_LOSS, "NO");
		Taxi_System.addSystem(context, RIDE_OFFER_DATA, "");
		didAddFragment(new Driver_Reject(), "code", new String[]{}, true);
	}
	//	
	
	//DRIVER_LOGIN	
	public void didLogIn()
	{
		didAddFragment(new Driver_Status() , "code", new String[]{"notShowMap"}, true);
		button.setVisibility(View.VISIBLE);
	}
	
	
	//DRIVER_STATUS	
	private void didGetRideOffer(final String[] string)
	{
	    didReceiveAlert(new String[]{"Customer has offer you a ride","1"});
	    didAddFragment(new Driver_RideOffer(), "rideid",string,true);  
	}

	
	//DRIVER_RIDEOFFER	
	public void didAcceptRide(String[] string)
	{
		didAddFragment(new Driver_Boarding(), "rideid",string,true);
	}
	
	//DRIVER_BOARDING	
	public void didAcceptBoarding(String[] string)
	{
		didAddFragment(new Driver_Finish_Rating(), "rideid",string,true);
	}

	//DRIVER_FINISH_RATING	
	public void didFinishRating(String[] string)
	{
		didAddFragment(new Driver_Rating(), "rideid",string,true);
	}
	
	//DRIVER_FINISH_RATING	
	public void didFinishAll()
	{
		finish();
	}	
	
	public void didReceiveAlert(String[] string)
	{
		final Fragment alert = new Banner();
		Bundle alertText = new Bundle();
		alertText.putString("content", string[0]);
		alertText.putString("isAccept", string[1]);
		alert.setArguments(alertText);
		getSupportFragmentManager()
		.beginTransaction()
		.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_fade_out)
		.replace(R.id.dummy, alert,"banner")
		.disallowAddToBackStack()
		.commitAllowingStateLoss();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				new Handler().postDelayed(new Runnable(){
			 		@Override
			 		public void run() {
			 			getSupportFragmentManager()
			 			.beginTransaction()
			 			.setCustomAnimations(R.anim.abc_fade_out,R.anim.abc_slide_out_top)
			 			.remove(alert)
			 			.commitAllowingStateLoss();
			 	}}, 2000);
			}
		});
	}
	
	@SuppressLint("ValidFragment")
	private class Banner extends Fragment
	{
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
	    	View view = inflater.inflate(R.layout.fragment_alert, null);
	    	TextView alert = (TextView)view.findViewById(R.id.alert);
	    	alert.setText(getArguments().getString("content"));

		    if(getArguments().getString("isAccept").equalsIgnoreCase("1"))
		    {
			    ((ImageView)view.findViewById(R.id.alertimg)).setImageDrawable(getResources().getDrawable(R.drawable.accept));
			    alert.setTextColor(Color.parseColor("#479917"));
		    }
		    else
		    {
			    ((ImageView)view.findViewById(R.id.alertimg)).setImageDrawable(getResources().getDrawable(R.drawable.decline));
			    alert.setTextColor(Color.RED);
		    }
		    return view;
		}
	}
	
//	@Override
//	protected void onDestroy()
//	{
		//Socket.socketDidDisconnect();
//		super.onDestroy();
//	}
	 
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
	
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    }
	
	public void toggleMenu(View v) {
		//mLayout.toggleMenu();
	}
	
	@Override
	public void onBackPressed()
	{        
//        didHideMenu();
//        if(isBackable)
//        showAlert();   
		isInMenu = false;
		super.onBackPressed();
	}

//	private void showAlert()
//	{
//		new AlertDialog.Builder(this)
//	    .setTitle("Attention!")
//	    .setMessage("This will cancel the current ride,are you sure?")
//	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int which) { 
//	        	didFinish();
//	        }
//	     })
//	     .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//		        public void onClick(DialogInterface dialog, int which) { 
//		        }
//		     })
//	    .setIcon(android.R.drawable.ic_dialog_alert)
//	     .show();
//	}
	  
	public void didAddFragment(Fragment frag,String var,String[] list,boolean isCheck)
	{
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		Bundle data = new Bundle();
		data.putStringArray(var,list);			
		frag.setArguments(data);
		ft.replace(R.id.activity_main_content_fragment, frag , frag.getClass().getName());
		if(isCheck)
		ft.addToBackStack(frag.getClass().getName());
		ft.commitAllowingStateLoss(); 
    }
		
	public void didHideMenu()
	{
		if(isMenu)
		{
			getSupportFragmentManager()
			.beginTransaction()
			.setCustomAnimations(0, R.anim.abc_fade_out)
			.remove(getSupportFragmentManager().findFragmentByTag("menu"))
			.disallowAddToBackStack()
			.commitAllowingStateLoss();
		    isMenu = false;
           	return;
		}
	}

	@Override
	public void didPressLogOut() {
		button.setVisibility(View.GONE);
		didHideMenu();
		FragmentManager manager = getSupportFragmentManager();
	    if (manager.getBackStackEntryCount() > 0) {
	        FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
	        manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		Taxi_System.didEndUpdatePosition();
		/* For backgournd service by Ying */
		Intent mServiceIntent = new Intent(this, RSSPullService.class);
		
		this.stopService(mServiceIntent);
		/**********************************/		
		Socket.socketDidDisconnect();    
		didAddFragment(new Driver_Login(), "code", new String[] {}, true);
	}

	@Override
	public void didPressProfile() {
		didHideMenu();
		didAddFragment(new Driver_Profile(), "code", new String[] {}, true);
		isInMenu = true;
	}

	@Override
	public void didPressSettings() {
		didHideMenu();
		didAddFragment(new Driver_Settings(), "code", new String[] {}, true);
		isInMenu = true;
	}

	@Override
	public void didPressStatistic() {
		didHideMenu();
		didAddFragment(new Driver_StatisTic(), "code", new String[] {}, true);
		isInMenu = true;
	}
		
	@Override
	public void didPressStory() {
		didHideMenu();
		didAddFragment(new Driver_History(), "code", new String[] {}, true);
		isInMenu = true;
	}

	@Override
	public void didPressRadar() {
		didHideMenu();
		//(new Driver_Status()).setShowMap(true); 
//		didAddFragment(new Driver_Status(), "code", new String[] {"showMap"}, true);
//		isInMenu = true;
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
	    Taxi_System.checkPlayServices(this);
	    Driver_Application.activityResumed();
	    removeNotification();
//	    Socket.socketDidConnect(context);
//	    gpsTracker = new GPSTracker(context);
//	    setActivity();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Driver_Application.activityStopped();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		gpsTracker.stopUsingGPS();
	}
	
//	private GPSTracker gpsTracker;
//	private static double latitude; // latitude
//    private static  double longitude; // longitude
//    private static Location location; // location
//    
//    public static double getLatitude(){
//        return latitude;
//    }
//
//    public static double getLongitude(){
//        return longitude;
//    }
//    
//    public static Location getLocation(){
//        return location;
//    }
//	/**
//     * Get GPS position
//     */
//    private class GPSTracker implements LocationListener {
//
//    	private List<String> provider;
////        private double latitude; // latitude
////        private double longitude; // longitude
////        private Location location; // location
//        
//        // Declaring a Location Manager
//        protected LocationManager locationManager;
//        
//        public GPSTracker(Context context) {
//            setLocation();
//        }
//        
//        public void setLocation() {
//        	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
//            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//            provider = locationManager.getProviders(criteria, true);
//            if (provider.isEmpty()){
//            }
//            else
//            {
//                for (String enabledProvider : provider)
//                {
//                    locationManager.requestSingleUpdate(enabledProvider, this, null);
//                }
//            }
//        }
//
//    
//        public void stopUsingGPS(){
//            if(locationManager != null){
//                locationManager.removeUpdates(GPSTracker.this);
//            }       
//        }
//        
//        @Override
//        public void onLocationChanged(Location mLocation) {
//        	if (mLocation != null) {
//           		latitude = mLocation.getLatitude();
//           		longitude = mLocation.getLongitude();
//           		location = mLocation;
//           		Taxi_System.didUpdatePosition(context, location);
//         	}
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//        }
//
//        @Override
//    	public void onProviderEnabled(String provider) { 
//    	}
//
//        @Override
//    	public void onStatusChanged(String provider, int status, Bundle extras) {  
//    	}
//    }
}
