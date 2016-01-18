package com.libreteam.taxi;

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
import com.libreteam.taxi.R;
import com.libreteam.taxi.extra.CustomMapFragment;
import com.libreteam.taxi.extra.ImageLoader;
import com.libreteam.taxi.extra.PathJSONParser;
import com.libreteam.taxi.extra.Socket;
import com.libreteam.taxi.extra.Customer_Constants;
import com.libreteam.taxi.extra.CustomMapFragment.OnMapReadyListener;

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

public class Customer_Boarding extends Fragment implements OnMapReadyListener, OnClickListener, 
															OnInfoWindowClickListener, OnMarkerClickListener {

	private CustomMapFragment  mMapFragment;
	private Context context;
    private String ride_id,customer_id,address,latlng,username,car_model,taxi_company,license_plate,time;
	
	private static LatLng driverLatLng = null;
	private static LatLng customerLatLng = null;
	private boolean isTracking;
	public Timer timer;
	
	@SuppressLint("InflateParams")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_boarding_, null);
	    context = view.getContext();
		isTracking = false;

	    ride_id = getArguments().getStringArray("rideid")[0];
	    customer_id = getArguments().getStringArray("rideid")[1];
	    address = getArguments().getStringArray("rideid")[2];
	    latlng = getArguments().getStringArray("rideid")[3];
	    car_model = getArguments().getStringArray("rideid")[4];
	    time = getArguments().getStringArray("rideid")[5];
//	    username= getArguments().getStringArray("rideid")[6];
	    username= Customer_Constants.selected_taxi_appname; //jin
	    taxi_company= getArguments().getStringArray("rideid")[7];
//	    license_plate = getArguments().getStringArray("rideid")[8];
	    license_plate = Customer_Constants.selected_plate_number;
//	    avatar = getArguments().getStringArray("rideid")[9];
	    Taxi_System.testLog(address);
    
	    ArrayList aList= new ArrayList(Arrays.asList(latlng.split(",")));
	    ArrayList d_aList= new ArrayList(Arrays.asList(getArguments().getStringArray("rideid")[12].split(",")));
	    customerLatLng = new LatLng(Double.parseDouble(d_aList.get(0).toString()), Double.parseDouble(d_aList.get(1).toString()));
		driverLatLng = new LatLng(Double.parseDouble(aList.get(0).toString()), Double.parseDouble(aList.get(1).toString()));
	
		if (savedInstanceState == null) 
			initComponents(view);
		
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	   	view.setOnKeyListener(new View.OnKeyListener() {
	   		@Override
	   		public boolean onKey(View v, int keyCode, KeyEvent event) {
	   			if( keyCode == KeyEvent.KEYCODE_BACK ) {
	   				if(((Customer_Fragment_Activity)getActivity()).isMenu)
	   				{
	   					((Customer_Fragment_Activity)getActivity()).didHideMenu();
	   					return true;
	   				}
	   	         	didRejectRide();
	   	         	((Customer_Fragment_Activity)getActivity()).didFinish();
	   	         	return true;
	   			} else 
	    			return false;
	    	}
	    }); 
	    return view;
	}

	public void didGetPosition(final LatLng driver)
	{
		driverLatLng = driver;
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable(){
			@Override
			public void run() {
						
			} 
		});
	}
	   
	private void initComponents(View v)
	{
		Taxi_System.setContent((LinearLayout)v.findViewById(R.id.header),context,1,(float) 0.07);
		Taxi_System.applyFonts(v, Taxi_System.faceType_normal(getActivity()));
		
		LinearLayout thirdsection = (LinearLayout)v.findViewById(R.id.thirdsection);
		Taxi_System.setContent(thirdsection, context, (float) 0.125, (float)0.07);
		
		ImageView thirdimg = (ImageView)v.findViewById(R.id.thirdimg);
		Taxi_System.setContent(thirdimg, context, (float)0.06, (float)0.04);
		
		LinearLayout logosection = (LinearLayout)v.findViewById(R.id.logosection);
		Taxi_System.setContent(logosection, context, (float) 0.25, (float)0.07);
		
		ImageView logo = (ImageView)v.findViewById(R.id.logo);
//		Taxi_System.setContent(logo, context, Float.parseFloat((String) getResources().getText(R.string.logosize_w)), Float.parseFloat((String) getResources().getText(R.string.logosize)));
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		
		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.06);
		
		((TextView)v.findViewById(R.id.username)).setText(username);
		((TextView)v.findViewById(R.id.carmodel)).setText(car_model);
		((TextView)v.findViewById(R.id.taxicompany)).setText(taxi_company);
		((TextView)v.findViewById(R.id.licenseplate)).setText(license_plate);
		((TextView)v.findViewById(R.id.time)).setText(time.replace("s", ""));
		((TextView)v.findViewById(R.id.time)).setTypeface(null, Typeface.BOLD);
		((TextView)v.findViewById(R.id.licenseplate)).setTypeface(null, Typeface.BOLD);
