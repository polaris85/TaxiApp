package com.libreteam.taxi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.libreteam.taxi.R;
import com.libreteam.taxi.extra.Socket;
import com.libreteam.taxi.extra.CircularProgressBar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Customer_Calling extends Fragment implements OnClickListener {
    
	private Context context;
	public String ride_id1,id;
//	private GPSTracker gpsTracker;
	private CircularProgressBar mCountdownBar2;
    private CountDownTimer mTimerCountDown;
	boolean isStop;

	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_calling, null);
		
	    context = view.getContext();
	    id = getArguments().getStringArray("driverid")[0];

	    Taxi_System.addSystem(context, "current_latlng", getArguments().getStringArray("driverid")[4] + "," + getArguments().getStringArray("driverid")[5]);
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
    	           	didStopCounting();
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
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);

//		gpsTracker = new GPSTracker(context);
		
		TextView driverName = (TextView) v.findViewById(R.id.drivername);
		driverName.setText(getArguments().getStringArray("driverid")[1]);
		TextView detail1 = (TextView) v.findViewById(R.id.detail1);
		detail1.setText(getArguments().getStringArray("driverid")[2]);
		TextView detail2 = (TextView) v.findViewById(R.id.detail2);
		detail2.setText(getArguments().getStringArray("driverid")[10]+"");
		Button select = (Button) v.findViewById(R.id.select);
		select.setOnClickListener(this);
		
		mCountdownBar2 = (CircularProgressBar) v.findViewById(R.id.countdown_bar2);
		if(mTimerCountDown == null)
		{
		    mTimerCountDown = new CountDownTimer(60 * 1000, 1000)
	        {
	            @Override
	            public void onTick(final long millisUntilFinished)
	            {
	                final int secondsRemaining = 60 - (int) (millisUntilFinished / 1000);
	                mCountdownBar2.setProgress(secondsRemaining);
	            }
	
	            @Override
	            public void onFinish()
	            {
	                mCountdownBar2.setProgress(60);
	                didStopCounting();
//				    FragmentManager fm = getActivity().getSupportFragmentManager();
//				    fm.popBackStack();
	                ((Customer_Fragment_Activity)getActivity()).didMap();
				    return;
	            }
	        }.start();
		}
		
	    new Handler().post(new Runnable() {
	        @Override
	        public void run() {
		        HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
	    		try 
	    		{
	    			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
	    			hash.put("id", new JSONObject().put("id", id));
	    		} catch (JSONException e) {
	    			e.printStackTrace();
	    		}
	    		
			    JSONObject bundle;
			    String good = "0";
			    String blocked = "0";
				try {
					bundle = new JSONObject(Taxi_System.getSystem(context, "userinfo"));
					good = bundle.getString("good");
					blocked = bundle.getString("blocked");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	    		HashMap<String, JSONObject> info = new HashMap<String, JSONObject>();
	    		try 
	    		{
	    			info.put("address", new JSONObject().put("address", getArguments().getStringArray("driverid")[6]));
	    			info.put("latlng", new JSONObject().put("latlng",getArguments().getStringArray("driverid")[4] + "," + getArguments().getStringArray("driverid")[5])); //String.format("%d,%d", gpsTracker.getLatitude(),gpsTracker.getLongitude())
	    			info.put("username", new JSONObject().put("username", Taxi_System.getSystem(context, "username"))); 
	    			info.put("d_latlng", new JSONObject().put("d_latlng",getArguments().getStringArray("driverid")[7] + "," + getArguments().getStringArray("driverid")[8])); 
	    			info.put("d_address", new JSONObject().put("d_address", getArguments().getStringArray("driverid")[9]));
	    			info.put("good", new JSONObject().put("good", good));
	    			info.put("bad", new JSONObject().put("bad", blocked));

	    		} catch (JSONException e) {
	    			e.printStackTrace();
	    		}
	    		
	    	    Socket.socketDidSendMessage(Taxi_System.jsonString(
	    	    		new String[]{"code","type"},
	    	    		new String[]{"1",Taxi_System.getSystem(context, "type")},
	    	    		new String[]{"token","id"},
	    	    		hash,
	    	    		new String[]{"address","latlng","username","d_latlng","d_address","good","bad"},
	    	    		info
	    	    		));
	    		}
	    });
	    isStop = false;
	}
	
	@Override
	public void onDestroyView() {
		if(mTimerCountDown != null)
		{
			mTimerCountDown.cancel();
			mTimerCountDown = null;
		}
		 super.onDestroyView();

	}
	
	private void didStopCounting()
	{
		isStop = true;
		HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
  		try {
  			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
  			hash.put("ride_id", new JSONObject().put("ride_id", ((Customer_Fragment_Activity)getActivity()).ride_id));
  			hash.put("id", new JSONObject().put("id", id));
  		} catch (JSONException e) {
  			e.printStackTrace();
  		}
  		
  		Socket.socketDidSendMessage(Taxi_System.jsonString(
 	    		new String[]{"code","type"},
 	    		new String[]{"2",Taxi_System.getSystem(context, "type")},
 	    		new String[]{"token","ride_id","id"},
 	    		hash,
 	    		null,null
 	    		));
		 
  		HashMap<String, JSONObject> timeOut = new HashMap<String, JSONObject>();
  		try {
  			timeOut.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
  			timeOut.put("ride_id", new JSONObject().put("ride_id", ((Customer_Fragment_Activity)getActivity()).ride_id));
  			timeOut.put("id", new JSONObject().put("id", id));
  		} catch (JSONException e) {
  			e.printStackTrace();
  		}
  		
		Socket.socketDidSendMessage(Taxi_System.jsonString(
 	    		new String[]{"code","type"},
 	    		new String[]{"8",Taxi_System.getSystem(context, "type")},
 	    		new String[]{"token","ride_id","id"},
 	    		timeOut,
 	    		null,null
 	    		));
		if(mTimerCountDown != null)
		{
			mTimerCountDown.cancel();
			mTimerCountDown = null;
		}
	}


	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
       
		case R.id.select:
			HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
	  		try {
	  			hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
	  			hash.put("ride_id", new JSONObject().put("ride_id", ((Customer_Fragment_Activity)getActivity()).ride_id));
				hash.put("id", new JSONObject().put("id", id));
	  		} catch (JSONException e) {
	  			e.printStackTrace();
	  		}
	  		
			Socket.socketDidSendMessage(Taxi_System.jsonString(
	 	    		new String[]{"code","type"},
	 	    		new String[]{"3",Taxi_System.getSystem(context, "type")},
	 	    		new String[]{"token","ride_id","id"},
	 	    		hash,
	 	    		null,null
	 	    		));
			FragmentManager fm = getActivity().getSupportFragmentManager();
			fm.popBackStack();
			break;
		}
	}
}
