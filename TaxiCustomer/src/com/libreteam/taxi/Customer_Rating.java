package com.libreteam.taxi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.libreteam.taxi.extra.Socket;
import com.libreteam.taxi.extra.Customer_Constants;

public class Customer_Rating extends Fragment implements OnClickListener{

	private Context context;
    private String ride_id,customer_id,address;
    private TextView txtRateNumber, txtDestination, txtDate, txtTicket, txtDistance;
    private String ticket="", time="";
    
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_rating, null);
		address = getArguments().getStringArray("rideid")[2];
		ride_id = getArguments().getStringArray("rideid")[0];
		customer_id = getArguments().getStringArray("rideid")[1];

		context = view.getContext();
/*		if (savedInstanceState == null)
			initComponents(view);*/
		
		// by Ying
		try {
			initComponents(view);
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		//////////
	
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
		     	} else 
		     		return false;
			}
		});
		new RideInfo().execute(ride_id);
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
		((Button)v.findViewById(R.id.send)).setOnClickListener(this);
		((TextView)v.findViewById(R.id.send)).setTypeface(null, Typeface.BOLD);
		txtRateNumber = (TextView)v.findViewById(R.id.ratenumber);
		((RatingBar)v.findViewById(R.id.rating)).setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			 public void onRatingChanged(RatingBar ratingBar, float rating,  boolean fromUser) {
				 txtRateNumber.setText(String.valueOf((int)rating));
			 }
		});
		txtDestination = (TextView)v.findViewById(R.id.txtDestination);
		txtDate = ((TextView)v.findViewById(R.id.txtDate));
		
		txtTicket = (TextView)v.findViewById(R.id.txtTicket);
		txtDistance = (TextView)v.findViewById(R.id.txtDistance);
		// by Ying
		JSONObject bundle = new JSONObject(Taxi_System.getSystem(context, "userinfo"));
		((TextView)v.findViewById(R.id.rating_completed_rides)).setText(" " + bundle.getString("completed"));
		//////////
	}
	
	class RideInfo extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Taxi_System.testLog(params[0]+"");
			String data = Taxi_System.sendRequest(Customer_Constants.GET_RIDE_INFO, new String[]{"ride_id"}, new String[]{params[0]});
			Taxi_System.testLog(data+"");
			if(data!=null) {
				try {
					JSONObject jsObj = new JSONObject(data);
					jsObj = jsObj.getJSONObject("info");
					ticket = jsObj.getString("ticketid");
					if(ticket!=null)
						ticket = ticket.substring(2);
					time = jsObj.getString("starttime");
					if(time!=null) {
						String[] tmpTime = time.split(" ");
/*						if(tmpTime.length>1)
							time = tmpTime[0] + " | " +tmpTime[1];
						else if(tmpTime.length==1)
							time = tmpTime[0];*/
						
						// by Ying
						if (tmpTime.length > 1)	{
							String[] tmpDate = tmpTime[0].split("-");
							String[] tmpHM = tmpTime[1].split(":");
							
							time = tmpDate[2] + "/" + tmpDate[1] + "/" + tmpDate[0] + " | " + tmpHM[0] + ":" + tmpHM[1];
						}
						else if (tmpTime.length == 1)	{
							String[] tmpDate = tmpTime[0].split("-");							
							
							time = tmpDate[2] + "/" + tmpDate[1] + "/" + tmpDate[0];							
						}
						//////////
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}	
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			txtDestination.setText(Taxi_System.getSystem(context, Customer_Constants.PICK_UP)+"");
			txtDate.setText(time);
			txtTicket.setText(" "+ticket);
			txtDistance.setText(" "+Taxi_System.getSystem(context, Customer_Constants.DISTANCE));
		}
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId())
		{
		case R.id.send:
		{
			HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
    		try
    		{
    			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
    			hash.put("dropoff", new JSONObject().put("dropoff", address));
    			hash.put("rate", new JSONObject().put("rate", txtRateNumber.getText().toString()));
    			hash.put("ride_id", new JSONObject().put("ride_id", ride_id));
    			hash.put("id", new JSONObject().put("id", customer_id));

    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
    		
	  		Socket.socketDidSendMessage(Taxi_System.jsonString(
	  				new String[]{"code","type"},
	  				new String[]{"6",Taxi_System.getSystem(context,"type")},
	  				new String[]{"token","dropoff","rate","ride_id","id"},
	  				hash,
	  				null,null
    		));
		}
	  		HashMap<String, JSONObject> note = new HashMap<String, JSONObject>();
    		try
    		{
    			note.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
    			note.put("note", new JSONObject().put("note", txtRateNumber.getText().toString()));
    			note.put("ride_id", new JSONObject().put("ride_id", ride_id));
    		} catch (JSONException e) {
    			e.printStackTrace();
    		}
    		
	  		Socket.socketDidSendMessage(Taxi_System.jsonString(
	  				new String[]{"code","type"},
	  				new String[]{"9",Taxi_System.getSystem(context,"type")},
	  				new String[]{"token","note","ride_id"},
	  				note, null, null
    		));
	 	  	((Customer_Fragment_Activity)getActivity()).didFinish();
			/* changeable GPS setting by Ying */
			Taxi_System.changeGPS(context, getActivity());
			/**********************************/	 	  	

			break;
		}
	}
}
