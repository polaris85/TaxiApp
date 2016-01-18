package com.libreteam.taxi;

import com.libreteam.taxi.R;
import com.libreteam.taxi.extra.Customer_Constants;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Customer_Register extends Fragment implements OnClickListener {
	private Context context;
	private EditText edtName, edtPass, edtNumb;
	private TextView txtCountry;
	
	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_register, null);
		context = view.getContext();
		initComponents(view);
		return view;
	}
	 
	private void initComponents(View v)
	{ 
		Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));
		 
		   // RelativeLayout header = (RelativeLayout) v.findViewById(R.id.header);
	        //Taxi_System.setContent(header,context,1,(float)0.1);
	        
		LinearLayout form = (LinearLayout) v.findViewById(R.id.form);
	    Taxi_System.setContent(form,context,1,(float)0.82);
	        
	    RelativeLayout image = (RelativeLayout) v.findViewById(R.id.image);
	    Taxi_System.setContent(image,context,1,(float)0.8 * (float)0.35);
	        
//	        RelativeLayout footer = (RelativeLayout) v.findViewById(R.id.footer);
	        //Taxi_System.setContent(footer,context,1,(float)0.2);
	        ///Header
	    {
	    	Button login = (Button) v.findViewById(R.id.btnBack);
            Taxi_System.setContent(login,context,1,(float)0.08);
            login.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.08);
	        login.setOnClickListener(this);
	        
            Button language = (Button) v.findViewById(R.id.btnLanguage);
            language.setOnClickListener(this);
         
            ImageView taxilogo = (ImageView) v.findViewById(R.id.logo);
            Taxi_System.setContent(taxilogo,context,(float)1,(float)0.5 /6);
	    }
	    Button mobile = (Button) v.findViewById(R.id.btnMobile);
     	Taxi_System.setContent(mobile,context,1,(float)0.04);
     	mobile.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.04);
     	mobile.setOnClickListener(this);
     	///Footer
     	{
     		Button register = (Button) v.findViewById(R.id.btnRegis);
	    	register.setTypeface(null,Typeface.BOLD);
	    	register.getLayoutParams().width = (int) (Taxi_System.getWidth(context) * 3/4 - 50);
	    	register.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
	    	register.setOnClickListener(this);  
     	}
     	txtCountry = (TextView) v.findViewById(R.id.btnCountry);
     	txtCountry.setText(getArguments().getStringArray("countryCode")[0].equalsIgnoreCase("") ? "00" : getArguments().getStringArray("countryCode")[0]);
	    	
	    edtName = ((EditText)v.findViewById(R.id.btnUname));
	    edtNumb = ((EditText)v.findViewById(R.id.btnNumb));
	    edtPass = ((EditText)v.findViewById(R.id.btnUpass));
	   	//((TextView) v.findViewById(R.id.lang)).setText(getResources().getText(R.)));
	}

	class SendPostReqAsyncTask extends AsyncTask<String, String, String>{
		@Override
	   	protected void onPreExecute()
	   	{
	   		if(Taxi_System.connectionStatus(context))
	   		{
		   		Taxi_System.showDialog(context);
	    	}
	    	else
	    	{
	    		Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
	    		return;
	    	}
	   	}
	    	
	        @Override
	        protected String doInBackground(String... params) {
	        	String[] nameReq = new String[] {"email","password","mobile"};	       		
	        	return Taxi_System.sendRequest(Customer_Constants.CUSTOMER_REGISTER, nameReq, params);
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
	            if(result.toString().contains("Success"))
	            {
	            	Taxi_System.addSystem(context, "name", edtName.getText().toString());
					Taxi_System.addSystem(context, "pass", edtPass.getText().toString());
	            	Taxi_System.addSystem(context, "mobnumb",edtNumb.getText().toString());
					FragmentManager fm = getActivity().getSupportFragmentManager();
					fm.popBackStack();
	            }
	            else
	            {
	            	Taxi_System.showToast(context, context.getResources().getString(R.string.check_infor));
	            }
//	            JSONObject json;
//				try {
//					json = new JSONObject(result);
//					  switch(Integer.parseInt(json.getString("code").toString()))
//                        {
//                        	case 200:
//                        		if(new JSONObject(json.getString("info")).getString("type").toString().equalsIgnoreCase("driver")) 
//    							{
//    								Taxi_System.showToast(context, "This application is for Customer only.");
//    								return;
//    							}
//    							Taxi_System.addSystem(context, "name", ((EditText)getView().findViewById(R.id.btnUname)).getText().toString());
//    							Taxi_System.addSystem(context, "pass", ((EditText)getView().findViewById(R.id.btnPassword)).getText().toString());
//    							FragmentManager fm = getActivity().getSupportFragmentManager();
//    							fm.popBackStack();
//	             			   break;
//	                        case 401:
//	                        	Taxi_System.showToast(context, "Invalid Type");
//	             			   break;   
//	                        case 500:
//	                        	Taxi_System.showToast(context, "Server Error");
//	             			   break; 
//	                        case 400:
//	                        	Taxi_System.showToast(context, "Check Infor");
//	             			   break;
//	             		   default:
//	             		   break;
//                        }
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
	        } 
	    }

	 
	@Override
	public void onClick(View v) {
		switch (v.getId()) 
		{
			case R.id.btnBack:
			   	FragmentManager fm = getActivity().getSupportFragmentManager();
				fm.popBackStack();
				break;
			case R.id.btnRegis:
			{
				if(!Taxi_System.connectionStatus(context))
				{
					Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
					return;
				}
				if(edtName.getText().length() == 0 || edtNumb.getText().length() == 0 || edtPass.getText().length() == 0)
				{
					Taxi_System.showToast(context, context.getResources().getString(R.string.please_check_email));
				}
				else
				{
					new SendPostReqAsyncTask().execute(new String[]{edtName.getText().toString(), 
							Taxi_System.md5(edtPass.getText().toString()), edtNumb.getText().toString()});
				}
			}
				break;
			case R.id.btnLanguage: {
				final CharSequence[] items = { " English", " Slovensko", " Italiano", " Deutsch"};
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Select Language");
				builder.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						((Button)getView().findViewById(R.id.btnLanguage)).setText(items[which]);
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				break;
			}
			case R.id.btnMobile:
			{
				final CharSequence[] items = { "04", "01", "045" };
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Select country code");
				builder.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						txtCountry.setText(items[which]);
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
			default:
				break;
		}
	}
}
