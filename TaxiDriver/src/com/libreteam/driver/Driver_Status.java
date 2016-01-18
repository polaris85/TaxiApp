package com.libreteam.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.libreteam.driver.Driver_Fragment_Off.DriverOff;
import com.libreteam.driver.Driver_Fragment_On.DriverOn;
import com.libreteam.driver.Driver_Information.DriverMenu;
import com.libreteam.driver.extra.CustomMapFragment;
import com.libreteam.driver.extra.CustomMapFragment.OnMapReadyListener;
import com.libreteam.driver.extra.RSSPullService;
import com.libreteam.driver.extra.Socket;
import com.libreteam.driver.extra.Taxi_Constants;
//import com.libreteam.driver.extra.GPSTracker;

public class Driver_Status extends Fragment implements OnClickListener, OnMarkerClickListener, 
														GooglePlayServicesClient.ConnectionCallbacks, OnInfoWindowClickListener, 
														OnMapReadyListener, DriverOn, DriverOff {
	private Context context;
	private static boolean isShowMap;
	private boolean isAvailable;
    private final List<Marker> mMarkerRainbow = new ArrayList<Marker>(); 
    private final List<LatLng> latlong = new ArrayList<LatLng>(); 
	private final List<JSONObject> listDriver = new ArrayList<JSONObject>();
	private CustomMapFragment  mMapFragment;
	private Button u;
	private ImageView updown;
	private TextView txtStartingFee, txtPerKm, txtDistance, txtStatus;
	
	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.driver_status, null);
		context = view.getContext();
    
		
		if ((savedInstanceState == null)) 
			initComponents(view);
		gpsTracker = new GPSTracker(context);
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
	                return true;
	            } else {
	                return false;
	            }
	        }
	    });
	
		return view;
	}
	
	private void initComponents(View v)
	{
		if(Taxi_System.getSystem(context, "available") == null || Taxi_System.getSystem(context, "available").equalsIgnoreCase(""))
			Taxi_System.addSystem(context, "available", "1");
	    mMapFragment = CustomMapFragment.newInstance();
		isAvailable =  false;
		Taxi_System.testLog(getResources().getString(R.raw.local_en));
		
		Taxi_System.setContent((LinearLayout)v.findViewById(R.id.header),context,1,(float) 0.07);
		LinearLayout uFrame = (LinearLayout)v.findViewById(R.id.uFrame);
		Taxi_System.setContent(uFrame, context, 1, (float)0.04);
		
		Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));

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
		
		updown = (ImageView)v.findViewById(R.id.mapicon);
		updown.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.04);
		updown.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.04);
		
		txtStartingFee = (TextView)v.findViewById(R.id.fee);
		txtPerKm = (TextView)v.findViewById(R.id.km);
		txtDistance = (TextView)v.findViewById(R.id.distance);
		txtStartingFee.setTypeface(null, Typeface.BOLD);
		txtPerKm.setTypeface(null, Typeface.BOLD);
		txtDistance.setTypeface(null, Typeface.BOLD);
		txtStatus = (TextView)v.findViewById(R.id.status);
		
		View s = (View)v.findViewById(R.id.v);
		s.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.15) / 5 * 3/4;
		
		u = (Button) v.findViewById(R.id.ubutton);
		u.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
//				if(gpsTracker.getLocation() == null) { Taxi_System.showToast(context, "Gps not available"); return;}				
				if(!isAvailable)
				{
					u.setTextColor(getResources().getColor(color.holo_orange_light));
					updown.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
					getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.down, 0).replace(R.id.mapfrag, mMapFragment,"statusmap").disallowAddToBackStack().commitAllowingStateLoss();
				}
				else
				{
					u.setTextColor(getResources().getColor(color.darker_gray));
					updown.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
	 				getChildFragmentManager().beginTransaction().setCustomAnimations(0,R.anim.down_b).remove(getChildFragmentManager().findFragmentByTag("statusmap")).commit();
	 				((Button) getView().findViewById(R.id.ulatlong1)).setVisibility(View.GONE);

				}
			    isAvailable =! isAvailable;
			}
		});
		updown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
