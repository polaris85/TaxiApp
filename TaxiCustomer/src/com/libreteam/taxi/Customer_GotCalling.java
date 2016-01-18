package com.libreteam.taxi;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.libreteam.taxi.extra.Socket;
import com.libreteam.taxi.extra.Customer_Constants;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
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
import android.widget.TextView;

@SuppressLint("InflateParams")
public class Customer_GotCalling extends Fragment implements OnClickListener{
    private Context context;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_gotcalling, null);
		context = view.getContext();
 
		for(String s : getArguments().getStringArray("rideid"))
			Taxi_System.testLog(s);
    
		if (savedInstanceState == null)
		{
			
		}
		Taxi_System.setContent((LinearLayout)view.findViewById(R.id.header),context,1,(float) 0.07);
		Taxi_System.applyFonts(view, Taxi_System.faceType_normal(getActivity()));
		
		LinearLayout thirdsection = (LinearLayout)view.findViewById(R.id.thirdsection);
		Taxi_System.setContent(thirdsection, context, (float) 0.125, (float)0.07);
		
		ImageView thirdimg = (ImageView)view.findViewById(R.id.thirdimg);
		Taxi_System.setContent(thirdimg, context, (float)0.06, (float)0.04);
		
		LinearLayout logosection = (LinearLayout)view.findViewById(R.id.logosection);
		Taxi_System.setContent(logosection, context, (float) 0.25, (float)0.07);
		ImageView logo = (ImageView)view.findViewById(R.id.logo);
//		Taxi_System.setContent(logo, context, Float.parseFloat((String) getResources().getText(R.string.logosize_w)), Float.parseFloat((String) getResources().getText(R.string.logosize)));
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		
		((TextView)view.findViewById(R.id.in)).setTypeface(null, Typeface.BOLD);  
		LinearLayout border = (LinearLayout)view.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);

		((Button)view.findViewById(R.id.icall)).setOnClickListener(this);
		((Button)view.findViewById(R.id.icall)).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.1);
		((Button)view.findViewById(R.id.icall)).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.1);
		((Button)view.findViewById(R.id.in)).setOnClickListener(this);
	
		((TextView)view.findViewById(R.id.drivername)).setText(getArguments().getStringArray("rideid")[0]);
		((TextView)view.findViewById(R.id.company)).setText(getArguments().getStringArray("rideid")[1]);
		((TextView)view.findViewById(R.id.model)).setText(getArguments().getStringArray("rideid")[2]);
		
		Taxi_System.testLog(getArguments().getStringArray("rideid")[5]);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK )
				{
					if(((Customer_Fragment_Activity)getActivity()).isMenu)
    	        	{
						((Customer_Fragment_Activity)getActivity()).didHideMenu();
						return true;
    	          	}
					getCurrentRide();
    	        	return true;
		     	} else {
		     		return false;
		     	}
			}
		});

		return view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.icall:
		  break;
		  
		case R.id.in:
			//getFragmentManager().popBackStack();
			getCurrentRide();
			((Customer_Fragment_Activity)getActivity()).isInCalling = false;
	      break;		
		}
	}

	private void getCurrentRide()
	{
		((Customer_Fragment_Activity)getActivity()).didAcceptRide(new String[]{
				  getArguments().getStringArray("rideid")[3],
				  getArguments().getStringArray("rideid")[4],
				  getArguments().getStringArray("rideid")[7],
	        	  getArguments().getStringArray("rideid")[5],
	        	  getArguments().getStringArray("rideid")[6],
	        	  getArguments().getStringArray("rideid")[8]
		});
				
		{
			HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
			try
			{
				hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
				hash.put("ride_id", new JSONObject().put("ride_id", getArguments().getStringArray("rideid")[3]));
	  			hash.put("id", new JSONObject().put("id", getArguments().getStringArray("rideid")[4]));
	  			String latitude = Taxi_System.getSystem(context, Customer_Constants.LATITUDE);
	  			String lng = Taxi_System.getSystem(context, Customer_Constants.LONGITUDE);
	  			String title = "";
	  			if(latitude.equals("") | lng.equals(""))
	  				title = "Not Available";
	  			else 
	  				title = Taxi_System.getAddressLine(context, Double.valueOf(latitude), Double.valueOf(lng));
	   	 		hash.put("pickup", new JSONObject().put("pickup", title));
	   		} catch (JSONException e) {
	   			e.printStackTrace();
	   		}
			
		  	Socket.socketDidSendMessage(Taxi_System.jsonString(
		    	new String[]{"code","type"},
		    	new String[]{"5",Taxi_System.getSystem(context,"type")},
			    new String[]{"token","ride_id","id"},
			    	hash,
			    	null,null
			    	));
		}
	}
}
