package com.libreteam.taxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.libreteam.taxi.R;
import com.libreteam.taxi.extra.CustomListViewAdapter;
import com.libreteam.taxi.extra.PathJSONParser;
import com.libreteam.taxi.extra.RowItem;
import com.libreteam.taxi.extra.Customer_Constants;

import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Customer_ListDriver extends Fragment implements OnItemClickListener {
	private Context context;
    private ListView actualListView;
    private List<RowItem> rowItems;
    private CustomListViewAdapter adapter;
    private List<List<HashMap<String, String>>> routes = null;
    
    @SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.customer_listdriver, null);
	    context = view.getContext();
        try {
			initComponents(view);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return view;
	}
    
	private void initComponents(View v) throws JSONException
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
//		Taxi_System.setContent(logo, context, Float.parseFloat((String) getResources().getText(R.string.logosize_w)), Float.parseFloat((String) getResources().getText(R.string.logosize)));
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		
		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);

		actualListView = (ListView)v.findViewById(R.id.listview);
		rowItems = new ArrayList<RowItem>();
		actualListView.setOnItemClickListener(this);
//		new SendPostReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token"),
//					getArguments().getStringArray("drivers")[1].toString(),
//					getArguments().getStringArray("drivers")[2].toString(),
//					Taxi_System.getSystem(context, "type")});
		setAdapter1(getArguments().getStringArray("drivers")[0]);
		
		Taxi_System.testLog(getArguments().getStringArray("drivers")[1]);
		Taxi_System.testLog(getArguments().getStringArray("drivers")[2]);
	}

