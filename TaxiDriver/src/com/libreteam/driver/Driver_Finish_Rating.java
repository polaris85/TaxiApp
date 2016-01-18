package com.libreteam.driver;

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
import com.libreteam.driver.extra.CustomMapFragment;
import com.libreteam.driver.extra.PathJSONParser;
import com.libreteam.driver.extra.CustomMapFragment.OnMapReadyListener;
import com.libreteam.driver.extra.Taxi_Constants;

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

@SuppressLint("InflateParams")
public class Driver_Finish_Rating extends Fragment implements OnClickListener, OnMapReadyListener, 
																OnInfoWindowClickListener, OnMarkerClickListener {
    private Context context;
    private CustomMapFragment  mMapFragment;
	private String ride_id,customer_id,address,latlng,d_latlng;
	private boolean isTracking;
	private static LatLng driverLatLng = null;
	private static LatLng customerLatLng = null;
	private static LatLng d_customerLatLng = null;
    private TextView txtDistance, txtCost;
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    View view = inflater.inflate(R.layout.driver_finish_rating, null);
	    context = view.getContext();
		
	    ride_id = getArguments().getStringArray("rideid")[0];
	    customer_id = getArguments().getStringArray("rideid")[1];
	    address = getArguments().getStringArray("rideid")[2];
	    latlng = getArguments().getStringArray("rideid")[3];
	    d_latlng = getArguments().getStringArray("rideid")[4];
	
	    for(String s : getArguments().getStringArray("rideid"))
	    	Taxi_System.testLog(s);
	    
	    ArrayList aList= new ArrayList(Arrays.asList(latlng.split(",")));
	    ArrayList d_aList= new ArrayList(Arrays.asList(d_latlng.split(",")));
	   	driverLatLng = new LatLng(Driver_Status.getLatitude(), Driver_Status.getLongitude());
		customerLatLng = new LatLng(Double.parseDouble(aList.get(0).toString()), Double.parseDouble(aList.get(1).toString()));
		d_customerLatLng = new LatLng(Double.parseDouble(d_aList.get(0).toString()), Double.parseDouble(d_aList.get(1).toString()));
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
	            	((Driver_Fragment_Activity)getActivity()).didFinish();
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
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		  
		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);
		
		((TextView)v.findViewById(R.id.destination)).setText(address);
		txtCost = (TextView)v.findViewById(R.id.fee);
		txtCost.setTypeface(null, Typeface.BOLD);
		txtDistance = (TextView)v.findViewById(R.id.distance);
		txtDistance.setTypeface(null, Typeface.BOLD);
		
		Button finish = (Button) v.findViewById(R.id.finish);
		finish.setTypeface(null, Typeface.BOLD);
		finish.setOnClickListener(this);
		
		Button btnGPS = (Button) v.findViewById(R.id.gps);
		btnGPS.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.1);
		btnGPS.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.1);
		btnGPS.setOnClickListener(this);
		
		if(mMapFragment == null)
        {
		    mMapFragment = CustomMapFragment.newInstance();
	        getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.abc_fade_in, 0).replace(R.id.finish_map, mMapFragment,"driver_finish").commit();		
        }
		
//		if (address.matches(""))	{			
			v.findViewById(R.id.body).setVisibility(View.GONE);
//			btnGPS.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.05);
//			btnGPS.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.05);
//			Taxi_System.setContent((LinearLayout)v.findViewById(R.id.bottom),context,1,(float) 0.01);
//		} else {
//			btnGPS.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.1);
//			btnGPS.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.1);
//			Taxi_System.setContent((LinearLayout)v.findViewById(R.id.bottom),context,1,(float) 0.03);
//		}
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
		    addMarker(points,mMapFragment.getMap());
		    if(d_customerLatLng.latitude!=0.0 && d_customerLatLng.longitude!=0.0)
		    	Taxi_System.zoomToFitLatLongs(points, mMapFragment.getMap(), Taxi_System.getWidth(context),(int)(Taxi_System.getHeight(context) * 0.6));
		    else
		    	Taxi_System.zoomToLocationRide(driverLatLng.latitude, driverLatLng.longitude, mMapFragment.getMap());
		    
