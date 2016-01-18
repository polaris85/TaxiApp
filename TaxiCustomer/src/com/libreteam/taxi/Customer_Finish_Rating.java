package com.libreteam.taxi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.libreteam.taxi.extra.CustomMapFragment;
import com.libreteam.taxi.extra.CustomMapFragment.OnMapReadyListener;
import com.libreteam.taxi.extra.Customer_Constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Customer_Finish_Rating extends Fragment implements OnClickListener, OnMapReadyListener, OnInfoWindowClickListener,OnMarkerClickListener {
    private Context context;
    private CustomMapFragment  mMapFragment;
    private String ride_id,customer_id,address,latlng;
    private Boolean isTracking;
    private static LatLng driverLatLng = null;
	private static LatLng customerLatLng = null;
	private static LatLng d_customerLatLng = null;
	private TextView txtDistance, txtCost;
	
	@SuppressLint("InflateParams")
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_finish_rating, null);
		context = view.getContext();
	
	    ride_id = getArguments().getStringArray("rideid")[0];
	    customer_id = getArguments().getStringArray("rideid")[1];
	    address = getArguments().getStringArray("rideid")[2];
	    latlng = getArguments().getStringArray("rideid")[3];
	    for(String s : getArguments().getStringArray("rideid"))
	    	Taxi_System.testLog(s);
    
	    ArrayList aList= new ArrayList(Arrays.asList(latlng.split(",")));
	    ArrayList d_aList= new ArrayList(Arrays.asList(getArguments().getStringArray("rideid")[5].split(",")));
	    ArrayList l_aList= new ArrayList(Arrays.asList(getArguments().getStringArray("rideid")[4].split(",")));