//	class SendPostReqAsyncTask extends AsyncTask<String, String, String>{
//		@Override
//	    protected void onPreExecute()
//	    {
//	    	if(Taxi_System.connectionStatus(context))
//		   		Taxi_System.showDialog(context);
//	    	else
//	    	{
//	    		Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
//	    		return;
//	    	}
//	    }
//	    	
//		@Override
//	  	protected String doInBackground(String... params) 
//	 	{       		
//			String[] nameReq = new String[] {"token","lat","lng","type"};
//		 	return Taxi_System.sendRequest(Customer_Constants.CUSTOMER_GPS_SEARCH, nameReq, params);
//	  	}
//
//		@Override
//	 	protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//	     	Taxi_System.notShow();
//	        Taxi_System.testLog(result);
//	        
//	        JSONObject json;
//			try {
//				json = new JSONObject(result);
//				if(json.getString("code").toString().equalsIgnoreCase("200"))
//				{
//					JSONArray cast = json.getJSONArray("info");
//					if(cast.length() == 0)
//					{
//						Taxi_System.showToast(context, context.getResources().getString(R.string.no_drivers_available));
//					    return;
//					}
//					setAdapter(cast);					
//				}
//				else
//					Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		} 
//	}
	
	private void setAdapter1(final String result) 
	{
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				try {
				if(adapter != null)
					adapter.clear();
				float distance = 0;
				String strDistance="0", strStarting_fee="0", strPrice_per_km="0";
				float price = 0.0f, starting_fee, price_per_km;
				
				JSONArray jsonArray = new JSONArray(result);
				for (int i=0; i<jsonArray.length(); i++)
				{
					List<LatLng> points = new ArrayList<LatLng>();
				   	points.add(new LatLng(Double.parseDouble(getArguments().getStringArray("drivers")[1].toString()) ,Double.parseDouble(getArguments().getStringArray("drivers")[2].toString())));
				   	points.add(new LatLng(Double.parseDouble(getArguments().getStringArray("drivers")[4].toString()) ,Double.parseDouble(getArguments().getStringArray("drivers")[5].toString())));
				   	try {
						getRoute(Taxi_System.downloadTask(Taxi_System.getMapsApiDirectionsUrl(points)));
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				   	if(routes!=null && routes.size()>0) {
				   		strDistance = routes.get(0).get(0).get("distance").toString();
				   		
					   	strStarting_fee = jsonArray.getJSONObject(i).getString("starting_fee");
					   	strPrice_per_km = jsonArray.getJSONObject(i).getString("price_per_km");
					   	if(strDistance!=null && strStarting_fee!=null && strPrice_per_km!=null) {
					   		distance = Float.parseFloat(strDistance.split(" ")[0]);
					   		starting_fee = Float.parseFloat(strStarting_fee);
					   		price_per_km = Float.parseFloat(strPrice_per_km);
					   		if(strDistance.contains("km")) 
						        price = distance *  price_per_km + starting_fee;
						    else 
						        price = distance / 1000 *  price_per_km + starting_fee;
					   		
					   		price = (float) ((int)(price*1.05*2)/2.0);
					   		Taxi_System.testLog(price);
					   	}
				   	} 
				   	
					///image,driver,vehicle,heart,time,price,rate,top1,starnum,response,top2,responsenum,driver_id
				  	RowItem item = new RowItem(
				  			jsonArray.getJSONObject(i).getString("car_plate_number"),
				  			jsonArray.getJSONObject(i).getString("thumb"),
				   			jsonArray.getJSONObject(i).getString("last_name") + " " + jsonArray.getJSONObject(i).getString("first_name"),
				 			jsonArray.getJSONObject(i).getString("car_model"),
				    		jsonArray.getJSONObject(i).getString("private_customer_rate")+"",
				    		jsonArray.getJSONObject(i).getString("minute") + " min", 
				    		price+"", 
				  			jsonArray.getJSONObject(i).getString("numRate"),
				    		jsonArray.getJSONObject(i).getString("private_customer_number"),
				    		jsonArray.getJSONObject(i).getString("rating"), 
				    		jsonArray.getJSONObject(i).getString("numResponse"),
				    		jsonArray.getJSONObject(i).getString("numResponse"),
				    		jsonArray.getJSONObject(i).getString("response"),
				    		jsonArray.getJSONObject(i).getString("driver_id"),
				    		jsonArray.getJSONObject(i).getString("starting_fee"),
				    		jsonArray.getJSONObject(i).getString("price_per_km"),
				    		jsonArray.getJSONObject(i).getString("minute"),
				    		new LatLng(
				    				Double.parseDouble(jsonArray.getJSONObject(i).getString("lat")),
				    				Double.parseDouble(jsonArray.getJSONObject(i).getString("lng")))
				    );
				  	item.setCompanyName(jsonArray.getJSONObject(i).getString("company_name"));
				  	item.setAppName(jsonArray.getJSONObject(i).getString("app_name"));				  	
				    rowItems.add(item);
				}
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						adapter = new CustomListViewAdapter(context,  R.layout.adapter_listdriver, rowItems);
						adapter.isVisible = false;
						actualListView.setAdapter(adapter);
							
						if(rowItems.size() == 0)
						{
							Button btnLoadMore = new Button(context);
							btnLoadMore.setText("Load More");
							actualListView.addFooterView(btnLoadMore);
						}
						adapter.notifyDataSetChanged();
						actualListView.invalidate();
					}
				});
				
				} catch(JSONException jsException) {
					Log.e("Customer_ListDriver", jsException.toString());
				}
			}
		}).start();
	}
	 
	private void setAdapter(final JSONArray jsonArray) 
	{
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				try {
				if(adapter != null)
					adapter.clear();
				float distance = 0;
				String strDistance="0", strStarting_fee="0", strPrice_per_km="0";
				float price = 0.0f, starting_fee, price_per_km;
				for (int i=0; i<jsonArray.length(); i++)
				{
					List<LatLng> points = new ArrayList<LatLng>();
					points.add(new LatLng(Double.parseDouble(getArguments().getStringArray("drivers")[4].toString()) ,Double.parseDouble(getArguments().getStringArray("drivers")[5].toString())));
				   	points.add(new LatLng(Double.parseDouble(getArguments().getStringArray("drivers")[1].toString()) ,Double.parseDouble(getArguments().getStringArray("drivers")[2].toString())));
				   	try {
						getRoute(Taxi_System.downloadTask(Taxi_System.getMapsApiDirectionsUrl(points)));
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				   	if(routes!=null && routes.size()>0) {
				   		strDistance = routes.get(0).get(0).get("distance").toString();
				   		
					   	strStarting_fee = jsonArray.getJSONObject(i).getString("starting_fee");
					   	strPrice_per_km = jsonArray.getJSONObject(i).getString("price_per_km");
					   	if(strDistance!=null && strStarting_fee!=null && strPrice_per_km!=null) {
					   		distance = Float.parseFloat(strDistance.split(" ")[0]);
					   		starting_fee = Float.parseFloat(strStarting_fee);
					   		price_per_km = Float.parseFloat(strPrice_per_km);
					   		if(strDistance.contains("km")) 
						        price = distance *  price_per_km + starting_fee;
						    else 
						        price = distance / 1000 *  price_per_km + starting_fee;
					   		
					   		price = (float) ((int)(price*2)/2.0);
					   		Taxi_System.testLog(price);
					   	}
				   	} 
				   	
					///image,driver,vehicle,heart,time,price,rate,top1,starnum,response,top2,responsenum,driver_id
				  	RowItem item = new RowItem(
				  			jsonArray.getJSONObject(i).getString("car_plate_number"),
				  			jsonArray.getJSONObject(i).getString("thumb"),
				   			jsonArray.getJSONObject(i).getString("last_name") + " " + jsonArray.getJSONObject(i).getString("first_name"),
				 			jsonArray.getJSONObject(i).getString("car_model"),
				    		jsonArray.getJSONObject(i).getString("private_customer_rate")+"",
				    		jsonArray.getJSONObject(i).getString("minute") + " min", 
				    		price+"", 
				  			jsonArray.getJSONObject(i).getString("numRate"),
				    		jsonArray.getJSONObject(i).getString("private_customer_number"),
				    		jsonArray.getJSONObject(i).getString("rating"), 
				    		jsonArray.getJSONObject(i).getString("numResponse"),
				    		jsonArray.getJSONObject(i).getString("numResponse"),
				    		jsonArray.getJSONObject(i).getString("response"),
				    		jsonArray.getJSONObject(i).getString("driver_id"),
				    		jsonArray.getJSONObject(i).getString("starting_fee"),
				    		jsonArray.getJSONObject(i).getString("price_per_km"),
				    		jsonArray.getJSONObject(i).getString("minute"),
				    		new LatLng(
				    				Double.parseDouble(jsonArray.getJSONObject(i).getString("lat")),
				    				Double.parseDouble(jsonArray.getJSONObject(i).getString("lng")))
				    );
				  	item.setCompanyName(jsonArray.getJSONObject(i).getString("company_name"));
				  	item.setAppName(jsonArray.getJSONObject(i).getString("app_name"));				  	
				    rowItems.add(item);
				}
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						adapter = new CustomListViewAdapter(context,  R.layout.adapter_listdriver, rowItems);
						adapter.isVisible = false;
						actualListView.setAdapter(adapter);
							
						if(rowItems.size() == 0)
						{
							Button btnLoadMore = new Button(context);
							btnLoadMore.setText("Load More");
							actualListView.addFooterView(btnLoadMore);
						}
						adapter.notifyDataSetChanged();
						actualListView.invalidate();
					}
				});
				
				} catch(JSONException jsException) {
					Log.e("Customer_ListDriver", jsException.toString());
				}
			}
		}).start();
	}
	 
	private String[] infor; 
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
			final int temp = 0;
			String per_km = rowItems.get(arg2 - temp).getPer_km();
			String start_fee = rowItems.get(arg2 - temp).getStart_fee();
			Taxi_System.addSystem(context, "start_fee", start_fee);
		    Taxi_System.addSystem(context, "per_km", per_km);
		    Taxi_System.addSystem(context, "img", rowItems.get(arg2 - temp).getImage());
		    Taxi_System.addSystem(context, Customer_Constants.ROUTE_PRICE, rowItems.get(arg2).getPrice());
		    
			infor = null;
			infor = new String[]{
					rowItems.get(arg2 - temp).getDriverid(),
					rowItems.get(arg2 - temp).getAppName(),
					rowItems.get(arg2 - temp).getVehicle(),
					rowItems.get(arg2 - temp).getHeart(),
					getArguments().getStringArray("drivers")[1].toString(),
					getArguments().getStringArray("drivers")[2].toString(),
					getArguments().getStringArray("drivers")[3].toString(),
					getArguments().getStringArray("drivers")[4].toString(),
					getArguments().getStringArray("drivers")[5].toString(),
					getArguments().getStringArray("drivers")[6].toString(),
					rowItems.get(arg2 - temp).getCompanyName(),
			};
			Customer_Constants.selected_taxi_icon=rowItems.get(arg2 - temp).getImage();
			Customer_Constants.selected_taxi_appname=rowItems.get(arg2 - temp).getAppName();
			Customer_Constants.selected_plate_number=rowItems.get(arg2 - temp).getPlateNumber();

			didCallingDriver(infor);
			Taxi_System.testLog(infor);
	}
	
	private void getRoute(final String jsonData) {
		JSONObject jObject;	
		try {
			jObject = new JSONObject(jsonData);
			PathJSONParser parser = new PathJSONParser();
			routes = parser.parse(jObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void didCallingDriver(String[] driverid)
	{
		((Customer_Fragment_Activity)getActivity()).didCallDriver(driverid);
	}
	  
}