//            if(check)
//            	points.remove(2);
		   	
		    List<LatLng> points1 = new ArrayList<LatLng>();
		   	points1.add(customerLatLng);
//		   	if(check)
		   		points1.add(d_customerLatLng);
//		   	else
//		   		points1.add(driverLatLng);
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

		private void addMarker(List<LatLng> latlng,GoogleMap mMap)
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
	
	@Override 
	public void onDestroyView()
	{
		super.onDestroyView();
	}

	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		@Override
		protected List<List<HashMap<String, String>>> doInBackground( String... jsonData) {
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
			if(routes==null || routes.size() == 0)
			{
	//			showAlert();
	//			((Button) getView().findViewById(R.id.finish)).setEnabled(false);
	//			((Button) getView().findViewById(R.id.gps)).setEnabled(false);
				txtDistance.setText("---");
				txtCost.setText("---");
				Taxi_System.addSystem(context, Taxi_Constants.DISTANCE, "--");
				return;
			}
			ArrayList<LatLng> points = null;
			PolylineOptions polyLineOptions = null;

			String strDistance="0";
//			String duration = "";
		
			for (int i = 0; i < routes.size(); i++) {
				points = new ArrayList<LatLng>();
				polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(i);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);
	
					if(j==0){	
						strDistance = (String)point.get("distance");						
						continue;
					} else if (j==1){ 
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
			txtDistance.setText(strDistance+"");
			float price = 0f;
			String strPrice, strStartingFee = "0.0", strPricePerKm = "0.0";
//			if(Taxi_System.getSystem(context, "start_fee")!="" && Taxi_System.getSystem(context, "start_fee")!=null)
//				strStartingFee = Taxi_System.getSystem(context, "start_fee");
//			if(Taxi_System.getSystem(context, "per_km")!="" && Taxi_System.getSystem(context, "per_km")!=null)
//				strPricePerKm = Taxi_System.getSystem(context, "per_km");
//	   		startingFee = Float.parseFloat(strStartingFee);
//	   		pricePerKm = Float.parseFloat(strPricePerKm);
	   		
		    if(strDistance.contains("km"))
		    	price = Float.parseFloat(strDistance.split(" ")[0]) *  Float.parseFloat(Taxi_System.getSystem(context, "per_km")) + Float.parseFloat(Taxi_System.getSystem(context, "start_fee"));
		    else
		        price = (Float.parseFloat(strDistance.split(" ")[0]) / 1000) *  Float.parseFloat(Taxi_System.getSystem(context, "per_km")) + Float.parseFloat(Taxi_System.getSystem(context, "start_fee"));
//		    price = pricePerKm*distance + startingFee;
	   		price = (float) ((int)(price*1.05*2)/2.0);
	   		Taxi_System.testLog(price);
		    strPrice = String.valueOf(price);
//		    if(strPrice.contains("."))
//		    	strPrice = strPrice.replace(".", ",");
		    txtCost.setText(price+"");
		    Taxi_System.addSystem(context, Taxi_Constants.DISTANCE, strDistance+"");
		}
	}
	
//	private void showAlert()
//	{
//		new AlertDialog.Builder(context)
//		.setTitle("Attention!")
//	    .setMessage("No route available for this ride.")
//	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//	    	public void onClick(DialogInterface dialog, int which) { 
//		       	((Driver_Fragment_Activity)getActivity()).didFinish();
//	    	}
//	    })
//		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) { 
//			 	//((Driver_Fragment_Activity)getActivity()).didFinish();
//			}
//		})
//		.setIcon(android.R.drawable.ic_dialog_alert)
//		.show();
//	} 
	 
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.finish:
			didEndUpdatePosition();
			((Driver_Fragment_Activity)getActivity()).didFinishRating(new String[]{
			ride_id, customer_id, address,
			String.valueOf(d_customerLatLng.latitude)+","+String.valueOf(d_customerLatLng.longitude)
			});
			break;	
        case R.id.gps:
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
		}	
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		
	}

	@Override
	public void onAttachFinish() {
		
	}
	
	float i = 0;	
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
						i += 0;//0.05;
				    	driverLatLng = new LatLng(Driver_Status.getLatitude(), 
				    								Driver_Status.getLongitude() + i);
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
}
