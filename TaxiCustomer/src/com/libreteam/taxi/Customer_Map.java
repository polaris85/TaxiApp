package com.libreteam.taxi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
//import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.location.LocationClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.libreteam.taxi.extra.CustomMapFragment;
import com.libreteam.taxi.extra.CustomMapFragment.OnMapReadyListener;
import com.libreteam.taxi.extra.PlaceJSONParser;
import com.libreteam.taxi.extra.Socket;
import com.libreteam.taxi.extra.Customer_Constants;
@SuppressLint("ClickableViewAccessibility")
public class Customer_Map extends Fragment implements OnClickListener, OnMarkerClickListener, 
															OnInfoWindowClickListener, OnMapReadyListener {
	public static AutoCompleteTextView atvPlaces, atvDriver;
	private ParserTask parserTask;
	private Adapter1 adapter1;
    private LatLng uLatLng ;
	private Context context;
	private String saveInstance;
	private boolean isYou,isDriver,isTop,isFirst;
    private final List<Marker> mMarkerRainbow = new ArrayList<Marker>(); 
    private final List<LatLng> latlong = new ArrayList<LatLng>(); 
    private List<LatLng> saveLatLng = new ArrayList<LatLng>();
	private final List<JSONObject> listDriver = new ArrayList<JSONObject>();
	private String arr;
	private CustomMapFragment  mMapFragment;
	private int mCurrentlyShowingFragment;
    private static final String SAVED_STATE_KEY = Customer_Map.class.getSimpleName();
	private Date _lastTypeTime = null;
	private Boolean isDisable;
	private String token, type;
//	GPSTracker gps;
	private CountDownTimer countDown;
	
    public static Customer_Map newInstance() {
        return new Customer_Map();
    }
    
	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_map, null);
		context = view.getContext();
		token = Taxi_System.getSystem(context, "token");
		type = Taxi_System.getSystem(context, "type");		

		Taxi_System.addSystem(context, "start_fee", "");
	    Taxi_System.addSystem(context, "per_km", "");
	    Taxi_System.testLog(Taxi_System.getSystem(context, "token"));
	    gpsTracker = new GPSTracker(context);
	    initComponentsView(view);
		initComponents(view);
		setUpYourLocation(view);
		setUpDriverLocation(view);
//		didRequestConnect();
//		gpsTracker = new LocationTracker(context);	//	added by Ying
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
	                return true;
	            } else {
	                return false;
	            }
	        }
	    });
		
		new PlacesTaskReverse().execute(String.valueOf(getLatitude())+","+String.valueOf(getLongitude()));
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		gpsTracker = new GPSTracker(context);
		/* Ying added for Google Location Services */
//    	gpsTracker.mLocationClient.connect();
    	/*******************************************/
		Socket.socketDidConnect(context);
		didRequestConnect();
	    countTime();
	}
	
	@Override
	public void onPause()
	{
		((MyApplication)getActivity().getApplication()).setFragmentSavedState(SAVED_STATE_KEY, getFragmentManager().saveFragmentInstanceState(this));
		if(atvDriver!= null && atvPlaces!= null) {
			atvPlaces.clearFocus();
			atvDriver.clearFocus();
		}
		/* Ying added for Google Location Services */
//		if (gpsTracker.mLocationClient.isConnected())	{
//			gpsTracker.mLocationClient.removeLocationUpdates(gpsTracker);
//		}
//		
//		gpsTracker.mLocationClient.disconnect();
		/*******************************************/		
		super.onPause();		
	}
	
	private void didRequestConnect()
	{
		HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
		try 
		{
			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	    Socket.socketDidSendMessage(Taxi_System.jsonString(
	    		new String[]{"code","type"},
	    		new String[]{"0",Taxi_System.getSystem(context, "type")},
	    		new String[]{"token"},
	    		hash, null, null
		));
	}
		
	private void initComponentsView(View v)
	{
		atvPlaces = (AutoCompleteTextView) v.findViewById(R.id.uLoc);
	    Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));
	    Taxi_System.setContent((LinearLayout)v.findViewById(R.id.header),context,1,(float) 0.07);
		isYou = isDriver = isDisable = isFirst = false;
		
		LinearLayout thirdsection = (LinearLayout)v.findViewById(R.id.thirdsection);
		Taxi_System.setContent(thirdsection, context, (float) 0.125, (float)0.07);
		
		ImageView thirdimg = (ImageView)v.findViewById(R.id.thirdimg);
		Taxi_System.setContent(thirdimg, context, (float)0.06, (float)0.04);

		
		LinearLayout logosection = (LinearLayout)v.findViewById(R.id.logosection);
		Taxi_System.setContent(logosection, context, (float) 0.25, (float)0.07);
		
		ImageView logo = (ImageView)v.findViewById(R.id.logo);