//				if(gpsTracker.getLocation() == null) { Taxi_System.showToast(context, "Gps not available"); return;}				
				if(!isAvailable)
				{
					u.setTextColor(getResources().getColor(color.holo_orange_light));
					updown.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
					getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.down, 0).replace(R.id.mapfrag, mMapFragment,"statusmap").disallowAddToBackStack().commitAllowingStateLoss();
				}
				else
				{
					u.setTextColor(getResources().getColor(color.darker_gray));
					updown.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
	 				getChildFragmentManager().beginTransaction().setCustomAnimations(0,R.anim.down_b).remove(getChildFragmentManager().findFragmentByTag("statusmap")).commit();
	 				((Button) getView().findViewById(R.id.ulatlong1)).setVisibility(View.GONE);

				}
			    isAvailable =! isAvailable;
			}
		});
       
		Button select = (Button) v.findViewById(R.id.select);
		select.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.04);
		select.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.04);
		select.setOnClickListener(this);

		Button urlatlong = (Button) v.findViewById(R.id.ulatlong1);
	    urlatlong.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.07);
	    urlatlong.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
		urlatlong.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
              goToMyLocation();				
			}
		}); 
	    
	    if(Taxi_System.getSystem(context, "available").equalsIgnoreCase("1"))
	    {
		    getChildFragmentManager().beginTransaction().setCustomAnimations(0, 0).add(R.id.map, new Driver_Fragment_On(),"driverOn").commit();
		    txtStatus.setText(context.getResources().getString(R.string.status_headline));
//		    isOn = true;
	    }
	    else
	    {
	    	getChildFragmentManager().beginTransaction().setCustomAnimations(0, 0).add(R.id.map, new Driver_Fragment_Off(),"driverOff").commit();
	    	String text = "<font color='red'>" + context.getResources().getString(R.string.status_headline_busy) + "</font>  <font color='black'>" + context.getResources().getString(R.string.status_headline_status) + "</font>";
	    	txtStatus.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
//	    	isOn = false;
	    }
//	    if(Taxi_System.getSystem(context, "userinfo").equalsIgnoreCase("") || Taxi_System.getSystem(context, "userinfo") == null)
//	    {
		new RequestUserInfor().execute();
//	    }
//	    else
//	    {
//            try
//            {
//				setDetail(new JSONObject(Taxi_System.getSystem(context, "userinfo")),v);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//	    }
		if(getArguments().getStringArray("code")[0].equalsIgnoreCase("showMap"))
		{
			showMap();
	    	getArguments().getStringArray("code")[0] = null;
		}
		
//		setAvaiableDefault();
	}