//        new ImageLoader(context).DisplayImage("http://www.thetorquereport.com/assets_c/2013/02/2014_Ford_Fiesta_ST_Race_Car_00010-thumb-530x398-26165.jpg", (ImageView)v.findViewById(R.id.icon));
		new ImageLoader(context).DisplayImage(Customer_Constants.selected_taxi_icon, (ImageView)v.findViewById(R.id.icon)); //jin
        ((ImageView)v.findViewById(R.id.icon)).getLayoutParams().height =  (int) (Taxi_System.getWidth(context) * 0.25);
        ((ImageView)v.findViewById(R.id.icon)).getLayoutParams().width =  (int) (Taxi_System.getWidth(context) * 0.25);
        
		Button reject = (Button) v.findViewById(R.id.reject);
		reject.setOnClickListener(this);
		Button accept = (Button) v.findViewById(R.id.accept);
		accept.setOnClickListener(this);
		Button urlatlong = (Button) v.findViewById(R.id.update);
	    urlatlong.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.07);
	    urlatlong.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
		urlatlong.setOnClickListener(this);
		((TextView)v.findViewById(R.id.reject)).setTypeface(null, Typeface.BOLD);
		((TextView)v.findViewById(R.id.accept)).setTypeface(null, Typeface.BOLD);
        if(mMapFragment == null)
        {
		    mMapFragment = CustomMapFragment.newInstance();
	        getChildFragmentManager().beginTransaction().replace(R.id.map, mMapFragment,"customer_boarding").commit();		
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
  
	private void addMarker(final List<LatLng> latlng, GoogleMap mMap)
	{
		String latitude = Taxi_System.getSystem(context, Customer_Constants.LATITUDE);
		String lng = Taxi_System.getSystem(context, Customer_Constants.LONGITUDE);
		int i = 0;
		int resourceId = 0;
		String title = null;
		mMapFragment.getMap().clear();
		for(LatLng lat : latlng)
		{
			if(i == 0)
			{
				if(latitude.equals("") | lng.equals(""))
					title = "You're here!";
				else 
					title = Taxi_System.getAddressLine(context, Double.valueOf(latitude), Double.valueOf(lng));
				resourceId = R.drawable.map_taxi_green;
			}
			else
			{
				if(latitude.equals("") | lng.equals(""))
					title = "Taxi's here!";
				else 
					title = Taxi_System.getAddressLine(context, Double.valueOf(latitude), Double.valueOf(lng));
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

			try {
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
			Taxi_System.testLog(routes);
			PolylineOptions polyLineOptions = null;

//			String distance = "";
//			String duration = "";
			if(routes==null) {
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
    
	private void didRejectRide()
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
	
	public void didUpdatePosition()
	{
		if(timer == null)
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() 
		{
		    @Override
		    public void run() {
		    	
		    	Handler handler = new Handler(Looper.getMainLooper());
 				handler.post(new Runnable(){
					@Override
					public void run() {
//				    	startTracking(driverLatLng);
						setUpMapView();
					} 
 				});
		    }
		}, 0, 6000);
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
		case R.id.update:
			if(!isTracking)
			{
				Taxi_System.didGetPosition(context, customer_id);
				Taxi_System.showToast(context, context.getResources().getString(R.string.start_tracking));
				didUpdatePosition();
			}
			else
			{
				Taxi_System.didEndGetPosition();
				Taxi_System.showToast(context, context.getResources().getString(R.string.stop_tracking));
				didEndUpdatePosition();
			}
			isTracking =! isTracking;
			break;
		case R.id.reject:
			((Customer_Fragment_Activity)getActivity()).didRejectRide();
        	didRejectRide();
			break;
		case R.id.accept:
//			Taxi_System.didAddFragment(this, new Customer_Finish_Rating(), "rideid",new String[]{
//        		ride_id,customer_id,
//        		gpsTracker.getAddressLine(context) == null ? "Not Available" : gpsTracker.getAddressLine(context),
//        		String.format("%.0f,%.0f", gpsTracker.getLatitude(),gpsTracker.getLongitude())
//        		},true);
			((Customer_Fragment_Activity)getActivity()).didAcceptRide(new String[]{
        		ride_id,
        		customer_id,
        		getArguments().getStringArray("rideid")[11],
        		String.valueOf(driverLatLng.latitude) + "," + String.valueOf(driverLatLng.longitude),
        		getArguments().getStringArray("rideid")[10],
        		getArguments().getStringArray("rideid")[12]
			});
			
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
		    	new String[]{"token","ride_id","id"},
		    	hash, null,null
			));
        	break;
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker)
	{

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}
}