//		Taxi_System.setContent(logo, context, Float.parseFloat((String) getResources().getText(R.string.logosize_w)), Float.parseFloat((String) getResources().getText(R.string.logosize)));
//		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.06);
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		  
		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.06);
		
		LinearLayout uFrame = (LinearLayout)v.findViewById(R.id.uFrame);
		Taxi_System.setContent(uFrame, context, 1, (float)0.08);
		
		LinearLayout dFrame = (LinearLayout)v.findViewById(R.id.dFrame);
		Taxi_System.setContent(dFrame, context, 1, (float)0.08);
			
		((LinearLayout)v.findViewById(R.id.first)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.09);
		((LinearLayout)v.findViewById(R.id.first)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.08);
		
		((RelativeLayout)v.findViewById(R.id.second)).getLayoutParams().width = Taxi_System.getWidth(context) - (int) (Taxi_System.getHeight(context) * 0.09) * 2;
		
		Button u = (Button) v.findViewById(R.id.ubutton);
		u.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.075);
		u.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.065);
		u.setOnClickListener(this);

		((LinearLayout)v.findViewById(R.id.third)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.09);
		((LinearLayout)v.findViewById(R.id.third)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.08);


		((LinearLayout)v.findViewById(R.id.forth)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.09);
		((LinearLayout)v.findViewById(R.id.forth)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.08);
		
		((RelativeLayout)v.findViewById(R.id.fifth)).getLayoutParams().width = Taxi_System.getWidth(context) - (int) (Taxi_System.getHeight(context) * 0.09) * 2;
		
		Button d = (Button) v.findViewById(R.id.dbutton);
		d.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.075);
		d.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.065);
		d.setOnClickListener(this);
		
		((LinearLayout)v.findViewById(R.id.sixth)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.09);
		((LinearLayout)v.findViewById(R.id.sixth)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.08);
		
		Button urlatlong = (Button) v.findViewById(R.id.ulatlong);
	    urlatlong.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.07);
	    urlatlong.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
		urlatlong.setOnClickListener(this); 
		
		Button select = (Button) v.findViewById(R.id.select);
		select.setOnClickListener(this);
		((TextView)v.findViewById(R.id.select)).setTypeface(null, Typeface.BOLD);
	
		Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "pt_sans.ttf");		   
		((EditText)v.findViewById(R.id.dLoc)).setTypeface(font1);
		new RequestUserInfor().execute();
	}
	
