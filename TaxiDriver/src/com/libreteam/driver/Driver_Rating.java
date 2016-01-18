package com.libreteam.driver;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libreteam.driver.extra.Socket;
import com.libreteam.driver.extra.Taxi_Constants;

public class Driver_Rating extends Fragment implements OnClickListener {

	private Context context;
    private boolean isGood;
    private String ride_id,customer_id, address;
    private Button negative,positive;
    private TextView txtTime, txtTicket, txtDestination, txtDistance;
    private String ticket="", time="";
    
	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    View view = inflater.inflate(R.layout.driver_rating, null);
	    context = view.getContext();
	    
	    ride_id = getArguments().getStringArray("rideid")[0];
	    customer_id = getArguments().getStringArray("rideid")[1];
	    address = getArguments().getStringArray("rideid")[2];
//	    latlng = getArguments().getStringArray("rideid")[3];
	    
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
		new RideInfo().execute(ride_id);
		return view;
	}
	
	private void initComponents(View v)
	{	   
		isGood = true;
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
		
        ((Button)v.findViewById(R.id.send)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.send)).setTypeface(null, Typeface.BOLD);
        positive = (Button)v.findViewById(R.id.positive);
        positive.setOnClickListener(this);
        
        negative = (Button)v.findViewById(R.id.negative);
        negative.setOnClickListener(this);
        isGoodCustomer(isGood);
        
        txtDestination = (TextView)v.findViewById(R.id.txtDestination);
        txtDistance = (TextView)v.findViewById(R.id.txtDistance);
        txtTime = (TextView)v.findViewById(R.id.txtDate);
        txtTicket = (TextView)v.findViewById(R.id.txtTicket);
    }
	
	class RideInfo extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			Taxi_System.testLog(params[0]+"");
			String data = Taxi_System.sendRequest(Taxi_Constants.GET_RIDE_INFO, new String[]{"ride_id"}, new String[]{params[0]});
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
//						if(tmpTime.length>1)
//							time = tmpTime[0] + " | " +tmpTime[1];
//						else if(tmpTime.length==1)
//							time = tmpTime[0];
						if (tmpTime.length > 1)	{
							String[] tmpDate = tmpTime[0].split("-");
							String[] tmpHM = tmpTime[1].split(":");
							
							time = tmpDate[2] + "/" + tmpDate[1] + "/" + tmpDate[0] + " | " + tmpHM[0] + ":" + tmpHM[1];
						}
						else if (tmpTime.length == 1)	{
							String[] tmpDate = tmpTime[0].split("-");							
							
							time = tmpDate[2] + "/" + tmpDate[1] + "/" + tmpDate[0];							
						}
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
			txtDestination.setText(Taxi_System.getSystem(context, Taxi_Constants.PICK_UP)+"");
			txtTime.setText(time);
			txtTicket.setText(ticket);
			txtDistance.setText(Taxi_System.getSystem(context, Taxi_Constants.DISTANCE)+"");
		}
	}
	private Animation blink()
	{
        AlphaAnimation  blinkanimation= new AlphaAnimation(1, 0);
        blinkanimation.setDuration(1100);
        blinkanimation.setInterpolator(new LinearInterpolator());
        blinkanimation.setRepeatCount(1000); 
        blinkanimation.setRepeatMode(Animation.RELATIVE_TO_PARENT);
        return blinkanimation;
	}
	
	private void isGoodCustomer(boolean isCool)
	{
		if(isCool)
		{
	        positive.startAnimation(blink());
			negative.clearAnimation();
		}
		else
		{
			negative.startAnimation(blink());
			positive.clearAnimation(); 
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.send:
			 HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
		 		try
		 		{
		 			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
		 			hash.put("ride_id", new JSONObject().put("ride_id", ride_id));
		 			hash.put("id", new JSONObject().put("id", customer_id));
		 			hash.put("rate", new JSONObject().put("rate", isGood ? "6" : "-1"));
		 		} catch (JSONException e) {
		 			e.printStackTrace();
		 		}
		 		
			  	Socket.socketDidSendMessage(Taxi_System.jsonString(
			  			new String[]{"code","type"},
			  			new String[]{"6",Taxi_System.getSystem(context,"type")},
			  			new String[]{"token","ride_id","id","rate"},
			  			hash,
			  			null,null
		 		));
				
	  		HashMap<String, JSONObject> note = new HashMap<String, JSONObject>();
	  		try
	  		{
	  			note.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
	  			note.put("ride_id", new JSONObject().put("ride_id", ride_id));
	  			note.put("note", new JSONObject().put("note",((EditText)getView().findViewById(R.id.note)).getText().toString()));
	  		} catch (JSONException e) {
	  			e.printStackTrace();
	  		}
	 	  	Socket.socketDidSendMessage(Taxi_System.jsonString(
	 	  		new String[]{"code","type"},
	 	  		new String[]{"9",Taxi_System.getSystem(context,"type")},
	 	  		new String[]{"token","ride_id","note"},
	 	  		note, null,null
	  		));	
	 	  	((Driver_Fragment_Activity)getActivity()).didFinish();	
			break;
			
		case R.id.positive:
			isGood = true;
			isGoodCustomer(true);
			break;
			
		case R.id.negative:
			isGood = false;
		    isGoodCustomer(false);
			break;
		}	
	}
}