//	    customerLatLng = new LatLng(Double.parseDouble(d_aList.get(0).toString()), Double.parseDouble(d_aList.get(1).toString()));
//	   	driverLatLng = new LatLng(Double.parseDouble(aList.get(0).toString()), Double.parseDouble(aList.get(1).toString()));
	   	d_customerLatLng = new LatLng(Double.parseDouble(l_aList.get(0).toString()), Double.parseDouble(l_aList.get(1).toString()));

	   	customerLatLng = new LatLng(Customer_Map.getLatitude(), Customer_Map.getLongitude());
	   	driverLatLng = new LatLng(Customer_Map.getLatitude(), Customer_Map.getLongitude());
		

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
		           	((Customer_Fragment_Activity)getActivity()).didFinish();		                
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
		isTracking = false;
		Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));
		Taxi_System.setContent((LinearLayout)v.findViewById(R.id.header),context,1,(float) 0.07);
		
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
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);
		
		((TextView)v.findViewById(R.id.destination)).setText(address);
		 txtDistance = (TextView)v.findViewById(R.id.distance);
		 txtDistance.setTypeface(null, Typeface.BOLD);
		txtCost = (TextView)v.findViewById(R.id.fee);
		txtCost.setTypeface(null, Typeface.BOLD);
		
		((Button) v.findViewById(R.id.finish)).setOnClickListener(this);
		Button btnGPS = (Button) v.findViewById(R.id.gps);
		btnGPS.setOnClickListener(this);
		btnGPS.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.1);
		btnGPS.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.1);
		((TextView)v.findViewById(R.id.finish)).setTypeface(null, Typeface.BOLD);
		if(mMapFragment == null)
        {
		    mMapFragment = CustomMapFragment.newInstance();
	        getChildFragmentManager().beginTransaction().replace(R.id.map, mMapFragment,"customer_finish").commit();		
        }
		
		//by Ying		
		if (address.matches(""))	{			
			v.findViewById(R.id.body).setVisibility(View.GONE);
		}
	}

	@Override
	public void onMapReady() {
		if(mMapFragment != null)
    	{
			Taxi_System.setUiMap(mMapFragment.getMap());
	        mMapFragment.getMap().setOnInfoWindowClickListener(this);
	        mMapFragment.getMap().setOnMarkerClickListener(this);
	        setUpMapView(true);
    	}
	}

	private void setUpMapView(Boolean check)
	{
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(customerLatLng);
		points.add(d_customerLatLng);
	   	points.add(driverLatLng);
	    addMarker(points, mMapFragment.getMap());
	    if(d_customerLatLng.latitude!=0.0 && d_customerLatLng.longitude!=0.0)
	        Taxi_System.zoomToFitLatLongs(points, mMapFragment.getMap(), Taxi_System.getWidth(context),(int)(Taxi_System.getHeight(context) * 0.6));
	    else
	    	Taxi_System.zoomToLocationRide(driverLatLng.latitude, driverLatLng.longitude, mMapFragment.getMap());
	    
//	    if(check)
//	    	points.remove(2);	    
		 
	    List<LatLng> points1 = new ArrayList<LatLng>();
	   	points1.add(customerLatLng);
//	   	if(check)
	   		points1.add(d_customerLatLng);
//	   	else
//	   		points1.add(driverLatLng);
	   	
	    try
	    {
	    	new ParserTask().execute(Taxi_System.downloadTask(Taxi_System.getMapsApiDirectionsUrl(points1)));
		}
	    catch (InterruptedException e) {
			e.printStackTrace();
		} 
		catch (ExecutionException e) {
			e.printStackTrace();
		}	    
		  
	}
	  
	private void addMarker(List<LatLng> latlng, GoogleMap mMap)
	{
		int resourceId = 0;
		String title = null;
		mMap.clear();
		for(int n=0; n<latlng.size(); n++) {
			if(n == 0)
			{
				//title = gpsTracker.getAddressLine(context) == null ? "You're here!" : gpsTracker.getAddressLine(context);
				resourceId = R.drawable.map_taxi_green;
			}
			else if(n == 1)
			{
				//title = gpsTracker.getAddressLine(context) == null ? "Taxi's here!" : gpsTracker.getAddressLine(context);
				resourceId = R.drawable.map_taxi_red;
			}
			else
				resourceId = R.drawable.map_taxi_yellow;
			if(latlng.get(n).latitude != 0.0 && latlng.get(n).longitude != 0.0 ) {
				mMapFragment.getMap().addMarker(new MarkerOptions()
				.position(latlng.get(n))
				.title(title)
				.icon(BitmapDescriptorFactory.fromResource(resourceId))
				.flat(true)
				.rotation(0));
			} 
		}
	}
	
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;
			try {
				jObject = new JSONObject(jsonData[0]);
				com.libreteam.taxi.extra.PathJSONParser parser = new com.libreteam.taxi.extra.PathJSONParser();
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			if(routes==null || routes.size() == 0) {
				(((TextView)getView().findViewById(R.id.distance))).setText("---");
				(((TextView)getView().findViewById(R.id.fee))).setText("---");
				Taxi_System.addSystem(context, Customer_Constants.DISTANCE, "--");
				return;
			}
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;
			String distance = "0";
				
			// traversing through routes
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);
			
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					if(j==0){	
						distance = (String)point.get("distance");						
						continue;
					} else if(j==1){ 
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
			txtDistance.setText(distance);
			float price = 0.0f;
			String p;
			String strStarting_fee="0", strPrice_per_km="0";
			float starting_fee, price_per_km;
			strPrice_per_km = Taxi_System.getSystem(context, "per_km");
			strStarting_fee = Taxi_System.getSystem(context, "start_fee");
			price_per_km = Float.parseFloat(strPrice_per_km);
			starting_fee = Float.parseFloat(strStarting_fee);
		    if(distance.contains("km"))
		    {
		        price = Float.parseFloat(distance.split(" ")[0]) *  price_per_km + starting_fee;
//		        price = ((int)price*2)/2;
//		        Taxi_System.testLog(price);
		    }
		    else
		    {
		        price = (Float.parseFloat(distance.split(" ")[0]) / 1000) *  price_per_km + starting_fee;
//		        price = ((int)price*2)/2;
//		        Taxi_System.testLog(price);
		    }
		    price = (float) ((int)(price*1.05*2)/2.0);
	   		Taxi_System.testLog(price);
		    p = String.valueOf(price);
//		    if(p.contains("."))
//		    	p = p.replace(".", ",");
			txtCost.setText(p + "");
			Taxi_System.addSystem(context, Customer_Constants.DISTANCE, distance+"");
		}
	}
		
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.finish:
				didEndUpdatePosition();
				((Customer_Fragment_Activity)getActivity()).didFisishRating(new String[]{
	        		ride_id,customer_id,
	        		address,
	        		String.valueOf(d_customerLatLng.latitude) + "," + String.valueOf(d_customerLatLng.longitude)});
				break;		
	        case R.id.gps:   
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
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		
	}
	
	public Timer timer;
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
						setUpMapView(false);
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
	
	public void didGetPosition(final LatLng driver)
	{
		driverLatLng = driver;
	}
}