//	private void updateInformation() {
//		String address = Taxi_System.getAddressLine(context, gps.getLatitude(), gps.getLongitude());
//		atvPlaces.setText(address);
//		goToMyLocation(new String[]{gps.getLatitude()+"", gps.getLongitude()+""});
//	}
	
	private void setUpYourLocation(final View v)
	{
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "pt_sans.ttf");		   
		atvPlaces.setTypeface(font);
		atvPlaces.setThreshold(5);		
		atvPlaces.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				isDisable = true;
				isFirst = true;
				isTop = true; 
				return false;
			}
		}); 
	    
		atvPlaces.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
				isDisable = true;
				_lastTypeTime = new Date();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(final Editable s) {
				Timer t = new Timer();
			    TimerTask tt = new TimerTask()
			    {
			        @Override
			        public void run()
			        {
			            Date myRunTime = new Date();
			            if ((_lastTypeTime.getTime() + 1000) <= myRunTime.getTime())
			            {
			                v.post(new Runnable()
			                {
			                    @Override
			                    public void run()
			                    {
			                        if(s.length() != 0 && isDisable && isFirst) //return;
			                        {
				                        new PlacesTask().execute(s.toString());
				                        try {
				                        ((ImageView)getView().findViewById(R.id.green)).setVisibility(View.GONE);
				                        ((ProgressBar)getView().findViewById(R.id.green_p)).setVisibility(View.VISIBLE);
				                        } catch (Exception e) {
				                        	
				                        }
			                        }
			                        Log.d("<tag>", "typing finished >>>> !!!");
			                    }
			                });
			            } else
			            	Log.d("<tag>", "Canceled");
			        }
			    };
			    t.schedule(tt, 1500);
			}
		});	
		atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @SuppressWarnings("rawtypes")
			@Override
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					isDisable = false;
					InputMethodManager imm1 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			        imm1.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
					JSONObject objc = new JSONObject((Map) adapter1.getItem(position));
					new PlacesTaskDetail().execute(objc.getString("reference"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
		      }
		});
	}
	
	private void setUpDriverLocation(final View v)
	{
		atvDriver = (AutoCompleteTextView) v.findViewById(R.id.dLoc);
        atvDriver.clearFocus();
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "pt_sans.ttf");		   
		atvDriver.setTypeface(font);
		atvDriver.setThreshold(5);		
		atvDriver.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event){
				isDisable = true;
				isFirst = true;
				isTop = false;
			    return false;
			}
		}); 
	    
		atvDriver.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
				isDisable = true;
				_lastTypeTime = new Date();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(final Editable s) {	
				Timer t = new Timer();
			    TimerTask tt = new TimerTask()
			    {
			        @Override
			        public void run()
			        {
			            Date myRunTime = new Date();
			            if ((_lastTypeTime.getTime() + 1000) <= myRunTime.getTime())
			            {
			                v.post(new Runnable()
			                {
			                    @Override
			                    public void run()
			                    {
			                        if(s.length() != 0 && isDisable && isFirst) //return;
			                        {
			                        	Log.d("<tag>", "typing finished <<<<<!!!");
			                        	new PlacesTask().execute(s.toString());
			                        	((ImageView)getView().findViewById(R.id.red)).setVisibility(View.GONE);
			                        	((ProgressBar)getView().findViewById(R.id.red_p)).setVisibility(View.VISIBLE);
			                        }
			                    }
			                });
			            } else
			                Log.d("<tag>", "Canceled");
			        }
			    };
			    t.schedule(tt, 1500);
			}
		});	
		
		atvDriver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressWarnings("rawtypes")
			@Override
		 	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					isDisable = false;
					InputMethodManager imm1 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			        imm1.hideSoftInputFromWindow(atvDriver.getWindowToken(), 0);
					JSONObject objc = new JSONObject((Map) adapter1.getItem(position));
					new PlacesTaskDetail().execute(objc.getString("reference"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initComponents(View v) {
        if(mMapFragment == null)
        {
		    mMapFragment = CustomMapFragment.newInstance();
	        getChildFragmentManager().beginTransaction().replace(R.id.mapfrag, mMapFragment,"fragmap").commit();		
        }
	}

    @Override
    public void onMapReady() {    	
    	if(mMapFragment!=null) {
    		if(mMapFragment.getMap() != null)
    		{
				Taxi_System.setUiMap(mMapFragment.getMap());
		        mMapFragment.getMap().setOnInfoWindowClickListener(this);
		        mMapFragment.getMap().setOnMarkerClickListener(this);
			    if(saveLatLng != null && saveLatLng.size() != 0)
			    {
			    	listDriver.clear();
			    	JSONArray cast = null;
			    	if(latlong.size() ==0)
			    		latlong.add(new LatLng(saveLatLng.get(0).latitude,saveLatLng.get(0).longitude));//(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
					if(saveInstance != null && saveInstance != "")	
						try {
							cast = new JSONArray(saveInstance);
							for (int i=0; i<cast.length(); i++) 
							{
								JSONObject objc = cast.getJSONObject(i);
								latlong.add(new LatLng(Double.parseDouble(objc.getString("lat")), Double.parseDouble(objc.getString("lng"))));
								listDriver.add(objc);
							}
							setUpMapView();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					else
						setUpMapView();
			    }
			    else 
				    goToMyLocation(new String[]{String.valueOf(getLatitude()), String.valueOf(getLongitude())}); 
    		}
    	}
    	isDisable = false;
    }
	
    private void setUpMapView()
    {
        try 
        {
			addMarkersToMap();
		} catch (JSONException e) {
			e.printStackTrace();
		} 

        Taxi_System.zoomToLocation(latlong.get(0).latitude, latlong.get(0).longitude, mMapFragment.getMap());
    }
    
	@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putInt("currently_showing_fragment", mCurrentlyShowingFragment);
		saveInstance = arr;
		saveLatLng = latlong;
	}
    
	@Override
	public void onStop() {
		/* Ying added for Google Location Services */
//		if (gpsTracker.mLocationClient.isConnected())	{
//			gpsTracker.mLocationClient.removeLocationUpdates(gpsTracker);
//		}
//		
//		gpsTracker.mLocationClient.disconnect();
		/*******************************************/
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		((MyApplication)getActivity().getApplication()).setFragmentSavedState(SAVED_STATE_KEY, null);
		if(atvDriver!= null && atvPlaces!= null) {
			atvPlaces.clearFocus();
			atvDriver.clearFocus();
		}
		super.onDestroy();
		stopCountTime();
	}
	
	private void addMarkersToMap() throws JSONException {
		if(mMapFragment!=null && mMapFragment.getMap()!=null)
			mMapFragment.getMap().clear();
		else 
			return;
	   	for (LatLng lat : latlong)
		{  
	   		if(latlong.indexOf(lat) == 0)
			{
	   			mMarkerRainbow.add(mMapFragment.getMap().addMarker(new MarkerOptions()
			    	.position(lat)
			      	.title(Taxi_System.getAddressLine(context, getLatitude(), getLongitude()) == null 
			      				? "You're here!" : Taxi_System.getAddressLine(context, getLatitude(), getLongitude()))
			      	.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_taxi_green))
			       	.flat(true)
			      	.rotation(0)));
	   			CircleOptions circleOptions = new CircleOptions()
		   	        .center(lat)
		   	        .radius(0)
		   	        .strokeWidth(2)
		   	        .strokeColor(Color.TRANSPARENT)
		   	        .fillColor(Color.TRANSPARENT);
		   	        mMapFragment.getMap().addCircle(circleOptions);		   	        
		 	} else {
		 		mMarkerRainbow.add(mMapFragment.getMap().addMarker(new MarkerOptions()
		       		.position(lat)
		          	.title(listDriver.get(latlong.indexOf(lat) - 1).getString("first_name") + "\n" + listDriver.get(latlong.indexOf(lat) - 1).getString("last_name") + "\nCar Model: " + listDriver.get(latlong.indexOf(lat) - 1).getString("car_model"))
		          	.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_taxi))
		        	.flat(true)
		        	.rotation(0)));
			}
		}
	}
	    
	private void goToMyLocation(String[] latlng)
	{
		isDisable = false;		
		new SendPostReqAsyncTask().execute(new String[]{token, latlng[0],latlng[1], type});
	}
		
	class SendPostReqAsyncTask extends AsyncTask<String, String, String>{
		String lat,lng;
    	@Override
    	protected void onPreExecute()
    	{
    		if(Taxi_System.connectionStatus(context)) {
	    		Taxi_System.showDialog(context);	    		
    		} else {
    			Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));    			
    			return;
    		}
        }
    	
        @Override
        protected String doInBackground(String... params) 
        {       		
        	String[] nameReq = new String[] {"token","lat","lng","type"};
        	lat = params[1];
        	lng = params[2];
	        return Taxi_System.sendRequest(Customer_Constants.CUSTOMER_GPS_SEARCH, nameReq, params);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Taxi_System.notShow();
            Taxi_System.testLog(result);
            if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
            JSONObject json;
            if(result!=null) {
				try {
					json = new JSONObject(result);
					latlong.clear();
					listDriver.clear();
				    latlong.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
					if(json.getString("code").toString().equalsIgnoreCase("200"))
					{
						JSONArray cast = json.getJSONArray("info");
						if(cast.length() == 0)
						{
							setUpMapView();
							Taxi_System.showToast(context, context.getResources().getString(R.string.no_drivers_available));
						    return;
						};
						arr = cast.toString();
						for (int i=0; i<cast.length(); i++) 
						{
						    JSONObject objc = cast.getJSONObject(i);
						    latlong.add(new LatLng(Double.parseDouble(objc.getString("lat")), Double.parseDouble(objc.getString("lng"))));
						    listDriver.add(objc);
							Taxi_System.testLog(objc.getString("minute"));
						}
						setUpMapView();
					}
					else
						Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
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
		case R.id.ulatlong:
			atvPlaces.setText(Taxi_System.getAddressLine(context, getLatitude(), getLongitude())+"");
		    new PlacesTaskReverse().execute(String.valueOf(getLatitude())+","+getLongitude());
			goToMyLocation(new String[]{getLatitude()+"", getLongitude()+""});
			setUpDriverLocation(getView());
		    atvPlaces.clearFocus();
	        atvDriver.clearFocus();
		   break;
		case R.id.ubutton:
			atvPlaces.requestFocus();
			atvPlaces.setText("");
	        InputMethodManager imm1 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm1.showSoftInput(atvPlaces, InputMethodManager.SHOW_IMPLICIT);
	        isDisable = true;
	        isFirst = true;
	        isTop = true;
			isYou =! isYou;
			break;
        case R.id.dbutton:
        	if(!isDriver)
			{
		        ((RelativeLayout)getView().findViewById(R.id.fifth)).setVisibility(View.VISIBLE);
		        ((Button)getView().findViewById(R.id.dbutton)).setBackgroundResource(R.drawable.button_x);
		        ((EditText)getView().findViewById(R.id.dLoc)).requestFocus();
		        atvDriver.requestFocus();
		        atvDriver.setText("");
		        InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm2.showSoftInput(atvDriver, InputMethodManager.SHOW_IMPLICIT);
		        isDisable = true;
		        isFirst = true;
		        isTop = false;
			}
        	else
			{
		        ((RelativeLayout)getView().findViewById(R.id.fifth)).setVisibility(View.GONE);
		        ((Button)getView().findViewById(R.id.dbutton)).setBackgroundResource(R.drawable.button_pen);
		        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(atvDriver.getWindowToken(), 0);
		        isDisable = false;
		        isTop = true;
			}
        	isDriver =! isDriver;
			break;
        case R.id.select:
        	if(arr != null && latlong.size() > 1)
        	{
        		if((atvPlaces.getText().toString().length() >= 5) && latlong.size() != 0 )
        		{
        			if((atvDriver.getText().toString().length() >= 5) && latlong.size() != 0 && uLatLng != null) {
	        			((Customer_Fragment_Activity)getActivity()).didGetListDriver(
	        				new String[]{
	        		    		arr,
	        		    		String.valueOf(latlong.get(0).latitude),
	        		    		String.valueOf(latlong.get(0).longitude),
	        		    		atvPlaces.getText().toString(),
	        		    		String.valueOf(uLatLng.latitude),
	        		    		String.valueOf(uLatLng.longitude),
	        		    		atvDriver.getText().toString()
	        		 	});
        			} else {
        				((Customer_Fragment_Activity)getActivity()).didGetListDriver(
	        				new String[]{
	        		    		arr,
	        		    		String.valueOf(latlong.get(0).latitude),
	        		    		String.valueOf(latlong.get(0).longitude),
	        		    		atvPlaces.getText().toString(), 
	        		    		"0.0", "0.0", ""
	        		 	});
        			}
        		} else 
        			Taxi_System.showToast(context, context.getResources().getString(R.string.please_enter_your_pickup));
        	}
        	else	
        	{
        		if(latlong!=null && latlong.size()>0)
        		new SendPostReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token"),
        				String.valueOf(latlong.get(0).latitude),String.valueOf(latlong.get(0).longitude), "user"});
        	}
        	break;
		}	
	}

	@Override
	public boolean onMarkerClick(Marker marker) 
	{
		markerEffect(marker);
		return false;
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
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
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

        	return Taxi_System.getInfor(Customer_Constants.CUSTOMER_SERVICE + "info?token=" + Taxi_System.getSystem(context, "token"));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Taxi_System.notShow();
            Taxi_System.testLog(result);
            if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
            JSONObject json ;
        try {
			json = new JSONObject(result);
            switch(Integer.parseInt(json.getString("code").toString()))
            {
            	case 200:
            		JSONObject parse = new JSONObject(json.getString("info"));
            		Taxi_System.addSystem(context, "username", parse.getString("full_name"));
            		Taxi_System.addSystem(context, "userinfo", parse.toString());
     			   	break;
                case 401:
                case 500:
                case 400:
                	Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
                	break;
     		   	default:
     		   		break;
            	}
            }
        	catch (JSONException e) {
        		e.printStackTrace();
			}            
        } 
    }
	
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
        	URL url = new URL(strUrl);                
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
          	StringBuffer sb  = new StringBuffer();
          	String line = "";
         	while( ( line = br.readLine())  != null){
         		sb.append(line);
          	}
         	data = sb.toString();
         	br.close();
        } catch (Exception e){
        	Log.d("Exception while downloading url", e.toString());
        } finally {
        	inputStream.close();
        	urlConnection.disconnect();
        }
        return data;
	}	
	
    private class PlacesTaskDetail extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... place) {
			String data = "";
			String url = "https://maps.googleapis.com/maps/api/place/details/json?reference=" + place[0] +"&key=" + Customer_Constants.API_MAP;
			try{
				data = downloadUrl(url);
			}catch(Exception e){
                Log.d("Background Task",e.toString());
			}
			return data;		
		}
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);
			if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
			try {
				JSONObject last = new JSONObject(new JSONObject(new JSONObject(new JSONObject(result).getString("result")).getString("geometry")).getString("location"));
				Log.e("+++",""+ last.getString("lat"));
				Log.e("---",""+ last.getString("lng"));
				if(isTop)
				{
					goToMyLocation(new String[]{last.getString("lat"),last.getString("lng")});
					saveLatLng.clear();
					saveLatLng.add(new LatLng(Double.parseDouble(last.getString("lat")),Double.parseDouble(last.getString("lng"))));
				}
				else
					uLatLng = new LatLng(Double.parseDouble(last.getString("lat")),Double.parseDouble(last.getString("lng")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}		
	}
    
	private class PlacesTask extends AsyncTask<String, Void, String>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Taxi_System.showDialog(context);
		}
		
		@Override
		protected String doInBackground(String... place) {
			String data = "";
			String key = Customer_Constants.API_MAP;
			String input = "";
			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}		
			String types = "types=geocode";
			String sensor = "sensor=true";			
			String parameters = input+"&"+types+"&"+sensor+"&"+"key=" +key;
			String output = "json";
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

			try{
				data = downloadUrl(url);
			} catch(Exception e) {
                Log.d("Background Task",e.toString());
			}
			return data;	
		}
		
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);
			Taxi_System.notShow();
			if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
			if(result.contains("error_message") || result.contains("INVALID_REQUEST") || result.contains("ZERO_RESULTS")) 
			{
				Taxi_System.showToast(context, "No Results");
				((ImageView)getView().findViewById(R.id.green)).setVisibility(View.VISIBLE);
                ((ProgressBar)getView().findViewById(R.id.green_p)).setVisibility(View.GONE);
                ((ImageView)getView().findViewById(R.id.red)).setVisibility(View.VISIBLE);
                ((ProgressBar)getView().findViewById(R.id.red_p)).setVisibility(View.GONE);
				return;
			}
			parserTask = new ParserTask();
			parserTask.execute(result);
		}		
	}
	
	public class Adapter1 extends ArrayAdapter<HashMap<String,String>>{
		private List<HashMap<String, String>> list = null;
		private boolean isRed;
		public Adapter1(Context context, int resource,
				List<HashMap<String, String>> objects,boolean isTop) {
			super(context, resource, objects);
			list = new ArrayList<HashMap<String, String>>();
			list = objects;
			this.isRed = isTop;
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent){
	        
			View row = convertView;
			
			try {
	        if (row == null) 
	        {
	            LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = mInflater.inflate(R.layout.adapter_location, parent, false);
	        }
	        //#EAEAEA
	       row.setBackgroundColor(Color.parseColor("#221B1E"));
	       TextView rw1 = (TextView)row.findViewById(R.id.des);
	       Typeface font = Typeface.createFromAsset(context.getAssets(), "pt_sans.ttf");
	       rw1.setTypeface(null, Typeface.BOLD);
	       rw1.setTypeface(font);
           StringBuilder address = new StringBuilder();
           for(int i = 0; i< list.get(position).get("description").split(" ").length - 1; i++)
        	   address.append(list.get(position).get("description").split(" ")[i]).append(" ");
        	   
           rw1.setText(address.toString().substring(0, address.length() - 2));
	       row.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.08);
	       ((LinearLayout)row.findViewById(R.id.border)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.09);
		   ((LinearLayout)row.findViewById(R.id.border)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.08);
		   if(isRed)
			   ((ImageView)row.findViewById(R.id.place)).setImageDrawable(getResources().getDrawable(R.drawable.map_green));
		   else
			   ((ImageView)row.findViewById(R.id.place)).setImageDrawable(getResources().getDrawable(R.drawable.map_red));
			
			} catch (Exception e) {
				
			}
		   
		   return row;
		}
	}
	
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
    	JSONObject jObject;   	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		Taxi_System.showDialog(context);
    	}
		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {						
			List<HashMap<String, String>> places = null;			
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();          
            try{
            	jObject = new JSONObject(jsonData[0]);
            	places = placeJsonParser.parse(jObject);
            }catch(Exception e){
            	Log.d("Exception",e.toString());
            }
            return places;
		}		
		
		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {	
			if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
			adapter1 = new Adapter1(context,  R.layout.adapter_location, result,isTop);
			if(isTop)
			{   
				atvPlaces.setAdapter(adapter1);
				atvPlaces.showDropDown();
				((ImageView)getView().findViewById(R.id.green)).setVisibility(View.VISIBLE);
                ((ProgressBar)getView().findViewById(R.id.green_p)).setVisibility(View.GONE);
                 //isFirst = false;
			}
			else
			{
				atvDriver.setAdapter(adapter1);
				atvDriver.showDropDown();
				((ImageView)getView().findViewById(R.id.red)).setVisibility(View.VISIBLE);
                ((ProgressBar)getView().findViewById(R.id.red_p)).setVisibility(View.GONE);
			}
			Taxi_System.notShow();
		}			
    }
	
    private class PlacesTaskReverse extends AsyncTask<String, Void, String>{
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		Taxi_System.showDialog(context);
    	}
    	
		@Override
		protected String doInBackground(String... place) {
			String data = "";
			String key = Customer_Constants.API_MAP;
			String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+place[0]+"&result_type=street_address&key="+key;
			try {
				data = downloadUrl(url);
			}catch(Exception e){
                Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);
			Taxi_System.notShow();
			if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
			try {
				JSONObject json = new JSONObject(result);
				if(json.getString("status").equalsIgnoreCase("OK"))
				{
					String temp = (new JSONObject((new JSONArray(json.getString("results"))).get(0).toString())).getString("formatted_address").toString();
					StringBuilder address = new StringBuilder();
					for(int i = 0; i< temp.split(" ").length - 1; i++)
			        {
						address.append(temp.split(" ")[i]).append(" ");
			        }
					atvPlaces.setText(address.toString().substring(0, address.length() - 2));
					Taxi_System.testLog(address.toString().substring(0, address.length() - 2));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}		
	}
    
    
	private void countTime() {
		countDown = new CountDownTimer(10000, 1000) {

		     public void onTick(long millisUntilFinished) {
		     }

		     public void onFinish() {
		    	 Log.e( "Customer_Fragment_Activity", Socket.isConnected()+"");
		    	 if(token!=null && !token.equals("") && !Socket.isConnected()) {
//		        	 Socket.socketReconnect();
//		    		 Socket.socketDidDisconnect();
//		    		 Socket.getConnection();
		    		 Socket.socketDidConnect(context);
		    	 }
		    	 if(countDown!=null)
		 			 countDown.cancel();
		 		 countDown = null;
		 		 countTime();
		     }
		};
		countDown.start();
	}
	private void stopCountTime() {
		if(countDown!=null)
			countDown.cancel();
	}
	
    public void onStart()	{
    	super.onStart();
//    	gpsTracker.mLocationClient.connect();
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
}
