package com.libreteam.taxi;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.libreteam.taxi.extra.Customer_Constants;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


@SuppressLint("ResourceAsColor")
public class Customer_Profile extends Fragment implements OnClickListener {

	private Context context;
	private Boolean isEditing;
	private List<TextView> listTextView = new ArrayList<TextView>();
	private List<TextView> listEditText = new ArrayList<TextView>();
	private TextView txtFullName, txtEmail, txtLangguage, txtPhone, txtPassword;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_profile, null);
		context = view.getContext();
    
		if (savedInstanceState == null) {
		
		}
		
		try {
			initComponents(view);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Taxi_System.applyFonts(view, Taxi_System.faceType(getActivity()));	
		return view;
	}
	
	private void initComponents(final View v) throws JSONException 
	{
		Taxi_System.testLog(Taxi_System.getSystem(context, "token"));
		isEditing = false;

		LinearLayout thirdsection = (LinearLayout)v.findViewById(R.id.thirdsection);
		Taxi_System.setContent(thirdsection, context, (float) 0.125, (float)0.07);
		
		ImageView thirdimg = (ImageView)v.findViewById(R.id.thirdimg);
		Taxi_System.setContent(thirdimg, context, (float)0.06, (float)0.04);
		
		LinearLayout logosection = (LinearLayout)v.findViewById(R.id.logosection);
		Taxi_System.setContent(logosection, context, (float) 0.25, (float)0.07);
		ImageView logo = (ImageView)v.findViewById(R.id.logo);
//		Taxi_System.setContent(logo, context, Float.parseFloat((String) getResources().getText(R.string.logosize_w)), Float.parseFloat((String) getResources().getText(R.string.logosize)));
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		
		Taxi_System.setContent((LinearLayout)v.findViewById(R.id.header),context,1,(float) 0.07);
	  
		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);
	  
		JSONObject bundle = new JSONObject(Taxi_System.getSystem(context, "userinfo"));
		txtFullName = (TextView)v.findViewById(R.id.fullname);
		txtFullName.setText(bundle.getString("full_name"));
		txtFullName.setTag("Full Name");
		listTextView.add(txtFullName);
	  
		((TextView)v.findViewById(R.id.idnumber)).setText(bundle.getString("id"));
		((TextView)v.findViewById(R.id.txtLanguageChange)).setOnClickListener(this);
	  
		txtPhone = (TextView)v.findViewById(R.id.phone);
		txtPhone.setText(bundle.getString("tel"));
		txtPhone.setTag("Phone Number");
		listTextView.add(txtPhone);
	  
		txtEmail = (TextView)v.findViewById(R.id.email);
		txtEmail.setText(bundle.getString("email"));
		txtEmail.setTag("Email");
		listTextView.add(txtEmail);
	  
		txtPassword = (TextView)v.findViewById(R.id.password);
		txtPassword.setText("*******");
		txtPassword.setTag("Password");
		listTextView.add(txtPassword);
	  
		txtLangguage = (TextView)v.findViewById(R.id.language);
		txtLangguage.setText(bundle.getString("language"));
		txtLangguage.setTag("Language");
//		listTextView.add(txtLangguage);
	  
		((TextView)v.findViewById(R.id.time)).setText(bundle.getString("createtime").split(" ")[0]);
	  
	  //listEditText.add(((TextView)v.findViewById(R.id.fullname_e)));
	  //listEditText.add(((EditText)v.findViewById(R.id.idnumber_e)));
