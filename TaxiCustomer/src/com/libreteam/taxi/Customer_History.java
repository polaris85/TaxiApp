package com.libreteam.taxi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.libreteam.taxi.extra.CustomHistoryAdapter;
import com.libreteam.taxi.extra.HistoryRow;

public class Customer_History extends Fragment implements OnClickListener,OnItemClickListener{
	private List<Button> listButton = new ArrayList<Button>();
	private Context context;
	private ListView listView;
	private List<HistoryRow> listRow;
	private CustomHistoryAdapter adapter;
    private CharSequence[] year = new CharSequence[]{"2011","2012","2013","2014"};
    private CharSequence[] month = new CharSequence[]{"January","Febuary","March","April","May","June","July","August","September","Octobor","November","December"};
	
    @SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
    View view = inflater.inflate(R.layout.customer_history, null);
    context = view.getContext();
		if (savedInstanceState == null) 
		{
			try {
				initComponents(view);
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
				
	    ((ImageView)v.findViewById(R.id.image1)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.03);
	    ((ImageView)v.findViewById(R.id.image1)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.03);
	    
	    ((ImageView)v.findViewById(R.id.image2)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.03);
	    ((ImageView)v.findViewById(R.id.image2)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.03);
	    
	    ((ImageView)v.findViewById(R.id.image3)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.03);
	    ((ImageView)v.findViewById(R.id.image3)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.03);
	    
		listButton.add((Button)v.findViewById(R.id.dat));
		listButton.add((Button)v.findViewById(R.id.stat));
		listButton.add((Button)v.findViewById(R.id.suc));
		((Button)v.findViewById(R.id.year)).setOnClickListener(this);
		((Button)v.findViewById(R.id.month)).setOnClickListener(this);
		setStateButton((Button)v.findViewById(R.id.dat));
		
		listView = (ListView)v.findViewById(R.id.listview);
		listRow = new ArrayList<HistoryRow>();
		listView.setOnItemClickListener(this);
		setAdapter(0);
		
		Button btnLeft = (Button)v.findViewById(R.id.left);
		btnLeft.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.03);
		btnLeft.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.03);
		Button btnRight = (Button)v.findViewById(R.id.right);
		btnRight.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.03);
		btnRight.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.03);
	}
	
	private String convertDate(String date,String type)
	{		
		if(type.equalsIgnoreCase("-"))
			return (date.split("-"))[0]+"."+(date.split("-"))[1]+".";
		if(type.equalsIgnoreCase(":"))
			return (date.split(":"))[0]+":"+(date.split(":"))[1];
		else return "";
	}
	
	private String convertChoice(String[] choices)
	{
		if(choices[0].equalsIgnoreCase("yes"))
		{
			return "0";
		}
		else
		{
			if(choices[1].equalsIgnoreCase("yes"))
			{
				return "1";
			}
			else
			{
				return "2";
			}
		}
	}
	
	private void setAdapter(int in) throws JSONException
	{
	    if(adapter != null)
    	{
	    	adapter.clear();
    	}
	 
		JSONObject bundle = new JSONObject(Taxi_System.getSystem(context, "userhistory"));  
		JSONArray arr = new JSONArray(bundle.getString("info"));		
        for(int i = 0; i< arr.length(); i++)
        {

			///image,date,time,name
        	HistoryRow item = new HistoryRow(
   			     convertChoice(new String[]{((JSONObject)arr.get(i)).getString("driver_completed"),((JSONObject)arr.get(i)).getString("cancelled_by_customer"),((JSONObject)arr.get(i)).getString("cancelled_by_driver")}),
   			     convertDate(((JSONObject)arr.get(i)).getString("day"),"-"),
   			     convertDate(((JSONObject)arr.get(i)).getString("time"),":"),
   			     //((JSONObject)arr.get(i)).getString("driver_lastname") + ((JSONObject)arr.get(i)).getString("driver_firstname")
   			   ((JSONObject)arr.get(i)).getString("pickup_address")
   				);        	
        	if(in == 0)
       	    {
        		listRow.add(item);
       	    }
        	else if(in == 1)
        	{
        		if(((JSONObject)arr.get(i)).getString("driver_completed").equalsIgnoreCase("no"))
        		{
        			listRow.add(item);
        		}
        	}
        	else if(in == 2)
        	{
        		if(!((JSONObject)arr.get(i)).getString("driver_completed").equalsIgnoreCase("no"))
        		{
        			listRow.add(item);
        		}
        	}
           
        }
	    adapter = new CustomHistoryAdapter(context,  R.layout.adapter_history, listRow);
		listView.setAdapter(adapter);
       
		adapter.notifyDataSetChanged();
		listView.invalidate();
	}
	
    private void setStateButton(View v)
    {
    	for(Button b : listButton)
    	{
    		if(v.getId() == b.getId())
    		{
    			b.setTextColor(Color.parseColor("#E4E523"));
    		}
    		else
    		{
    			b.setTextColor(Color.DKGRAY);
    		}
    		b.setOnClickListener(this);
    	}
    }
    
    private void didShowList(final Button button,final CharSequence[] items)
    {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Select Option");
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(button.getId() == R.id.month)
				    button.setText("     " + items[which]);
				else
					button.setText(items[which]);	
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}  

	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.year )
		{
			didShowList((Button)v,year);
		}
		else if( v.getId() == R.id.month)
		{
			didShowList((Button)v,month);
		}
		else
		{
			int in = 0;
	         setStateButton(v);
	         if(v.getId() == R.id.dat)
	         {
	        	 in = 0;
	         }
	         if(v.getId() == R.id.stat)
	         {
	        	 in = 1;
	         }
	         if(v.getId() == R.id.suc)
	         {
	        	 in = 2;
	         }
	 		try {
				setAdapter(in);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		
	}
}
