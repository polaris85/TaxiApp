package com.libreteam.taxi;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.libreteam.taxi.extra.CompanyRow;
import com.libreteam.taxi.extra.CustomCompanyAdapter;
import com.libreteam.taxi.extra.Customer_Constants;
import com.libreteam.taxi.extra.CustomCompanyAdapter.AdapterInterface;

public class Customer_Company extends Fragment  implements OnItemClickListener,AdapterInterface{

	 private ListView listView;
 	 private List<CompanyRow> rowItems;
     private Context context;
     CustomCompanyAdapter adapter;
     private CharSequence[] countryCode;
     private CharSequence[] countryName;
     private CheckBox checkbox;

	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_company, null);
		context = view.getContext();
		if (savedInstanceState == null) 
		{
			initComponents(view);
		}
		return view;
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
//		Taxi_System.setContent(logo, context, Float.parseFloat((String) getResources().getText(R.string.logosize_w)), Float.parseFloat((String) getResources().getText(R.string.logosize)));
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		  
		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);
		
		final Button down = (Button)v.findViewById(R.id.downbutton);
		down.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.04);
		down.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.04);
		down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				didShowList(down,countryName);
			}
		});
		
		new GetCountryReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token")});

		checkbox = (CheckBox) v.findViewById(R.id.che);
		checkbox.setOnClickListener(new OnClickListener() {
                  
			@Override
			public void onClick(View v) {
				if(checkbox.isChecked())
                {
					checkState(true);
	            }else{
	                checkState(false);
	            }
			}
		});
		
		listView = (ListView)v.findViewById(R.id.listview);
		rowItems = new ArrayList<CompanyRow>();
		listView.setOnItemClickListener(this);
//		setAdapter();
		 checkbox.setChecked(true);
//		 checkState(true);
	}
	
	private void checkState(Boolean s)
	{
		String ids="";
		for(CompanyRow com : rowItems)
		{
			if(ids.equals(""))
				ids = com.getId();
			else
				ids = ids.concat(",").concat(com.getId());
			
			com.setCheck(s);
		}
		
		String temp="1";
		if(s)
			temp="0";
		new SetBlockListReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token"), ids, temp});
		
		adapter = new CustomCompanyAdapter(context,  R.layout.adapter_company, rowItems);
		adapter.setCallback(this);
		listView.setAdapter(adapter);
		listView.invalidate();
	}
	
	public void setAdapter(final JSONArray jsonArray)
    {
	    if(adapter != null)
    	{
            adapter.clear();
    	}
	    checkbox.setChecked(true);
	    try{
		    for (int i=0; i<jsonArray.length(); i++)
	    	{
				///check,name,position,rate
		    	boolean ch = true;
		    	if(jsonArray.getJSONObject(i).getString("value").equals("1")) {
		    		ch = true;
		    	}else{
		    		ch = false;
		    		checkbox.setChecked(false);
		    	}
		    		CompanyRow item = new CompanyRow
					(
					jsonArray.getJSONObject(i).getString("id") , ch, jsonArray.getJSONObject(i).getString("name") ,"88%","4.2"
					);
		            rowItems.add(item);
	    	}
		} catch(JSONException jsException) {
			Log.e("Customer_Company", jsException.toString());
		}
		adapter = new CustomCompanyAdapter(context,  R.layout.adapter_company, rowItems);
		adapter.setCallback(this);
		listView.setAdapter(adapter);
		listView.invalidate();
    }
	
    private void didShowList(final Button button,final CharSequence[] items)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Select Option");
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((TextView)getView().findViewById(R.id.cityname)) .setText(items[which]);
				dialog.cancel();
				new SendPostReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token"), (String) countryCode[which]});
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	}

	@Override
	public void callbackF(String id, boolean type) {
		String temp="1";
		if(type)
			temp="0";
		new SetBlockListReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token"), id, temp});
		
		for(CompanyRow com : rowItems)
		{
			if(!com.getCheck())
			{
				((CheckBox) getView().findViewById(R.id.che)).setChecked(false);
				break;
			}
			else
			{
				((CheckBox) getView().findViewById(R.id.che)).setChecked(true);
			}
		}
	}
	
	class GetCountryReqAsyncTask extends AsyncTask<String, String, String>{
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
	  	protected String doInBackground(String... params) 
	 	{       		
			String[] nameReq = new String[] {"token"};
		 	return Taxi_System.sendRequest(Customer_Constants.GET_COUNTRY, nameReq, params);
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
			try {
				json = new JSONObject(result);
				if(json.getString("code").toString().equalsIgnoreCase("200"))
				{
					JSONArray cast = json.getJSONArray("countries");
					if(cast.length() == 0)
					{
//						Taxi_System.showToast(context, context.getResources().getString(R.string.no_drivers_available));
					    return;
					}
					countryName = new CharSequence[cast.length()];
					countryCode = new CharSequence[cast.length()];
					for(int i=0; i<cast.length(); i++) {
						Locale loc = new Locale("",cast.getString(i));
						countryName[i] = loc.getDisplayCountry();
						countryCode[i] = cast.getString(i);
					}
//					setAdapter(cast);
				}
				else
					Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	}
	
	class SendPostReqAsyncTask extends AsyncTask<String, String, String>{
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
	  	protected String doInBackground(String... params) 
	 	{       		
			String[] nameReq = new String[] {"token","country"};
		 	return Taxi_System.sendRequest(Customer_Constants.GET_TAXI_COMPANY, nameReq, params);
	  	}

		@Override
	 	protected void onPostExecute(String result) {
			super.onPostExecute(result);
	        Taxi_System.testLog(result);
	        if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
	        JSONObject json;
			try {
				json = new JSONObject(result);
				if(json.getString("code").toString().equalsIgnoreCase("200"))
				{
					JSONArray cast = json.getJSONArray("info");
					if(cast.length() == 0)
					{
						Taxi_System.showToast(context, context.getResources().getString(R.string.no_drivers_available));
					    return;
					}
					setAdapter(cast);
				}
				else
					Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Taxi_System.notShow();
		} 
	}
	
	class SetBlockListReqAsyncTask extends AsyncTask<String, String, String>{
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
	  	protected String doInBackground(String... params) 
	 	{       		
			String[] nameReq = new String[] {"token","companyid","type"};
		 	return Taxi_System.sendRequest(Customer_Constants.CUSTOMER_BLOCKLIST, nameReq, params);
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
		} 
	}

}
