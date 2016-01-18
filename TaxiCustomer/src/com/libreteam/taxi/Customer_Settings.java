package com.libreteam.taxi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.libreteam.taxi.extra.Customer_Constants;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Customer_Settings extends Fragment {
	
	private Spinner spnLanguage;
	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.customer_language, container, false);
		context = view.getContext();
		spnLanguage = (Spinner)view.findViewById(R.id.spnLanguage);
		Taxi_System.applyFonts(view, Taxi_System.faceType(getActivity()));
		Taxi_System.setContent((LinearLayout)view.findViewById(R.id.header),context,1,(float) 0.07);
		
		LinearLayout thirdsection = (LinearLayout)view.findViewById(R.id.thirdsection);
		Taxi_System.setContent(thirdsection, context, (float) 0.125, (float)0.07);
		
		ImageView thirdimg = (ImageView)view.findViewById(R.id.thirdimg);
		Taxi_System.setContent(thirdimg, context, (float)0.06, (float)0.04);
		
		LinearLayout logosection = (LinearLayout)view.findViewById(R.id.logosection);
		Taxi_System.setContent(logosection, context, (float) 0.25, (float)0.07);
		ImageView logo = (ImageView)view.findViewById(R.id.logo);
//		Taxi_System.setContent(logo, context, Float.parseFloat((String) getResources().getText(R.string.logosize_w)), Float.parseFloat((String) getResources().getText(R.string.logosize)));
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		
		LinearLayout border = (LinearLayout)view.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);
		
		setSpinner();
		return view;
	}

	
	private void setSpinner() {
		List<String> listLanguage = new ArrayList<String>();
		listLanguage.add("English");
		listLanguage.add("Slovenia");
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listLanguage) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.WHITE);
                ((TextView) v).setPadding(5, 5, 5, 5);
                return v;
			}
		};
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnLanguage.setAdapter(arrayAdapter);
		String position = Taxi_System.getSystem(context, Customer_Constants.SPINNER_SELECTED);
		if(!position.equals(""))
			spnLanguage.setSelection(Integer.parseInt(position));
		spnLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
				Taxi_System.addSystem(context, Customer_Constants.SPINNER_SELECTED, position+"");
				Taxi_System.addSystem(context, Customer_Constants.IS_LANGUAGE_SETTING, "true");
				Configuration configuration = new Configuration();
				if(position==0)
					configuration.locale = new Locale("en_US");
				else if(position==1)
					configuration.locale = new Locale("sl_SI");
				Customer_Settings.this.getResources().updateConfiguration(configuration, Customer_Settings.this.getResources().getDisplayMetrics());
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
	}
}
