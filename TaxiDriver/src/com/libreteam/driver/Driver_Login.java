package com.libreteam.driver;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.libreteam.driver.R;
import com.libreteam.driver.extra.Socket;
import com.libreteam.driver.extra.Taxi_Constants;

public class Driver_Login extends Fragment implements View.OnClickListener, 
														DialogInterface.OnClickListener, Socket.SocketRespond {
  
	private Context context;
//    private CustomListViewAdapter adapter;
//    private List<RowItem> rowItems;
    boolean isRememberme,isFacebook;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private EditText edtEmail, edtPassword;
    
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.driver_login, null);
		context = view.getContext();
		initComponents(view);
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(getActivity(), null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(getActivity());
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        
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
    	            	((Driver_Fragment_Activity)getActivity()).didFinishAll();
    	                return true;
    	            } else {
    	                return false;
    	            }
    	        }
    	    });
        
		return view;
	}
	
	private class SessionStatusCallback implements Session.StatusCallback {       
		@Override
	    public void call(Session session, SessionState state, Exception exception) {
			if(!session.isOpened()) return;
			new Request( session, "/me", null, HttpMethod.GET, new Request.Callback() {
	      		public void onCompleted(Response response) {
	      			Session session = Session.getActiveSession();
	      			if (!session.isClosed()) 
	       				session.closeAndClearTokenInformation();
	        				        
	       			if(response.getRawResponse().equalsIgnoreCase("") || response.getRawResponse() == null) return;
	       			JSONObject json;
	       			try {
	       				json = new JSONObject(response.getRawResponse().toString());
	       				Taxi_System.testLog(response.getRawResponse());
	       				new SendPostReqAsyncTask().execute(new String[]{json.getString("id").toString(),
	        									getMyPhoneNumber() == null ? "000" : getMyPhoneNumber(),json.getString("email").toString()
	        							});
	       			} catch (JSONException e) {
	       				e.printStackTrace();
	       			}
	      		}
	      	}).executeAsync();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	public void onStop() {
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}
	  
//	  private String GetCountryZipCode(){
//	        String CountryID="";
//	        String CountryZipCode="";
//	       TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//	        
//	        CountryID= manager.getSimCountryIso().toUpperCase();
//	        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
//	        for(int i=0;i<rl.length;i++)
//	        {
//                String[] g=rl[i].split(",");
//                if(g[1].trim().equals(CountryID.trim()))
//                {
//                     CountryZipCode=g[0];
//                     break; 	                                                 
//                }
//	        }
//            return CountryZipCode;
//	  }
	  
	private String getMyPhoneNumber()
	{
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager)
		getActivity().getSystemService(Context.TELEPHONY_SERVICE); 
		Taxi_System.testLog(mTelephonyMgr.getLine1Number());
		return mTelephonyMgr.getLine1Number();
	}
	  
	private void initComponents(View v)
	{
		Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));
		
	    LinearLayout top = (LinearLayout) v.findViewById(R.id.top);
	    Taxi_System.setContent(top,context,1,(float)0.05);
        
		LinearLayout header = (LinearLayout) v.findViewById(R.id.header);
	    Taxi_System.setContent(header,context,1,(float)0.3);
        
	    LinearLayout form = (LinearLayout) v.findViewById(R.id.form);
        Taxi_System.setContent(form,context,1,(float)0.35);
        
        LinearLayout footer = (LinearLayout) v.findViewById(R.id.footer);
        Taxi_System.setContent(footer,context,1,(float)0.15);
	    ///Header
        {
		    ImageView taxilogo = (ImageView) v.findViewById(R.id.logo);
		    Taxi_System.setContent(taxilogo,context,(float)0.5,(float) ((float)0.5 /5.5));
		    
		    LinearLayout gap = (LinearLayout) v.findViewById(R.id.gap);
		    Taxi_System.setContent(gap,context,1,(float)0.5/5);
		    
	        Button facebook = (Button) v.findViewById(R.id.btnFacebook);
	        Taxi_System.setContent(facebook,context,(float)0.5,(float)0.5 /8);
	        facebook.setOnClickListener(this);
        }
        ///Footer
        {
//        	 LinearLayout footer1 = (LinearLayout) v.findViewById(R.id.footer1);
            // Taxi_System.setContent(footer1,context,(float)0.5,(float)0.15);
             
//             LinearLayout footer2 = (LinearLayout) v.findViewById(R.id.footer2);
             //Taxi_System.setContent(footer2,context,(float)0.5,(float)0.15);
        	
        	Button register = (Button) v.findViewById(R.id.btnRegister);
        	//Taxi_System.setContent(register,context,(float)0.5 /(float)1.5,(float)0.15 /(float)2.5);
        	register.setOnClickListener(this);
            
            Button login = (Button) v.findViewById(R.id.btnLogin);
            login.setTypeface(null,Typeface.BOLD);
            Taxi_System.setContent(login,context,(float)0.5 /(float)1.5,(float)0.15 /(float)2.5);
            login.setOnClickListener(this);
        }
        
        edtEmail = (EditText)v.findViewById(R.id.btnEmail);
        
        edtPassword = (EditText)v.findViewById(R.id.btnPassword);
              
        Button forgot = (Button) v.findViewById(R.id.btnForgot);
        
        forgot.setOnClickListener(this);
        
        final CheckBox checkbox = (CheckBox) v.findViewById(R.id.btnCheckbox);
        checkbox.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(checkbox.isChecked())
                        {
                        	Taxi_System.addSystem(context, "isRememberme", "1");
                        }else{
                        	Taxi_System.addSystem(context, "isRememberme", "0");
                        }
                    }
                });
        if(Taxi_System.getSystem(context, "isRememberme").equalsIgnoreCase("0") ||
        Taxi_System.getSystem(context, "isRememberme").equalsIgnoreCase(""))
        {
        	checkbox.setChecked(false);
        	edtEmail.setText("");
        	edtPassword.setText("");
        }
        else	
        {
        	checkbox.setChecked(true);
        	edtEmail.setText(Taxi_System.getSystem(context, "email"));
        	edtPassword.setText(Taxi_System.getSystem(context, "password"));
        }
	}
	
	class SendPostReqAsyncTask extends AsyncTask<String, String, String>{
		@Override
		protected void onPreExecute()
		{
			if(Taxi_System.connectionStatus(context))
				Taxi_System.showDialog(context);
		    else {
		    	Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
		    	return;
		    }
		}
		    	
		@Override
		protected String doInBackground(String... params) 
		{
			if(!isFacebook)
			{
				String[] nameReq = new String[] {"email","password","mobile"};	       		
			    return Taxi_System.sendRequest(Taxi_Constants.DRIVER_LOGIN, nameReq, params);
			} else {
				String[] nameReq = new String[] {"facebookId","mobile","email"};	       		
				return Taxi_System.sendRequest(Taxi_Constants.DRIVER_FACEBOOK, nameReq, params);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		 	Taxi_System.notShow();
			Taxi_System.testLog(result);
		  	JSONObject json;
			try {
				if(result==null) {
					Taxi_System.showToast(context, getResources().getString(R.string.check_internet_connection));
					return;
				}
				json = new JSONObject(result);
				switch(Integer.parseInt(json.getString("code").toString()))
				{
				case 200:
					if(new JSONObject(json.getString("info")).getString("type").toString().equalsIgnoreCase("user")) 
    				{
    					Taxi_System.showToast(context, context.getResources().getString(R.string.this_application_is_for));
    					return;
    				}
    				Taxi_System.addSystem(context, "token", new JSONObject(json.getString("info")).getString("token").toString());
    				Taxi_System.addSystem(context, "type", new JSONObject(json.getString("info")).getString("type").toString());
   					didLogIn();
	         		break;
				case 401: 
				case 500:
				case 400:
					Taxi_System.showToast(context, getResources().getString(R.string.not_internet_connection));
	               	break;
				case 2:
	               	Taxi_System.showToast(context, getResources().getString(R.string.someone_logged_in_using));
	           		break;
				default:
	                	break;
             	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	}
	 
	private void didLogIn()
	{
//		final FragmentTransaction ft = this.getFragmentManager().beginTransaction(); 
//		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//		ft.replace(R.id.activity_main_content_fragment, new Driver_Status() , "Status"); 
//		ft.addToBackStack(null);
//		ft.commit(); 
		((Driver_Fragment_Activity)getActivity()).didLogIn();
	}
	 
	private void didShowAlert()
	{
		AlertDialog ad = new AlertDialog.Builder(context)
		.setMessage(context.getResources().getString(R.string.you_need_to_go_to_Taxi_website_to_get_an_account))
		.setIcon(R.drawable.logotaxi)
		.setTitle("Drivers?")
		.setPositiveButton("Yes", this)
		.setNegativeButton("No", this)
		.setCancelable(false)
		.create();
		ad.show();
	}
	
	@Override
	public void onClick(View arg0) 
	{
		switch (arg0.getId()) 
		{
		case R.id.btnRegister:
	        didShowAlert();
			break;
		case R.id.btnLogin:
			isFacebook = false;
			if(!Taxi_System.connectionStatus(context))
			{
				Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
				return;
			}
			if(edtEmail.getText().length() == 0 || edtPassword.getText().length() == 0)
			{
				Taxi_System.showToast(context, context.getResources().getString(R.string.please_check_email));
			}
			else
			{
				if(!Taxi_System.checkPlayServices(getActivity()) || !Taxi_System.checkGPS(context, getActivity())) return;
				if(Taxi_System.getSystem(context, "isRememberme").equalsIgnoreCase("1"))
				{
					Taxi_System.addSystem(context, "email", edtEmail.getText().toString());
					Taxi_System.addSystem(context, "password", edtPassword.getText().toString());
				}
				new SendPostReqAsyncTask().execute(new String[]{edtEmail.getText().toString(), 
						Taxi_System.md5(edtPassword.getText().toString()), getMyPhoneNumber() == null ? "000" : getMyPhoneNumber()});
			}
			break;
		case R.id.btnFacebook:
			break;
		case R.id.btnForgot:
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Reset Password!");
			alert.setIcon(R.drawable.logotaxi);
			alert.setMessage("Choose which email address you want to send the password to?");
	        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			final EditText input = new EditText(context);
			input.setBackgroundResource(R.drawable.apptheme_textfield_default_holo_dark);
			input.setTextColor(Color.parseColor("#FFFFFF"));
			input.setTypeface(Taxi_System.faceType(getActivity()));
			input.requestFocus();
			imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
			alert.setView(input);
			alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				imm.hideSoftInputFromInputMethod(input.getWindowToken(), 0);
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
				  imm.hideSoftInputFromInputMethod(input.getWindowToken(), 0);
			  }
			});

			alert.show();
			break;
		default:
			break;
		}
	}

	@Override
	public void respondData(String string) {
	    Taxi_System.testLog(string);
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		switch(arg1){
		case DialogInterface.BUTTON_POSITIVE:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://taxi.sflashcard.com"));
			startActivity(browserIntent);
			break;
		case DialogInterface.BUTTON_NEGATIVE: // no
			break;
		default:
			break;
		}
	}
}
