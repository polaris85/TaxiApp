package com.libreteam.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
import com.libreteam.driver.extra.CustomMapFragment;
import com.libreteam.driver.extra.PathJSONParser;
import com.libreteam.driver.extra.Socket;
import com.libreteam.driver.extra.CustomMapFragment.OnMapReadyListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Driver_Boarding extends Fragment implements OnInfoWindowClickListener, 
															OnMarkerClickListener, OnClickListener, OnMapReadyListener {
	private CustomMapFragment  mMapFragment;
	private Context context;
    private String ride_id,customer_id,address,latlng,range;
    private boolean isTracking;
    private static LatLng driverLatLng = null;
	private static LatLng customerLatLng = null;
	public Timer timer;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.driver_boarding, null);
		context = view.getContext();
		isTracking = false;

		ride_id = getArguments().getStringArray("rideid")[0];
		customer_id = getArguments().getStringArray("rideid")[1];
    	address = getArguments().getStringArray("rideid")[2];
    	latlng = getArguments().getStringArray("rideid")[3];
    	range = getArguments().getStringArray("rideid")[4];

    	for(String s : getArguments().getStringArray("rideid"))
    		Taxi_System.testLog(s);
    
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
	                return true;
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
  		Socket.socketDidSendMessage(Taxi_System.jsonString(
  			new String[]{"code","type"},
  			new String[]{"2",Taxi_System.getSystem(context,"type")},
  			new String[]{"token","ride_id","id"},
  			hash,
  			null,null
		));
	}
	
	private void initComponents(View v)
	{
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
			
		((TextView)v.findViewById(R.id.address)).setText(address);
		((TextView)v.findViewById(R.id.dis)).setText(range);
		((TextView)v.findViewById(R.id.dis)).setTypeface(null, Typeface.BOLD);
		
		Button here = (Button) v.findViewById(R.id.here);
		here.setTypeface(null, Typeface.BOLD);
		here.setOnClickListener(this);
		
		Button reject = (Button) v.findViewById(R.id.reject);
		reject.setTypeface(null, Typeface.BOLD);
		reject.setOnClickListener(this);
		
		Button accept = (Button) v.findViewById(R.id.accept);
		accept.setTypeface(null, Typeface.BOLD);
		accept.setOnClickListener(this);
		
		Button latlong = (Button) v.findViewById(R.id.ulatlong);
	    latlong.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.07);
	    latlong.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
		latlong.setOnClickListener(this);
		
        if(mMapFragment == null)
        {
		    mMapFragment = CustomMapFragment.newInstance();
	        getChildFragmentManager()
	        .beginTransaction()
	        .setCustomAnimations(R.anim.abc_fade_in, 0)
	        .replace(R.id.map, mMapFragment,"driver_boading")
	        .commitAllowingStateLoss();		
        }
	}
	
	@Override
	public void onDestroyView()
	{
		//Taxi_System.didEndUpdatePosition();
		super.onDestroyView();
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

//	    Taxi_System.zoomToFitLatLongs(points, mMapFragment.getMap(), Taxi_System.getWidth(context),(int)(Taxi_System.getHeight(context) * 0.6));
//	    Taxi_System.zoomToLocationRide(driverLatLng.latitude, driverLatLng.longitude, mMapFragment.getMap());
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
		mMapFragment.getMap().clear();
		for(LatLng lat : latlng)
		{
			title = Taxi_System.getAddressLine(context, Driver_Status.getLatitude(), Driver_Status.getLongitude());
			if(i == 0)
			{
				title = title == "" ? "You're here!" : title;
				resourceId = R.drawable.map_taxi_green;
			}
			else
			{
				title = title=="" ? "Taxi's here!" : title;
				resourceId = R.drawable.map_taxi_yellow;
			}
			mMapFragment.getMap().addMarker(new MarkerOptions()
            .position(lat)
            .title(title)
            .icon(BitmapDescriptorFactory.fromResource(resourceId))
            .flat(true)
            .rotation(0));
			i++;
		}
	}
	
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
		
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
	
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;
	
			try 
			{
				jObject = new JSONObject(jsonData[0]);
				PathJSONParser parser = new PathJSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}
	
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;
//			String distance = "";
//			String duration = "";
			if(routes==null) {
				Taxi_System.showToast(context, getResources().getString(R.string.check_internet_connection));
				return;
			}
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);
	
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);
	
					if(j==0){	
//						distance = (String)point.get("distance");						
						continue;
					}else if(j==1){ 
//						duration = (String)point.get("duration");
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
		}
    }
	 
	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}


	@Override
	public void onInfoWindowClick(Marker marker) {
		
	}
	float i = 0;	
	public void didUpdatePosition()
	{
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() 
		{
		    @Override
		    public void run() {
			    	
		    	Handler handler = new Handler(Looper.getMainLooper());
	 			handler.post(new Runnable(){
					@Override
					public void run() {
						i += 0;//0.05;
				    	driverLatLng = new LatLng(Driver_Status.getLatitude(), Driver_Status.getLongitude() + i);
				    	startTracking(driverLatLng);
					} 
	 			});
		    }
		}, 0, 6000);
	}
		
	private void startTracking(LatLng driver)
	{
		List<LatLng> points = new ArrayList<LatLng>();
	  	points.add(customerLatLng);
	   	points.add(driver);
	    addMarker(points,mMapFragment.getMap());
//	    Taxi_System.zoomToFitLatLongs(points, mMapFragment.getMap(), Taxi_System.getWidth(context),(int)(Taxi_System.getHeight(context) * 0.6));
	    Taxi_System.zoomToLocationRide(driver.latitude, driver.longitude, mMapFragment.getMap());
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
	
	public void didEndUpdatePosition()
	{
		if(timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.ulatlong:
 			if(!isTracking)
 			{
 				didUpdatePosition();
 				Taxi_System.showToast(context, context.getResources().getString(R.string.start_tracking));
 			}
 			else
 			{
 				didEndUpdatePosition();
 				Taxi_System.showToast(context, context.getResources().getString(R.string.stop_tracking));
 			}
 			isTracking =! isTracking;
 			break;
		case R.id.here: {
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
		    		info.put("name", new JSONObject().put("name", new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("first_name") + " " + new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("last_name")));
		    		info.put("taxi_company", new JSONObject().put("taxi_company", new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("company_name")));
		    		info.put("car_model", new JSONObject().put("car_model", new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("car_model")));
		    		info.put("des_lat", new JSONObject().put("des_lat", getArguments().getStringArray("rideid")[5]));
		    		info.put("des_address", new JSONObject().put("des_address", getArguments().getStringArray("rideid")[6]));
		    		info.put("start_lat", new JSONObject().put("start_lat", getArguments().getStringArray("rideid")[7]));
		    		info.put("latlng", new JSONObject().put("latlng", 
		    				String.valueOf(Driver_Status.getLatitude()) +","+String.valueOf(Driver_Status.getLongitude())));
		    	} catch (JSONException e) {
		    		e.printStackTrace();
		    	}	
		    	Socket.socketDidSendMessage(Taxi_System.jsonString(
		   	    		new String[]{"code","type"},
		   	    		new String[]{"7",Taxi_System.getSystem(context,"type")},
		   	    		new String[]{"token","ride_id","id"},
		   	    		hash,
		   	    		new String[]{"name","taxi_company","car_model","des_lat","des_address","start_lat","latlng"},
		   	    		info
		   		));
			}
	    	break;     
         case R.id.reject:
        	 ((Driver_Fragment_Activity)getActivity()).didRejectRide();
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
    		  		Socket.socketDidSendMessage(Taxi_System.jsonString(
    	    		new String[]{"code","type"},
    	    		new String[]{"2",Taxi_System.getSystem(context,"type")},
    	    		new String[]{"token","ride_id","id"},
    	    		hash,
    	    		null,null
    	    		));
            	}
             break;
         case R.id.accept:
        	   ((Driver_Fragment_Activity)getActivity()).didAcceptBoarding(new String[]{
        		ride_id,
        		customer_id,
        		getArguments().getStringArray("rideid")[6],
                String.valueOf(customerLatLng.latitude) + "," + String.valueOf(customerLatLng.longitude),
                getArguments().getStringArray("rideid")[5]
        		});
         	 {
            	 HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
    	    		try
    	    		{
    	    			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
    	    			hash.put("ride_id", new JSONObject().put("ride_id", ride_id));
    	    			hash.put("id", new JSONObject().put("id", customer_id));
    	    			hash.put("pickup", new JSONObject().put("pickup", address));
    	    		} catch (JSONException e) {
    	    			e.printStackTrace();
    	    		}

    		  		Socket.socketDidSendMessage(Taxi_System.jsonString(
    	    		new String[]{"code","type"},
    	    		new String[]{"5",Taxi_System.getSystem(context,"type")},
    	    		new String[]{"token","ride_id","id","pickup"},
    	    		hash,
    	    		null,null
    	    		));
            	}
         	 break;
		}
	}

	@Override
	public void onAttachFinish() {
		
	}

}