//	  listEditText.add(((EditText)v.findViewById(R.id.phone_e)));
//	  listEditText.add(((EditText)v.findViewById(R.id.email_e)));
//	  listEditText.add(((EditText)v.findViewById(R.id.password_e)));
//	  listEditText.add(((EditText)v.findViewById(R.id.language_edit)));
	  //listEditText.add(((EditText)v.findViewById(R.id.re_password)));
	  
		String request = "<font color='green'>"+ bundle.getString("completed") +"</font> " + "<font color='grey'>|</font>" + " <font color='white'>"+ bundle.getString("request")+"</font>";
		((TextView)v.findViewById(R.id.request)).setText(Html.fromHtml(request), TextView.BufferType.SPANNABLE);
	  
		String cancel = "<font color='white'>"+ bundle.getString("cancelByDriver") +"</font> " + "<font color='grey'>|</font>" + " <font color='red'>"+ bundle.getString("cancelByUser")+"</font>";
		((TextView)v.findViewById(R.id.cancel)).setText(Html.fromHtml(cancel), TextView.BufferType.SPANNABLE);
	  	
		String overall1 = "<font color='yellow'>"+ bundle.getString("overall") +"</font> ";
		((TextView)v.findViewById(R.id.overall1)).setText(Html.fromHtml(overall1), TextView.BufferType.SPANNABLE);
	  
		String rating = "<font color='white'>"+ bundle.getString("rate") +"</font> ";
		((TextView)v.findViewById(R.id.rating)).setText(Html.fromHtml(rating), TextView.BufferType.SPANNABLE);
	  
		String block = "<font color='green'>"+ bundle.getString("good") +"</font> " + "<font color='grey'>|</font>" + " <font color='red'>"+ bundle.getString("blocked")+"</font>";
		((TextView)v.findViewById(R.id.block)).setText(Html.fromHtml(block), TextView.BufferType.SPANNABLE);
	  
		String overall2 = "<font color='yellow'>"+ bundle.getString("overall") +"</font> ";
		((TextView)v.findViewById(R.id.overall2)).setText(Html.fromHtml(overall2), TextView.BufferType.SPANNABLE);
	  
		((TextView)v.findViewById(R.id.driver)).setText(bundle.getString("favouriteDriver"));
	  
		((TextView)v.findViewById(R.id.company)).setText(bundle.getString("favouriteCompany"));

		Button edit = (Button)v.findViewById(R.id.edit);
		edit.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.06);
		edit.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.05);
	  
		Button up = (Button)v.findViewById(R.id.up);
		up.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.06);
		up.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.05);
	  
		up.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View vat) {
			if(!Taxi_System.connectionStatus(context)) return;
			if(!invalidUpdate()){ 
				Taxi_System.showToast(context, context.getResources().getString(R.string.password_not_match_or));
				isEditing = false; 
				return;
			}
//			new SendPostReqAsyncTask().execute(new String[]{
//			Taxi_System.getSystem(context, "token"), 
//			((EditText)v.findViewById(R.id.fullname_e)).getText().toString(),
//			((EditText)v.findViewById(R.id.email_e)).getText().toString(),
//			((EditText)v.findViewById(R.id.password_e)).getText().toString(),
//			((EditText)v.findViewById(R.id.language_edit)).getText().toString(),
//			((EditText)v.findViewById(R.id.phone_e)).getText().toString()
//			});
		}
		});
	  
		for(TextView text: listEditText)
			text.setOnClickListener(this);
	  
		edit.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View k) {

			if(!isEditing)
			{
				for(int i = 0; i< listTextView.size(); i++)
				{
					listTextView.get(i).setTextColor(Color.parseColor("#F7C228"));
					//listTextView.get(i).setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
					
					//listTextView.get(i).setVisibility(View.GONE);
					//listEditText.get(i).setVisibility(View.VISIBLE);
					//listEditText.get(i).setText(listTextView.get(i).getText().toString());
					//listEditText.get(i).setTextColor(Color.parseColor("#F7C228"));
					//listEditText.get(i).setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);	
				}
				changeMode(true);
				//((TextView)v.findViewById(R.id.re_e)).setVisibility(View.VISIBLE);
				 //((TextView)v.findViewById(R.id.visible)).setVisibility(View.GONE);
				 //((EditText)v.findViewById(R.id.re_password)).setVisibility(View.VISIBLE);
				// ((EditText)v.findViewById(R.id.password_e)).setText(Taxi_System.getSystem(context, "pass_backup"));
				 //((Button)v.findViewById(R.id.up)).setVisibility(View.VISIBLE);
				// ((EditText)v.findViewById(R.id.fullname_e)).requestFocus(1);
//				 InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//			     imm2.showSoftInput(((EditText)v.findViewById(R.id.fullname_e)), InputMethodManager.SHOW_IMPLICIT);
			}
			else
			{
//				InputMethodManager imm2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//			    imm2.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				for(int i = 0; i< listTextView.size(); i++)
				{
					//listTextView.get(i).setVisibility(View.VISIBLE);
					//listEditText.get(i).setVisibility(View.GONE);
					listTextView.get(i).setTextColor(Color.parseColor("#ffffff"));
				}
				//((TextView)v.findViewById(R.id.re_e)).setVisibility(View.GONE);
				//((TextView)v.findViewById(R.id.visible)).setVisibility(View.VISIBLE);
				//((EditText)v.findViewById(R.id.re_password)).setVisibility(View.GONE);
				//((Button)v.findViewById(R.id.up)).setVisibility(View.GONE);
				changeMode(false);
			}
			isEditing =! isEditing;
		}
	}); 
	}	
	private void changeMode(Boolean bool)
	{
		 for(TextView text: listTextView)
		 {
			 if(bool)
			     text.setOnClickListener(this);
			 else
				 text.setOnClickListener(null);
		 }
	}
	
	private Boolean invalidUpdate()
	{
		if(((EditText)getView().findViewById(R.id.re_password)).length() != 0 &&
		((EditText)getView().findViewById(R.id.password_e)).length() != 0 &&
		((EditText)getView().findViewById(R.id.password_e)).getText().toString().equals(((EditText)getView().findViewById(R.id.re_password)).getText().toString())
		&&((EditText)getView().findViewById(R.id.fullname_e)).length() != 0 &&
		((EditText)getView().findViewById(R.id.phone_e)).length() != 0 && ((EditText)getView().findViewById(R.id.language_edit)).length() != 0
		)
		{
			return true;
		}
		else
		{
			return false;
		}
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
        protected String doInBackground(String... params) 
        {
        	{
        		String[] nameReq = new String[] {"token","fullname","email","password","language","tel"};	       		
	        	return Taxi_System.sendRequest(Customer_Constants.CUSTOMER_INFO, nameReq, params);
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
            if(result.contains("200"))
            {

            	Taxi_System.showToast(context, context.getResources().getString(R.string.profile_updated));
//            	for(int i = 0; i< listTextView.size(); i++)
//				{
//					listTextView.get(i).setVisibility(View.VISIBLE);
//					listEditText.get(i).setVisibility(View.GONE);
//					listTextView.get(i).setText(listEditText.get(i).getText().toString());
//				}
    			Taxi_System.addSystem(context, "email", ((TextView)getView().findViewById(R.id.email)).getText().toString());
				Taxi_System.addSystem(context, "password", Taxi_System.getSystem(context, "pass_backup"));

            	//Taxi_System.addSystem(context, "pass_backup", ((EditText)getView().findViewById(R.id.re_password)).getText().toString());
            	//Taxi_System.addSystem(context, "password", ((EditText)getView().findViewById(R.id.re_password)).getText().toString());
//
//            	((TextView)getView().findViewById(R.id.re_e)).setVisibility(View.GONE);
//				((TextView)getView().findViewById(R.id.visible)).setVisibility(View.VISIBLE);
//				((EditText)getView().findViewById(R.id.re_password)).setVisibility(View.GONE);
//				((TextView)getView().findViewById(R.id.password)).setText("*******");
//				((Button)getView().findViewById(R.id.up)).setVisibility(View.GONE);
            }
            else
            {
            	Taxi_System.addSystem(context, "pass_backup", Taxi_System.getSystem(context, "password"));
            	Taxi_System.showToast(context, context.getResources().getString(R.string.server_error_try_again));
            }
        } 
    }

	private void setPopUpView(final View v)
	{
//		if(((String)(v.getTag())).equalsIgnoreCase("Language"))
//		{
//			final CharSequence[] items = { "English", "Slovensko", "Italiano", "Deutsch"};
//			AlertDialog.Builder builder = new AlertDialog.Builder(context);
//			builder.setTitle("Select Language");
//			builder.setItems(items, new DialogInterface.OnClickListener() {
//	
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					((TextView)getView().findViewById(R.id.language)).setText(items[which]);
//					didUpdateInfor();
//				}
//			});
//			AlertDialog alert = builder.create();
//			alert.show();
//			return;
//		}
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Edit Infor");
		alert.setIcon(null);
		alert.setMessage("Change your " + (String)(v.getTag()));	        
		LayoutInflater factory = LayoutInflater.from(context);

		final View textEntryView = factory.inflate(R.layout.adapter_setting, null);
      
		final TextView title1 = (TextView) textEntryView.findViewById(R.id.mot);
		title1.setText((String)(v.getTag()));
      
		final EditText input1 = (EditText) textEntryView.findViewById(R.id.hai);
      	input1.setHint((String)(v.getTag()));


      	final TextView title2 = (TextView) textEntryView.findViewById(R.id.ba);
      	title2.setText("Re-enter Password");
      
      	final EditText input2 = (EditText) textEntryView.findViewById(R.id.bon);
      	input2.setHint("Re-enter Password");
  
      	if(!((String)(v.getTag())).equalsIgnoreCase("Password"))
      	{
      		title2.setVisibility(View.GONE);
      		input2.setVisibility(View.GONE);
      		input1.setText(((TextView)v).getText().toString());
      	}
      	else
      	{
      		input1.setText(Taxi_System.getSystem(context, "pass_backup"));
      	}
		alert.setView(textEntryView);
		alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) 
		{
			if(((String)(v.getTag())).equalsIgnoreCase("Password"))
			{
				if(input1.getText().toString().toLowerCase().equalsIgnoreCase(input2.getText().toString()) && input1.getText().length() != 0 && input2.getText().length() != 0)
				{
					Taxi_System.addSystem(context, "pass_backup", input1.getText().toString());
				}
				else
				{
					Taxi_System.showToast(context, context.getResources().getString(R.string.password_not_match));
					return;
				}
			}
			else
			{
				if(input1.getText().length() != 0)
				{
			        ((TextView)v).setText(input1.getText().toString());
				}
				else
				{
					Taxi_System.showToast(context, context.getResources().getString(R.string.infor_cant_be_empty));
					return;
				}
			}
			didUpdateInfor();
		}
		});
      
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		alert.show();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.edit)
			setPopUpView(v);
		else if(v.getId() == R.id.txtLanguageChange) {
			final CharSequence[] items = { "English", "Slovensko", "Italiano", "Deutsch"};
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Select Language");
			builder.setItems(items, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					txtLangguage.setText(items[which]);
					Taxi_System.addSystem(context, Customer_Constants.SPINNER_SELECTED, which+"");
					Taxi_System.addSystem(context, Customer_Constants.IS_LANGUAGE_SETTING, "true");
					Configuration configuration = new Configuration();
					if(which==0)
						configuration.locale = new Locale("en_US");
					else if(which==1)
						configuration.locale = new Locale("sl_SI");
					Customer_Profile.this.getResources().updateConfiguration(configuration, Customer_Profile.this.getResources().getDisplayMetrics());
					
					didUpdateInfor();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
	}
	
	private void didUpdateInfor()
	{
		if(!Taxi_System.connectionStatus(context)) return;
		new SendPostReqAsyncTask().execute(new String[]{
			Taxi_System.getSystem(context, "token"), 
			txtFullName.getText().toString(),
			txtEmail.getText().toString(),
//			txtPassword.getText().toString(),
			Taxi_System.getSystem(context, "password"),
			txtLangguage.getText().toString(),
			txtPhone.getText().toString()
		});
	}
}
