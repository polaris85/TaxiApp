package com.libreteam.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.libreteam.driver.R;
import com.libreteam.driver.extra.CustomMapFragment;
import com.libreteam.driver.extra.Socket;
import com.libreteam.driver.extra.Taxi_Constants;
import com.libreteam.driver.extra.CustomMapFragment.OnMapReadyListener;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Driver_RideOffer extends Fragment implements OnClickListener, OnMapReadyListener, 
															OnInfoWindowClickListener, OnMarkerClickListener {

	private CustomMapFragment  mMapFragment;
	private Context context;
	private String ride_id,customer_id,address,latlng,time="-",range="-";
    private CountDownTimer countDownTimer;
    private final long startTime = 21 * 1000;
	private final long interval = 1 * 1000;
	
	private static LatLng driverLatLng = null;
	private static LatLng customerLatLng = null;
	
	private TextView txtCountDown, txtMeter;
	private Button btnAccept;
	
	@SuppressLint("InflateParams")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.driver_rideoffer, null);
    
		context = view.getContext();
    
		ride_id = getArguments().getStringArray("rideid")[0];
		customer_id = getArguments().getStringArray("rideid")[1];
		address = getArguments().getStringArray("rideid")[2];
		latlng = getArguments().getStringArray("rideid")[3];

		ArrayList aList= new ArrayList(Arrays.asList(latlng.split(",")));
		driverLatLng = new LatLng(Driver_Status.getLatitude(), Driver_Status.getLongitude()); 
		customerLatLng = new LatLng(Double.parseDouble(aList.get(0).toString()), Double.parseDouble(aList.get(1).toString()));

		if (savedInstanceState == null) 
			initComponents(view);
	
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new View.OnKeyListener() {
	        @Override
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	            if( keyCode == KeyEvent.KEYCODE_BACK ) {
	            	if(((Driver_Fragment_Activity)getActivity()).isMenu)
	            	{
	            		((Driver_Fragment_Activity)getActivity()).didHideMenu();
	            		return true;
	            	}
	            	didSendMessage();
	    	    	((Driver_Fragment_Activity)getActivity()).didFinish();
	                return false;
	            } else {
	                return false;
	            }
	        }
	    });
		
        return view;
	}

	private void didSendMessage()
	{
		HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
 		try
 		{
 			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
 			hash.put("ride_id", new JSONObject().put("ride_id", ride_id));
 			hash.put("id", new JSONObject().put("id", customer_id));
 		} catch (JSONException e) {
 			e.printStackTrace();
 		}
 		HashMap<String, JSONObject> info = new HashMap<String, JSONObject>();
 		try
 		{
 			info.put("username", new JSONObject().put("username", Taxi_System.getSystem(context, "username")));
 		} catch (JSONException e) {
 			e.printStackTrace();
 		}		
 		
	  	Socket.socketDidSendMessage(Taxi_System.jsonString(
	  			new String[]{"code","type"},
	  			new String[]{"2",Taxi_System.getSystem(context,"type")},
	  			new String[]{"token","ride_id","id"},
	  			hash,
	  			new String[]{"username"},
	  			info
 		));
	}
	
	public class MyCountDownTimer extends CountDownTimer {
		
//		private MediaPlayer  mp = MediaPlayer.create(getActivity(), R.raw.alert);
		public MyCountDownTimer(long startTime, long interval)
		{
			super(startTime, interval);
		}

		@Override
		public void onFinish()
		{	
			countDownTimer.cancel();
			didRejectRide();
//			if(!mp.isPlaying()) {
//				mp.stop();
//				mp.release();
//			}
		}

		@Override		 
		public void onTick(long millisUntilFinished) 
		{
			txtCountDown.setText("" + millisUntilFinished / 1000);
			if(millisUntilFinished / 1000 <=21 && millisUntilFinished / 1000 >=17)
			{
//				mp.start();
			}
			if(millisUntilFinished / 1000 <= 10)
			{
				txtCountDown.setTextColor(Color.RED);
			}
			if(millisUntilFinished / 1000 <= 5)
			{
				blinkCountDown();
			}
		}		
		
    }
	
	@Override
	public void onDestroy() {
		if(countDownTimer!=null)
			countDownTimer.cancel();
		super.onDestroy();
	}
	 
	private void didRejectRide()
	{
		((Driver_Fragment_Activity)getActivity()).didRejectRide();
		HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
   	 	try
	    {
	    	hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
	    	hash.put("ride_id", new JSONObject().put("ride_id", ride_id));
	    	hash.put("id", new JSONObject().put("id", customer_id));
	    } catch (JSONException e) {
	    	e.printStackTrace();
	    }
	    		
	    HashMap<String, JSONObject> info = new HashMap<String, JSONObject>();
	    try
	    {
	    	info.put("username", new JSONObject().put("username", Taxi_System.getSystem(context, "username")));
	   	} catch (JSONException e) {
	   		e.printStackTrace();
	   	}
	   
	    Socket.socketDidSendMessage(Taxi_System.jsonString(
	    		new String[]{"code","type"},
	    		new String[]{"3",Taxi_System.getSystem(context,"type")},
	    		new String[]{"token","ride_id","id"},
	    		hash,
	    		new String[]{"username"},
	    		info
		));
	}
	
	private void blinkCountDown()
	{
//		MediaPlayer  mp = MediaPlayer.create(getActivity(), R.raw.alert);
//		mp.start();
		Animation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(150); 
		anim.setStartOffset(120);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		txtCountDown.startAnimation(anim);
	} 
	 
	private void initComponents(View v)
	{
		countDownTimer = new MyCountDownTimer(startTime, interval);
		new Handler().post(new Runnable() {
		    @Override
		    public void run() {
				countDownTimer.start();
		    }
		});
		
		((TextView)v.findViewById(R.id.no1)).setText(Taxi_System.getSystem(context, Taxi_Constants.GOOD));
		((TextView)v.findViewById(R.id.no2)).setText(Taxi_System.getSystem(context, Taxi_Constants.BAD));
		
		Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));
		Taxi_System.setContent((LinearLayout)v.findViewById(R.id.header),context,1,(float) 0.07);

		LinearLayout thirdsection = (LinearLayout)v.findViewById(R.id.thirdsection);
		Taxi_System.setContent(thirdsection, context, (float) 0.125, (float)0.07);
		ImageView thirdimg = (ImageView)v.findViewById(R.id.thirdimg);
		Taxi_System.setContent(thirdimg, context, (float)0.06, (float)0.04);
		
		LinearLayout logosection = (LinearLayout)v.findViewById(R.id.logosection);
		Taxi_System.setContent(logosection, context, (float) 0.25, (float)0.07);
		ImageView logo = (ImageView)v.findViewById(R.id.logo);
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		  
		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);
		
		txtMeter = (TextView)v.findViewById(R.id.meter);
		txtMeter.setTypeface(null, Typeface.BOLD);
		
		txtCountDown = (TextView)v.findViewById(R.id.countdown);
		txtCountDown.setTypeface(null, Typeface.BOLD);

		//((TextView)v.findViewById(R.id.countdown)).setTextSize(100);
		((TextView)v.findViewById(R.id.address)).setText(address);
		
		Button reject = (Button) v.findViewById(R.id.reject);
		reject.setTypeface(null, Typeface.BOLD);
		reject.setOnClickListener(this);
		
		btnAccept = (Button) v.findViewById(R.id.accept);
		btnAccept.setTypeface(null, Typeface.BOLD);
		btnAccept.setOnClickListener(this);
        		
        if(mMapFragment == null)
        {
		    mMapFragment = CustomMapFragment.newInstance();
	        getChildFragmentManager()
	        .beginTransaction()
	        .setCustomAnimations(R.anim.abc_fade_in, 0)
	        .replace(R.id.map, mMapFragment,"boarding")
	        .commitAllowingStateLoss();		
        }
	}

	@Override
    public void onMapReady() {
    	if(mMapFragment != null)
    	{
		    Taxi_System.setUiMap(mMapFragment.getMap());
	        mMapFragment.getMap().setOnInfoWindowClickListener(this);
	        mMapFragment.getMap().setOnMarkerClickListener(this);
	    	setUpMapView();
    	}
    }

    private void setUpMapView()
    {
	   	List<LatLng> points = new ArrayList<LatLng>();
	   	points.add(customerLatLng);
	   	points.add(driverLatLng);
	    addMarker(points,mMapFragment.getMap());

	    Location loc1 = new Location("");
	    loc1.setLatitude(customerLatLng.latitude);
	    loc1.setLongitude(customerLatLng.longitude);

	    Location loc2 = new Location("");
	    loc2.setLatitude(driverLatLng.latitude);
	    loc2.setLongitude(driverLatLng.longitude);

	    float distanceInMeters = loc1.distanceTo(loc2);
	    
	    if(distanceInMeters>1000)
	    	Taxi_System.zoomToFitLatLongs(points, mMapFragment.getMap(), Taxi_System.getWidth(context),(int)(Taxi_System.getHeight(context) * 0.6));
	    else
	    	Taxi_System.zoomToLocationRide(driverLatLng.latitude, driverLatLng.longitude, mMapFragment.getMap());

		try
		{
			new ParserTask().execute(Taxi_System.downloadTask(Taxi_System.getMapsApiDirectionsUrl(points)));
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		} 
		catch (ExecutionException e) {
			e.printStackTrace();
		}	  
    }

	private void addMarker(List<LatLng> latlng,GoogleMap mMap)
	{
		int i = 0;
		int resourceId = 0;
		String title = null;
		for(LatLng mlat : latlng)
		{
//			title = Taxi_System.getAddressLine(context, lat, lng);
			if(i == 0)
			{
				title = title=="" ? "You're here!" : title;
				resourceId = R.drawable.map_taxi_green;
			}
			else
			{
				title = title=="" ? "Taxi's here!" : title;
				resourceId = R.drawable.map_taxi_yellow;
			}
			mMap.addMarker(new MarkerOptions()
            .position(mlat)
            .title(title)
            .icon(BitmapDescriptorFactory.fromResource(resourceId))
            .flat(true)
            .rotation(0));
			i++;
		}
	}

	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
    	protected void onPreExecute()
    	{
    		if(Taxi_System.connectionStatus(context))
    			Taxi_System.showDialog(context);
    		else
    		{
    			Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
    			return;
    		}
        }
		
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				com.libreteam.driver.extra.PathJSONParser parser = new com.libreteam.driver.extra.PathJSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) 
			
			{
				e.printStackTrace();
			}
			return routes;
		}
		
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			Taxi_System.notShow();
			Taxi_System.testLog(routes);
			if(routes==null || routes.size() == 0)
			{
				if(countDownTimer!=null)
					countDownTimer.cancel();
				btnAccept.setEnabled(false);
				txtMeter.setText("---");
				showAlert();
				return;
			}
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;
			String distance = "";
			String duration = "";			
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if(j==0){	
						distance = (String)point.get("distance");						
						continue;
					}else if(j==1){ 
						duration = (String)point.get("duration");
						continue;
					}
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					points.add(position);
				}

				polyLineOptions.addAll(points);
				polyLineOptions.width(5);
				polyLineOptions.color(Color.BLACK);
			}
			mMapFragment.getMap().addPolyline(polyLineOptions);
			txtMeter.setText("  " + distance + "  ");
			time = duration;
			range = distance;
		}
	}
    
	private void showAlert()
	{
		new AlertDialog.Builder(context)
	    .setTitle(context.getResources().getString(R.string.attention))
	    .setMessage(context.getResources().getString(R.string.no_route_available))
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	((Driver_Fragment_Activity)getActivity()).didFinish();
	        }
	     })
	     .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	//((Driver_Fragment_Activity)getActivity()).didFinish();
		        }
		     })
		.setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.reject:
            didRejectRide();
		   break;  
		case R.id.accept:
			if(countDownTimer!=null)
				countDownTimer.cancel();
			((Driver_Fragment_Activity)getActivity()).didAcceptRide(new String[]{
        		ride_id, 
        		customer_id,
        		address,
        		//String.valueOf(gpsTracker.getLatitude()) + "," + String.valueOf(gpsTracker.getLongitude()),
        		String.valueOf(customerLatLng.latitude) + "," + String.valueOf(customerLatLng.longitude),
        		range,
        		getArguments().getStringArray("rideid")[4],
        		getArguments().getStringArray("rideid")[5],
        		latlng
			});
