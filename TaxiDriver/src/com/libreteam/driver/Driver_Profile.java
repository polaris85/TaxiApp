package com.libreteam.driver;


import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class Driver_Profile extends Fragment {

	private Context context;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.driver_profile, null);
		context = view.getContext();
    
		if (savedInstanceState == null) 
		{
		    
		}
		try 
		{
			initComponents(view);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
//		  view.setFocusableInTouchMode(true);
//			view.requestFocus();
//			view.setOnKeyListener(new View.OnKeyListener() {
//			        @Override
//			        public boolean onKey(View v, int keyCode, KeyEvent event) {
//			            if( keyCode == KeyEvent.KEYCODE_BACK ) {
//			            	if(((Driver_Fragment_Activity)getActivity()).isMenu)
//			            	{
//			            		((Driver_Fragment_Activity)getActivity()).didHideMenu();
//			            		return true;
//			            	}
//			            	//((Driver_Fragment_Activity)getActivity()).isBackable = true;
////			            	((Driver_Fragment_Activity)getActivity()).didPopBackStack();
//			                return true;
//			            } else {
//			                return false;
//			            }
//			        }
//			    });
//		
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
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);

		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);
	  
		JSONObject bundle = new JSONObject(Taxi_System.getSystem(context, "userinfo"));
		((TextView)v.findViewById(R.id.fullname)).setText(bundle.getString("first_name") + " " + bundle.getString("last_name"));//fullname
		((TextView)v.findViewById(R.id.idnumber)).setText(bundle.getString("app_name") + "");//appname
	  
		((TextView)v.findViewById(R.id.phone)).setText(bundle.getString("app_photo"));//idnumber
		((TextView)v.findViewById(R.id.email)).setText(bundle.getString("contact_mobile"));//phone
	  	((TextView)v.findViewById(R.id.password)).setText(bundle.getString("contact_email"));//email
	  
	  	((TextView)v.findViewById(R.id.language)).setText("********");//password
	  	((TextView)v.findViewById(R.id.time)).setText(bundle.getString("company_name"));//company
	  	((TextView)v.findViewById(R.id.city)).setText(bundle.getString("city"));//country
	  
	 	((TextView)v.findViewById(R.id.request)).setText(bundle.getString("car_model"));//model
	 	((TextView)v.findViewById(R.id.cancel)).setText(bundle.getString("car_plate_number"));//plate
	 	((TextView)v.findViewById(R.id.overall1)).setText(bundle.getString("prime_color"));//color
	  
	 	((TextView)v.findViewById(R.id.rating)).setText(bundle.getString("taxi_year"));//year
	 	((TextView)v.findViewById(R.id.block)).setText(bundle.getString("taxi_seats"));//no seats
	 	((TextView)v.findViewById(R.id.overall2)).setText(bundle.getString("large_baggages").equalsIgnoreCase("0") ? "NO" : "YES");//baggage
	  
	 	((TextView)v.findViewById(R.id.driver)).setText(bundle.getString("taxi_credit").equalsIgnoreCase("0") ? "NO" : "YES");//credit card
	}
}