//	private void setAvaiableDefault() {
//		if(Taxi_System.getSystem(context, "km") == null || Taxi_System.getSystem(context, "km").equalsIgnoreCase(""))
//			Taxi_System.addSystem(context, "km", "8");
//		if(Taxi_System.getSystem(context, "start_fee") == null || Taxi_System.getSystem(context, "start_fee").equalsIgnoreCase(""))
//			Taxi_System.addSystem(context, "start_fee", "0");
//		if(Taxi_System.getSystem(context, "per_km").equalsIgnoreCase(""))
//			Taxi_System.addSystem(context, "per_km", "10");
//		
//		txtStartingFee.setText(Taxi_System.getSystem(context, "start_fee")+"");
//		txtPerKm.setText(Taxi_System.getSystem(context, "km")+"");
//		txtDistance.setText(Taxi_System.getSystem(context, "per_km") + " Km");
//	}
	
	private DriverMenu menuInterface;
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
	    {
	    	menuInterface = (DriverMenu) activity;

	    } catch (ClassCastException e)
	    {
	          throw new ClassCastException(" must implement MenuInterface");
	    }
	 }
	
	public void showMap()
	{
//		if(gpsTracker.getLocation() == null) { Taxi_System.showToast(context, "Gps not available"); return;}
		u.setTextColor(getResources().getColor(color.holo_orange_light));
		updown.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
		getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.down, 0).replace(R.id.mapfrag, mMapFragment,"statusmap").disallowAddToBackStack().commitAllowingStateLoss();
        isAvailable = true;
	}

	public void showMissedCall()
	{
        Driver_Fragment_Off parentFragment = (Driver_Fragment_Off) getChildFragmentManager()
				   .findFragmentByTag("driverOff");
        parentFragment.getMissedCall();
	}

	
    @Override
    public void onMapReady() {
    	if(mMapFragment != null)
    	{
		    Taxi_System.setUiMap(mMapFragment.getMap());
	        mMapFragment.getMap().setOnInfoWindowClickListener(this);
	        mMapFragment.getMap().setOnMarkerClickListener(this);
		    goToMyLocation();	
    	}
    }
	
    private void setUpMapView()
    {
        try 
        {
			addMarkersToMap();
		} catch (JSONException e) {
			e.printStackTrace();
		} 
//        if(mMapFragment.getMap()!=null)
        	Taxi_System.zoomToLocation(latlong.get(0).latitude, latlong.get(0).longitude, mMapFragment.getMap());

    }

    private void addMarkersToMap() throws JSONException
    {
    	if(mMapFragment!=null && mMapFragment.getMap()!=null) {
    		mMapFragment.getMap().clear();
	        for (LatLng mlat : latlong)
	        {  
	        	String address = Taxi_System.getAddressLine(context, getLatitude(), getLongitude());
	        	if(latlong.indexOf(mlat) == 0)
		        {
		        	mMarkerRainbow.add(mMapFragment.getMap().addMarker(new MarkerOptions()
				                .position(mlat)
				                .title(address == null ? "You're here!" : address)
				                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_taxi))
				                .flat(true)
				                .rotation(0)));
		        	CircleOptions circleOptions = new CircleOptions()
		        		.center(mlat)
				   	  	.radius(0)
				   	   	.strokeWidth(2)
				   	   	.strokeColor(Color.TRANSPARENT)
				   	 	.fillColor(Color.TRANSPARENT);
				   	mMapFragment.getMap().addCircle(circleOptions);
				} else {
					mMarkerRainbow.add(mMapFragment.getMap().addMarker(new MarkerOptions()
			       		.position(mlat)
	//		            .title(listDriver.get(0).getString("first_name") + "\n" + listDriver.get(0).getString("last_name") + "\nCar Model: " + listDriver.get(0).getString("car_model"))
			       		.title(address == null ? "You're here!" : address)
			       		.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_taxi))
			          	.flat(true)
			          	.rotation(0)));
				}
			}
    	}
    }
    
    
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	    outState.putSerializable("saveState", "fap");
	}
	 
	@Override
	public void onResume() 
	{
		super.onResume();
		gpsTracker = new GPSTracker(context);
		if(Taxi_System.getSystem(context, "token").equalsIgnoreCase("") || Taxi_System.getSystem(context, "token") == null) 
			return;	
		Socket.socketDidConnect(context);
		/* For backgournd service by Ying */
		Intent mServiceIntent = new Intent(getActivity(), RSSPullService.class);
		
		getActivity().startService(mServiceIntent);
		/**********************************/				
		try {				
			HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
			Socket.socketDidSendMessage(Taxi_System.jsonString(
				    new String[]{"code","type"},
		    		new String[]{"0",Taxi_System.getSystem(context, "type")},
			 		new String[]{"token"},
			  		hash,
			  		null,null    		
			));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	        
	    if(Taxi_System.getSystem(context, "available").equalsIgnoreCase("1"))
	    {
	 		try {
	 			HashMap<String, JSONObject> hash1 = new HashMap<String, JSONObject>();
	 			hash1.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
	 			hash1.put("status", new JSONObject().put("status", "1"));
	 			Socket.socketDidSendMessage(Taxi_System.jsonString(
		 	    		new String[]{"code","type"},
		 	    		new String[]{"10",Taxi_System.getSystem(context, "type")},
		 	    		new String[]{"token","status"},
		 	    		hash1,
		 	    		null,null
		 	    ));
	 		} catch (JSONException e) {
	 			e.printStackTrace();
	 		}
	    	}
	    else
	    {
			try {
				HashMap<String, JSONObject> hash2 = new HashMap<String, JSONObject>();
				hash2.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
				hash2.put("status", new JSONObject().put("status", "2"));
				Socket.socketDidSendMessage(Taxi_System.jsonString(
					   		new String[]{"code","type"},
					   		new String[]{"10",Taxi_System.getSystem(context, "type")},
					   		new String[]{"token","status"},
					 		hash2,
					   		null,null
					    ));
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    }
//	    if(Taxi_System.checkGPS(context, getActivity()))
//			Taxi_System.didUpdatePosition(context, Driver_Fragment_Activity.getLocation());
	}
	
	@Override
	public void onDestroyView()
	{
		for(Fragment frag : getChildFragmentManager().getFragments())
		{
			if(frag != null)
			{
			    getChildFragmentManager().beginTransaction().
			    remove(frag).commitAllowingStateLoss();
			}
		}
	    super.onDestroyView();
	}
	    
	private void goToMyLocation()
	{
		String latitude = String.valueOf(getLatitude());
		String longitude = String.valueOf(getLongitude());
		String token = Taxi_System.getSystem(context, "token");
		String type = Taxi_System.getSystem(context, "type");
		new SendPostReqAsyncTask().execute(new String[]{token, latitude, longitude, type});
	}
		
	class SendPostReqAsyncTask extends AsyncTask<String, String, String>{
    	@Override
    	protected void onPreExecute()
    	{
    		if(Taxi_System.connectionStatus(context))
    		{
	    		Taxi_System.showDialog(context);
    		}
    		else
    		{
    			Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));    			
    			return;
    		}
        }
    	
        @Override
        protected String doInBackground(String... params) 
        {       		
        	String[] nameReq = new String[] {"token","lat","lng","type"};
	        return Taxi_System.sendRequest(Taxi_Constants.DRIVER_GPS_SEARCH, nameReq, params);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Taxi_System.notShow();
            Taxi_System.testLog(result);
            if(result!=null) {
	            JSONObject json;
				try {
					json = new JSONObject(result);
					latlong.clear();
					listDriver.clear();
				    latlong.add(new LatLng(getLatitude(), getLongitude()));
					if(json.getString("code").toString().equalsIgnoreCase("200"))
					{
						JSONArray cast = json.getJSONArray("info");
						if(cast.length() != 0) {
							for (int i=0; i<cast.length(); i++) 
							{
							    JSONObject objc = cast.getJSONObject(i);
							    latlong.add(new LatLng(Double.parseDouble(objc.getString("lat")), Double.parseDouble(objc.getString("lng"))));
							    listDriver.add(objc);
							}
						}
						setUpMapView();
					}
					else
					{
						Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
            }
        } 
    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.ubutton:			
			goToMyLocation();			
			break;
			
        case R.id.select:
			((Driver_Fragment_Activity)getActivity()).didPressSettings();
        	break;
		}	
	}

	public void convertAddress(String address) {
	    if (address != null && !address.isEmpty()) {
	        try {
	            List<Address> addressList = new Geocoder(context).getFromLocationName(address, 1);
	            if (addressList != null && addressList.size() > 0) {
	                double lat = addressList.get(0).getLatitude();
	                double lng = addressList.get(0).getLongitude();
	                Taxi_System.testLog(lat + "-"  + lng);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } 
	    } 
	} 
	

	@Override
	public boolean onMarkerClick(Marker marker) 
	{
			markerEffect(marker);
		return false;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
	}

	@Override
	public void onDisconnected() {
		
	}
	
	private void markerEffect(final Marker option)
	{
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(1 - interpolator
                        .getInterpolation((float) elapsed / duration), 0);
                option.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(getActivity(), "Click Info Window", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void DriverOnClick(String string) 
	{
//		isOn = false;
        getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.left,R.anim.right_b).replace(R.id.map, new Driver_Fragment_Off(),"driverOff").disallowAddToBackStack().commit();	
        Taxi_System.addSystem(context, "available", "0");
        String text = "<font color='red'>BUSY</font>  <font color='black'>Status</font>";
    	txtStatus.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    	HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
		try {
			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
			hash.put("status", new JSONObject().put("status", "2"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Socket.socketDidSendMessage(Taxi_System.jsonString(
		   	new String[]{"code","type"},
		   	new String[]{"10",Taxi_System.getSystem(context, "type")},
		   	new String[]{"token","status"},
		   	hash,
		   	null,null
		));
	}

	@Override
	public void DriverOffClick(String string) 
	{
//		isOn = true;
        getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.right, R.anim.left_b).replace(R.id.map, new Driver_Fragment_On(),"driverOn").disallowAddToBackStack().commit();	
        Taxi_System.addSystem(context, "available", "1");
        txtStatus.setText(context.getResources().getString(R.string.status_headline));
		try {
			HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
			hash.put("status", new JSONObject().put("status", "1"));
			Socket.socketDidSendMessage(Taxi_System.jsonString(
		    		new String[]{"code","type"},	    		
		    		new String[]{"10",Taxi_System.getSystem(context, "type")},
				 	new String[]{"token","status"},
				   	hash,
				  	null,null
			));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	class RequestUserInfor extends AsyncTask<String, String, String>{
    	@Override
    	protected void onPreExecute()
    	{
    		if(Taxi_System.connectionStatus(context))
    		{
//	    		Taxi_System.showDialog(context);
    		}
    		else
    		{
    			Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
    			return;
    		}
        }

        @Override
        protected String doInBackground(String... params) {
        	return Taxi_System.getInfor(Taxi_Constants.DRIVER_SERVICES + "info?token=" + Taxi_System.getSystem(context, "token"));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Taxi_System.notShow();
            Taxi_System.testLog(result);
            if(result==null) {
				Taxi_System.showToast(context, getResources().getString(R.string.check_internet_connection));
				return;
			}
            JSONObject json;
			try 
			{
				json = new JSONObject(result);
	            JSONObject parse = new JSONObject(json.getString("info"));
	    		Taxi_System.addSystem(context, "userinfo", parse.toString());
                setDetail(parse,getView());
			} catch (JSONException e) {
				e.printStackTrace();
			}
        } 
    }
	
	private void setDetail(JSONObject obj,View v) throws JSONException 
	{	
		Taxi_System.addSystem(context, "km", obj.getString("allow_distance")+"");
		Taxi_System.addSystem(context, "start_fee", obj.getString("starting_fee"));
		Taxi_System.addSystem(context, "per_km", obj.getString("price_per_km"));
		
		txtStartingFee.setText(obj.getString("starting_fee")+"");
		txtPerKm.setText(obj.getString("price_per_km")+"");
		txtDistance.setText(obj.getString("allow_distance") + " Km");

		
		String tariffs = obj.getString("tafiffs");
		if(tariffs!=null) {
			try {
				JSONObject jsTariffs = new JSONObject(tariffs);
				String tickNumber = jsTariffs.getString("tick");
				Taxi_System.addSystem(context, "opt", tickNumber+"");
				JSONArray jsArray = jsTariffs.getJSONArray("tariffs");
				if(jsArray!=null) {
/*					for(int n=0; n<jsArray.length(); n++)
						Taxi_System.addSystem(context, "fee" + String.valueOf(n), jsArray.getString(n)+"");*/
					/* For localizing by Ying */
					for(int n=0; n<jsArray.length(); n++)	{
						String str = jsArray.getString(n).replace("Fee", context.getResources().getString(R.string.fee_settings));
						str = str.replace("Empty", context.getResources().getString(R.string.empty_settings));
						Taxi_System.addSystem(context, "fee" + String.valueOf(n), str);						
					}
					/**************************/
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Taxi_System.addSystem(context, "tariffs", obj.getString("tafiffs")+"");	
		}
		float fee = Float.parseFloat(Taxi_System.getSystem(context, "start_fee"));
		float km = Float.parseFloat(Taxi_System.getSystem(context, "per_km"));
		if(fee==0||km==0)
			menuInterface.didPressSettings();
//		setingTariffs();
	}
	
//	private Dialog dialog;
//	private void setingTariffs() {
//		dialog = new Dialog(context);
//		dialog.setContentView(R.layout.adapter_setting);
//		dialog.setTitle(context.getResources().getString(R.string.edit_price));
////		alert.setMessage(context.getResources().getString(R.string.enter_your_initial_fee));	        
//		final EditText input1 = (EditText) dialog.findViewById(R.id.first);
//		final EditText input11 = (EditText) dialog.findViewById(R.id.first1);
//		final EditText input2 = (EditText) dialog.findViewById(R.id.second);
//		final EditText input22 = (EditText) dialog.findViewById(R.id.second1);
//		((CheckBox)dialog.findViewById(R.id.empty)).setVisibility(View.GONE);
//		((LinearLayout)dialog.findViewById(R.id.llButton)).setVisibility(View.VISIBLE);
//		((Button)dialog.findViewById(R.id.btnDone)).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				if((input1.length() == 0 && input11.length() != 0))
//					input1.setText("0");
//				if((input2.length() == 0 && input22.length() != 0))
//					input2.setText("0");
//				if((input11.length() == 0 && input1.length() != 0))
//					input11.setText("0");
//				if((input22.length() == 0 && input2.length() != 0))
//					input22.setText("0");
//				if((input1.length()==0 && input11.length()==0)||(input2.length()==0 && input22.length()==0)) {
//					Toast toast = new Toast(context);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.setDuration(Toast.LENGTH_SHORT);
//					toast.setText(context.getResources().getString(R.string.input_data_invalid));
//					toast.show();
//				} else {
//					dialog.cancel();
//				}
//			}
//		});
//		((Button)dialog.findViewById(R.id.btnCancel)).setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if((input1.length()==0 && input11.length()==0)||(input2.length()==0 && input22.length()==0)) {
//					Toast toast = Toast.makeText(context, context.getResources().getString(R.string.input_data_invalid), Toast.LENGTH_SHORT);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();
//				} else {
//					dialog.cancel();
//				}
//			}	
//		});
//		dialog.show();
//	}
	
	public static boolean isShowMap() {
		return isShowMap;
	}

	public static void setShowMap(boolean isShowMap) {
		Driver_Status.isShowMap = isShowMap;
	}

	@Override
	public void onAttachFinish() {
		((Button) getView().findViewById(R.id.ulatlong1)).setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		gpsTracker.stopUsingGPS();
	}
	
	private GPSTracker gpsTracker;
	private static double latitude; // latitude
    private static  double longitude; // longitude
    private static Location location; // location
    
    public static double getLatitude(){
        return latitude;
    }

    public static double getLongitude(){
        return longitude;
    }
    
    public static Location getLocation(){
        return location;
    }
	/**
     * Get GPS position
     */
/*    
    private class GPSTracker implements LocationListener {

    	private List<String> provider;
//        private double latitude; // latitude
//        private double longitude; // longitude
//        private Location location; // location
        
        // Declaring a Location Manager
        protected LocationManager locationManager;
        
        public GPSTracker(Context context) {
            setLocation();
        }
        
        public void setLocation() {
        	locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            provider = locationManager.getProviders(criteria, true);
            if (provider.isEmpty()){
            }
            else
            {
                for (String enabledProvider : provider)
                {
                    locationManager.requestSingleUpdate(enabledProvider, this, null);
                }
            }
        }
    
        public void stopUsingGPS(){
            if(locationManager != null){
                locationManager.removeUpdates(GPSTracker.this);
            }       
        }
        
        @Override
        public void onLocationChanged(Location mLocation) {
        	if (mLocation != null) {
           		latitude = mLocation.getLatitude();
           		longitude = mLocation.getLongitude();
           		location = mLocation;
           		Taxi_System.didUpdatePosition(context, location);
//           		goToMyLocation();
         	}
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
    	public void onProviderEnabled(String provider) { 
    	}

        @Override
    	public void onStatusChanged(String provider, int status, Bundle extras) {  
    	}
    }
*/   
    /* Google Play Services for Location by Ying */
    
    public void onStart()	{
    	super.onStart();
//    	gpsTracker.mLocationClient.connect();    	
    }
    
    public void onStop()	{
//    	gpsTracker.stopUsingGPS();
    	super.onStop();    	
    }       
   
    private class GPSTracker extends Service implements LocationListener, android.location.LocationListener {

    	private final Context mContext;

    	// flag for GPS status
    	boolean isGPSEnabled = false;

    	// flag for network status
    	boolean isNetworkEnabled = false;

    	// flag for GPS status
    	boolean canGetLocation = false;

//    	Location location = null; // location
//    	double latitude; // latitude
//    	double longitude; // longitude

    	// The minimum distance to change Updates in meters
    	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    	// The minimum time between updates in milliseconds
    	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    	// Declaring a Location Manager
    	protected LocationManager locationManager;

//    	long timeInMilliseconds = 0L;
//    	long timeSwapBuff = 0L;
//    	long updatedTime = 0L;
    	
    	public GPSTracker(Context context) {
    		this.mContext = context;
    		getLocation();

    	}

    	public Location getLocation() {
    		try {
    			locationManager = (LocationManager) mContext
    					.getSystemService(LOCATION_SERVICE);

    			// getting GPS status
    			isGPSEnabled = locationManager
    					.isProviderEnabled(LocationManager.GPS_PROVIDER);

    			// getting network status
    			isNetworkEnabled = locationManager
    					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    			if (!isGPSEnabled && !isNetworkEnabled) {
    				// no network provider is enabled
    			} else {
    				this.canGetLocation = true;
    				// if GPS Enabled get lat/long using GPS Services
    				if (isGPSEnabled) {
    					if (location == null) {
    						locationManager.requestLocationUpdates(
    								LocationManager.GPS_PROVIDER,
    								MIN_TIME_BW_UPDATES,
    								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    						Log.d("GPS", "GPS Enabled");
    						if (locationManager != null) {
    							location = locationManager
    									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    							if (location != null) {
    								latitude = location.getLatitude();
    								longitude = location.getLongitude();
    								Taxi_System.didUpdatePosition(context, location);
    							}
    						}
    					}
    				}
    				
    				if (isNetworkEnabled) {
    					locationManager.requestLocationUpdates(
    							LocationManager.NETWORK_PROVIDER,
    							MIN_TIME_BW_UPDATES,
    							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    					Log.d("Network", "Network Enabled");
    					if (locationManager != null) {
    						location = locationManager
    								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    						if (location != null) {
    							latitude = location.getLatitude();
    							longitude = location.getLongitude();
    							Taxi_System.didUpdatePosition(context, location);
    						}
    					}
    				}
    			}

    		} catch (Exception e) {
    			e.printStackTrace();
    		}

    		return location;
    	}

    	/**
    	 * Stop using GPS listener Calling this function will stop using GPS in your
    	 * app
    	 * */
    	public void stopUsingGPS() {
    		if (locationManager != null) {
    			locationManager.removeUpdates(GPSTracker.this);
    		}
    	}

    	/**
    	 * Function to get latitude
    	 * */
    	public double getLatitude() {
    		if (location != null) {
    			latitude = location.getLatitude();
    		}

    		// return latitude
    		return latitude;
    	}

    	/**
    	 * Function to get longitude
    	 * */
    	public double getLongitude() {
    		if (location != null) {
    			longitude = location.getLongitude();
    		}

    		// return longitude
    		return longitude;
    	}

    	/**
    	 * Function to check GPS/wifi enabled
    	 * 
    	 * @return boolean
    	 * */
    	public boolean canGetLocation() {
    		return this.canGetLocation;
    	}


    	@Override
    	public void onLocationChanged(Location mlocation) {
        	if (mlocation != null) {
        		location = mlocation;
           		latitude = mlocation.getLatitude();
           		longitude = mlocation.getLongitude();
           		Taxi_System.didUpdatePosition(context, location);
         	}
    	}

    	@Override
    	public void onProviderDisabled(String provider) {
    	}

    	@Override
    	public void onProviderEnabled(String provider) {
    	}

    	@Override
    	public void onStatusChanged(String provider, int status, Bundle extras) {
    	}

    	@Override
    	public IBinder onBind(Intent arg0) {
    		return null;
    	}
    	
    }
//    private class GPSTracker implements GooglePlayServicesClient.ConnectionCallbacks, 
//    									GooglePlayServicesClient.OnConnectionFailedListener, 
//    									LocationListener	{
//    	private static final int MILLISECONDS_PER_SECOND = 1000;
//    	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
//    	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
//    	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
//    	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
//    	
//    	LocationRequest mLocationRequest;
//    	LocationClient mLocationClient;
//    	boolean mUpdatesRequested;    	
//    	
//    	public GPSTracker(Context context)	{
//    		mLocationRequest = LocationRequest.create();
//    		
//    		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    		mLocationRequest.setInterval(UPDATE_INTERVAL);
//    		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//    		
//    		mLocationClient = new LocationClient(context, this, this);
//    		mUpdatesRequested = true;    		
//    	}
//    	
//    	@Override
//    	public void onConnected(Bundle dataBundle)	{    		
//    		if (mUpdatesRequested)	{
//    			mLocationClient.requestLocationUpdates(mLocationRequest, this);
//    		}    		
//    	}
//    	
//    	@Override
//    	public void onDisconnected()	{    		
//    	}   
//    	
//    	@Override
//    	public void onConnectionFailed(ConnectionResult connectionResult)	{    		
//    	}
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
//        public void stopUsingGPS(){
//    		if (mLocationClient.isConnected())	{
//    			mLocationClient.removeLocationUpdates(this);
//    		}
//    		
//    		mLocationClient.disconnect();            
//        }        
//    }    
}