//        	Taxi_System.didAddFragment(this, new Driver_Boarding(), "rideid",
//        		new String[]{
//        		ride_id,customer_id,
//        		gpsTracker.getAddressLine(context) == null ? "Not Available" : gpsTracker.getAddressLine(context),
//        		String.format("%.0f,%.0f", gpsTracker.getLatitude(),gpsTracker.getLongitude()),
//        		String.valueOf((int)(Float.parseFloat(range.split(" ")[0]) * 1609)) + "m"
//        		},true);
			{
				HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
	    		try
	    		{
	    			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
	    			hash.put("ride_id", new JSONObject().put("ride_id", ride_id));
	    			hash.put("id", new JSONObject().put("id", customer_id));
	    		} catch (JSONException e) {
	    			e.printStackTrace();
	    		}
	    		HashMap<String, JSONObject> info = new HashMap<String, JSONObject>();
	    		try
	    		{
	    			info.put("address", new JSONObject().put("address", address));
	    			info.put("latlng", new JSONObject().put("latlng",String.valueOf(Driver_Status.getLatitude()) + "," + String.valueOf(Driver_Status.getLongitude()))); //String.format("%d,%d", gpsTracker.getLatitude(),gpsTracker.getLongitude())	    			
	    			info.put("car_model", new JSONObject().put("car_model", new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("car_model")));	    			
	    			info.put("time", new JSONObject().put("time", time));	    			
	    			info.put("username", new JSONObject().put("username", new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("first_name") + "" + new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("last_name")));
	    			info.put("taxi_company", new JSONObject().put("taxi_company", new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("company_name")));
	    			info.put("license_plate", new JSONObject().put("license_plate", new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("taxi_license_number")));
	    			info.put("avatar", new JSONObject().put("avatar", new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("avatar")));
	    			info.put("des", new JSONObject().put("des", getArguments().getStringArray("rideid")[5]));
	    			info.put("des_lat", new JSONObject().put("des_lat", getArguments().getStringArray("rideid")[4]));
	    			info.put("start_lat", new JSONObject().put("start_lat", latlng));
	    		} catch (JSONException e) {
	    			e.printStackTrace();
	    		}	
	    		
	  		 Socket.socketDidSendMessage(Taxi_System.jsonString(
	   	    		new String[]{"code","type"},
	   	    		new String[]{"4",Taxi_System.getSystem(context,"type")},
	   	    		new String[]{"token","ride_id","id"},
	   	    		hash,
	   	    		new String[]{"address","latlng","username","time","car_model","taxi_company","license_plate","avatar","des","des_lat","start_lat"},info
	   	    		));
        	}
        	break;   
		}
	}
	
	@Override
	public void onInfoWindowClick(Marker marker) {
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		showAlert();
		return false;
	}

	@Override
	public void onAttachFinish() {		
	}
}
