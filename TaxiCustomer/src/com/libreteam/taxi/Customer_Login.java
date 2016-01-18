package com.libreteam.taxi;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.libreteam.taxi.extra.Socket;
import com.libreteam.taxi.extra.Customer_Constants;

@SuppressLint({ "InflateParams", "DefaultLocale" })
public class Customer_Login extends Fragment implements OnClickListener {
    private Context context;
    boolean isRememberme,isFacebook;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private EditText email, password;
       
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.customer_login, null);
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
    				if(((Customer_Fragment_Activity)getActivity()).isMenu)
    				{
    					((Customer_Fragment_Activity)getActivity()).didHideMenu();
    					return true;
    				}
    				((Customer_Fragment_Activity)getActivity()).didFinishAll();
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
	        new Request(session, "/me", null, HttpMethod.GET, new Request.Callback() {
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
	  
	private String GetCountryZipCode(){
		String CountryID="";
		String CountryZipCode="";
		TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
	        
		CountryID= manager.getSimCountryIso().toUpperCase();
	  	String[] rl=this.getResources().getStringArray(R.array.country_codes);
		for(int i=0;i<rl.length;i++)
		{
			String[] g=rl[i].split(",");
			if(g[1].trim().equals(CountryID.trim()))
          	{
				CountryZipCode=g[0];
           	 	break; 	                                                 
        	}
	  	}
		return CountryZipCode;
	}
	  
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
	    Taxi_System.setContent(header,context,1,(float)0.35);
        
	    LinearLayout form = (LinearLayout) v.findViewById(R.id.form);
        Taxi_System.setContent(form,context,1,(float)0.35);
        
	    ///Header
        {
		    ImageView taxilogo = (ImageView) v.findViewById(R.id.logo);
		    Taxi_System.setContent(taxilogo,context,(float)0.5,(float) ((float)0.5 /5.5));
		    
		    LinearLayout gap = (LinearLayout) v.findViewById(R.id.gap);
		    Taxi_System.setContent(gap,context,1,(float)0.5/9);
		    
	        Button facebook = (Button) v.findViewById(R.id.btnFacebook);
	        Taxi_System.setContent(facebook,context,(float)0.45,(float)0.5 /8);
	        facebook.setOnClickListener(this);
        }
        ///Footer
        {
        	Button register = (Button) v.findViewById(R.id.btnRegister);
        	register.setTypeface(null,Typeface.BOLD);
        	Taxi_System.setContent(register,context,(float)0.5 /(float)1.5,(float)0.15 /(float)2.5);
        	register.setOnClickListener(this);
            
            Button login = (Button) v.findViewById(R.id.btnLogin);
            login.setTypeface(null,Typeface.BOLD);
            Taxi_System.setContent(login,context,(float)0.5 /(float)1.5,(float)0.15 /(float)2.5);
            login.setOnClickListener(this);
        }
        
        email = (EditText) v.findViewById(R.id.btnEmail);
        password = (EditText) v.findViewById(R.id.btnPassword);     
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
        	email.setText("");
        	password.setText("");
        }
        else	
        {
        	checkbox.setChecked(true);
        	email.setText(Taxi_System.getSystem(context, "email"));
        	password.setText(Taxi_System.getSystem(context, "password"));
        }
        autoLogIn(email, password);
	}
	
	private void autoLogIn(EditText email, EditText password)
	{
		if(!Taxi_System.getSystem(context, "name").equalsIgnoreCase("") 				
		&&!Taxi_System.getSystem(context, "pass").equalsIgnoreCase("")
		&&Taxi_System.getSystem(context, "name") != null
		&&Taxi_System.getSystem(context, "pass") != null
		&&!Taxi_System.getSystem(context, "mobnumb").equalsIgnoreCase("")
		&&Taxi_System.getSystem(context, "mobnumb") != null
		)
		{
			email.setText(Taxi_System.getSystem(context, "name"));
        	password.setText(Taxi_System.getSystem(context, "pass"));
        	didAutoLogin(true);
		}
	}
	
	class SendPostReqAsyncTask extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute()
		{
			if(Taxi_System.connectionStatus(context))
			{
				Taxi_System.showDialog(context);
			}
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
			   	return Taxi_System.sendRequest(Customer_Constants.CUSTOMER_LOGIN, nameReq, params);
		 	}
		 	else {
		    	String[] nameReq = new String[] {"facebookId","mobile","email"};	       		
			  	return Taxi_System.sendRequest(Customer_Constants.CUSTOMER_FACEBOOK, nameReq, params);
			}
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
				switch(Integer.parseInt(json.getString("code").toString()))
	        	{
	            	case 200:
	            		if(new JSONObject(json.getString("info")).getString("type").toString().equalsIgnoreCase("driver")) 
	            		{
	            			Taxi_System.showToast(context, "This application is for Customer only.");
	            			return;
	    				}
	            		Taxi_System.addSystem(context, "token", new JSONObject(json.getString("info")).getString("token").toString());
	    				Taxi_System.addSystem(context, "type", new JSONObject(json.getString("info")).getString("type").toString());
	    				Taxi_System.addSystem(context, "pass_backup", password.getText().toString());
	    				didLogIn();
	    				break;
		         	case 401:
		            	Taxi_System.showToast(context, context.getResources().getString(R.string.invalid_type));
		            	break;   
		         	case 500:
		             	Taxi_System.showToast(context, context.getResources().getString(R.string.server_error));
		             	break; 
		        	case 400:
		            	Taxi_System.showToast(context, context.getResources().getString(R.string.check_infor));
		             	break;
		        	case 1:
		            	Taxi_System.showToast(context, json.getString("info")+"");
		             	break;
		         	default:
		         		Taxi_System.showToast(context, json.getString("info")+"");
		         		break;
	        	}
			} catch (JSONException e) {
					e.printStackTrace();
					Taxi_System.showToast(context, context.getResources().getString(R.string.server_error));
			}
		} 
	}

	private void didLogIn()
	{
		((Customer_Fragment_Activity)getActivity()).didLogIn();
		Taxi_System.addSystem(context, "name", "");
		Taxi_System.addSystem(context, "pass", "");
		Taxi_System.addSystem(context, "mobnumb", "");
	}
	
	private void didAutoLogin(Boolean isAuto)
	{
		isFacebook = false;
		if(!Taxi_System.connectionStatus(context))
		{
			Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
			Taxi_System.addSystem(context, "name", "");
			Taxi_System.addSystem(context, "pass", "");
			Taxi_System.addSystem(context, "mobnumb", "");
			return;
		}
		if(email.getText().length() == 0 || password.getText().length() == 0)
		{
			Taxi_System.showToast(context, context.getResources().getString(R.string.please_check_email));
			Taxi_System.addSystem(context, "name", "");
			Taxi_System.addSystem(context, "pass", "");
			Taxi_System.addSystem(context, "mobnumb", "");
		}
		else
		{
			if(!Taxi_System.checkPlayServices(getActivity()) || !Taxi_System.checkGPS(context, getActivity())) return;
			if(Taxi_System.getSystem(context, "isRememberme").equalsIgnoreCase("1"))
			{
				Taxi_System.addSystem(context, "email", email.getText().toString());
				Taxi_System.addSystem(context, "password", password.getText().toString());
			}
			String temp = null;
			if(!isAuto)
			{
				temp = getMyPhoneNumber() == null ? "000" : getMyPhoneNumber();
			}
			else
			{
				temp = Taxi_System.getSystem(context, "mobnumb");
			}
			new SendPostReqAsyncTask().execute(new String[]{email.getText().toString(), 
					Taxi_System.md5(password.getText().toString()),temp});
		}
	}
	
	
	@Override
	public void onClick(View arg0) 
	{
		switch (arg0.getId()) 
		{
		case R.id.btnRegister:
			Taxi_System.didAddFragment(this, new Customer_Register(),"countryCode",new String[] {(GetCountryZipCode() == null ||GetCountryZipCode().equalsIgnoreCase("")) ? "000": GetCountryZipCode()},true);
			break;
		case R.id.btnLogin:			
			didAutoLogin(false);
//			if(Socket.socketIO()==null) {
//				Socket.getConnection();
//				Socket.socketDidConnect();
//			}
			break;
		case R.id.btnFacebook:
			if(!Taxi_System.checkPlayServices(getActivity()) || !Taxi_System.checkGPS(context, getActivity())) return;
			   isFacebook = true;
			Session session = Session.getActiveSession();
			if (!session.isOpened() && !session.isClosed()) 
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		    else 
		    	Session.openActiveSession(getActivity(), this, true, statusCallback);
			break;
		case R.id.btnForgot:
			AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext());
			alert.setTitle("Reset Password!");
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
}
