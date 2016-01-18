package com.libreteam.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.libreteam.driver.extra.Taxi_Constants;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class Driver_Settings extends Fragment implements OnClickListener {
	
	private Context context;
	private TextView fees,kilos;
	private List<Button> listButton = new ArrayList<Button>();
	private List<Button> listEdit = new ArrayList<Button>();
	private List<TextView> listTextView = new ArrayList<TextView>();
	private Spinner spnLanguage;
	private TextView txtDistance;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.driver_setting, null);
		context = view.getContext();
		if (savedInstanceState == null) 
			initComponents(view);
		setSpinner();
		return view;
	}

	private void initComponents(final View v)
	{   
		spnLanguage = (Spinner)v.findViewById(R.id.spnLanguage);
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
		txtDistance = (TextView)v.findViewById(R.id.distance);
		txtDistance.setTypeface(null, Typeface.BOLD);

		fees = ((TextView)v.findViewById(R.id.fees));
		kilos = ((TextView)v.findViewById(R.id.kilos));
		fees.setTypeface(null, Typeface.BOLD);
		kilos.setTypeface(null, Typeface.BOLD);
		
		fees.setText(Taxi_System.getSystem(context, "start_fee"));
		kilos.setText(Taxi_System.getSystem(context, "per_km"));
		
		Button opt0 = ((Button)v.findViewById(R.id.check1));
		opt0.setTag(-1);
		Button opt1 = ((Button)v.findViewById(R.id.check2));
		opt1.setTag(-2);
		Button opt2 = ((Button)v.findViewById(R.id.check3));
		opt2.setTag(-3);
		Button opt3 = ((Button)v.findViewById(R.id.check4));
		opt3.setTag(-4);
		Button opt4 = ((Button)v.findViewById(R.id.check5));
		opt4.setTag(-5);

		Button edt0 = ((Button)v.findViewById(R.id.editbutton1));
		edt0.setTag(1);
		Button edt1 = ((Button)v.findViewById(R.id.editbutton2));
		edt1.setTag(2);
		Button edt2 = ((Button)v.findViewById(R.id.editbutton3));
		edt2.setTag(3);
		Button edt3 = ((Button)v.findViewById(R.id.editbutton4));
		edt3.setTag(4);
		Button edt4 = ((Button)v.findViewById(R.id.editbutton5));
		edt4.setTag(5);
		listEdit.add(edt0);
		listEdit.add(edt1);
		listEdit.add(edt2);
		listEdit.add(edt3);
		listEdit.add(edt4);
		
		TextView fee0 = ((TextView)v.findViewById(R.id.fee1));
		TextView fee1 = ((TextView)v.findViewById(R.id.fee2));
		TextView fee2 = ((TextView)v.findViewById(R.id.fee3));
		TextView fee3 = ((TextView)v.findViewById(R.id.fee4));
		TextView fee4 = ((TextView)v.findViewById(R.id.fee5));
		listTextView.add(fee0);
		listTextView.add(fee1);
		listTextView.add(fee2);
		listTextView.add(fee3);
		listTextView.add(fee4);
		
		listButton.add(opt0);
		listButton.add(opt1);
		listButton.add(opt2);
		listButton.add(opt3);
		listButton.add(opt4);

		if(Taxi_System.getSystem(context, "km") == null || Taxi_System.getSystem(context, "km").equalsIgnoreCase(""))
			Taxi_System.addSystem(context, "km", "10");
		
		for(int i = 0; i<5;i++)
		{		
			listEdit.get(i).setOnClickListener(this);
		}
		if(Taxi_System.getSystem(context, "opt").equalsIgnoreCase("") || Taxi_System.getSystem(context, "opt") == null)
		{
			Taxi_System.addSystem(context, "opt", "0");
			for(int i = 0; i< 5; i++)
		  	{
				if(i == 0)
					Taxi_System.addSystem(context, "fee" + String.valueOf(i),
							(Taxi_System.getSystem(context, "start_fee")+ " " + context.getResources().getString(R.string.fee_settings) + "| "+Taxi_System.getSystem(context, "per_km")+" Km"));
				else   
					Taxi_System.addSystem(context, "fee" + String.valueOf(i),context.getResources().getString(R.string.empty_settings));
		  	}
		}
		
		for(int i = 0; i< 5; i++)
			listTextView.get(i).setText(Taxi_System.getSystem(context, "fee" + String.valueOf(i)));

		setUpButtonState(listButton);
//	    setFeesKilos();
	    SeekBar seek = ((SeekBar)v.findViewById(R.id.seekbar));
	    seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	            
	    	@Override
	    	public void onStopTrackingTouch(SeekBar seekBar) { 
	    		Taxi_System.addSystem(context, "km", String.valueOf(seekBar.getProgress() + 2));
	    		new RequestUpdateInfo().execute();
//	    		Taxi_System.testLog(Taxi_System.getSystem(context, "km"));
	    	} 
	    	
	    	@Override
	    	public void onStartTrackingTouch(SeekBar seekBar) { 
	    	} 
	    	
	    	@Override
	    	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { 
	    		txtDistance.setText((Integer.toString(progress + 2)) + " Km");
	    	}
		});	    
//	    seek.setProgress(Integer.parseInt(Taxi_System.getSystem(context, "km")) - 2);
//	    ((TextView)v.findViewById(R.id.distance)).setText(Integer.toString(seek.getProgress() + 2) + " Km");
	    int km = 0;
	    
	    if(!Taxi_System.getSystem(context, "km").equals(""))
	    	km = Integer.parseInt(Taxi_System.getSystem(context, "km"));
	    seek.setProgress(km);
	    txtDistance.setText(km + " Km");
	}
	
	private void setSpinner() {
		List<String> listLanguage = new ArrayList<String>();
		listLanguage.add("English");
		listLanguage.add("Slovensko");
		listLanguage.add("Italiano");
		listLanguage.add("Deutsch");
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
		String position = Taxi_System.getSystem(context, Taxi_Constants.SPINNER_SELECTED);
		if(!position.equals(""))
			spnLanguage.setSelection(Integer.parseInt(position));
		spnLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
				Taxi_System.addSystem(context, Taxi_Constants.SPINNER_SELECTED, position+"");
				Configuration configuration = new Configuration();
				if(position==0)
					configuration.locale = new Locale("en_US");
				else if(position==1)
					configuration.locale = new Locale("sl_SI");
				context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
	}
	
	private void setFeesKilos()
	{
		for(int i = 0; i< 5; i++)
		{
			if(i == Integer.parseInt(Taxi_System.getSystem(context, "opt")))
			{
				fees.setText(listTextView.get(i).getText().toString().split(" ")[0]);
				kilos.setText(listTextView.get(i).getText().toString().split(" ")[2]);
				Taxi_System.addSystem(context, "start_fee",fees.getText().toString());
				Taxi_System.addSystem(context, "per_km",kilos.getText().toString());
			}
		}
	}
	
	private void setTariff(int numb,String[] details)
	{
		for(int i = 0; i< 5; i++)
		{
			if(i == numb)
			{
				if(details[0].length() == 0 && details[1].length() == 0 && details[2].length() == 0 && details[3].length() == 0)
				{
					Taxi_System.addSystem(context, "fee" + String.valueOf(numb), context.getResources().getString(R.string.empty_settings));
				}
				else 
				{
					Taxi_System.addSystem(context, "fee" + String.valueOf(numb), details[0] + (details[1].length() == 0 ? "" : "." + details[1]) + " " + context.getResources().getString(R.string.fee_settings) + "| "+details[2] + (details[3].length() == 0 ? "" : "." + details[3]) +" Km");
				} 
			}
		}
		for(int i = 0; i< 5; i++)
		{
			listTextView.get(i).setText(Taxi_System.getSystem(context, "fee" + String.valueOf(i)));
		}
	}
	
	private void setUpButtonState(List<Button> buttons)
	{
		int i = 0;
		String tick = Taxi_System.getSystem(context, "opt");
		if(!tick.equals("")) 
			i = Integer.parseInt(tick);
		for(int n=0; n<buttons.size(); n++) {
			if(i==n)
				buttons.get(n).setBackgroundResource(R.drawable.tick_ac);
			else
				buttons.get(n).setBackgroundResource(R.drawable.tick_in);
			buttons.get(n).setOnClickListener(this);
			buttons.get(n).getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.05);
			buttons.get(n).getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.05);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void onClick(final View v) 
	{ 
		if((Integer)v.getTag() < 0)
		{
			if(listTextView.get(((Integer)v.getTag()* -1) - 1).getText().toString().equalsIgnoreCase("Empty"))
				return;
			int m = 0;
			for(Button but : listButton)
			{
		        if(v.getId() == ((Button)listButton.get(m)).getId())
		        {
		        	Taxi_System.addSystem(context, "opt", String.valueOf(m));
		        }
		        m ++;
			}
			setUpButtonState(listButton);
			setFeesKilos();
			new RequestUpdateInfo().execute();
		}
		else
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(context);
			alert.setTitle("Edit price");
			alert.setIcon(null);
			alert.setMessage(context.getResources().getString(R.string.enter_your_initial_fee));	        
			LayoutInflater factory = LayoutInflater.from(context);

			final View textEntryView = factory.inflate(R.layout.adapter_setting, null);
			
			final EditText input1 = (EditText) textEntryView.findViewById(R.id.first);
			final EditText input11 = (EditText) textEntryView.findViewById(R.id.first1);

			final EditText input2 = (EditText) textEntryView.findViewById(R.id.second);
			final EditText input22 = (EditText) textEntryView.findViewById(R.id.second1);
	      
			final CheckBox empty = (CheckBox) textEntryView.findViewById(R.id.empty);
			if(((Integer)v.getTag() - 1) == 0)
			{
				empty.setVisibility(View.GONE);
			}
			empty.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View vat) {
					if(empty.isChecked())
					{
						input1.setText("");
						input11.setText("");
						input2.setText("");
						input22.setText("");
						input1.setEnabled(false);
						input11.setEnabled(false);
						input2.setEnabled(false);
						input22.setEnabled(false);
					}
					else
					{
						input1.setEnabled(true);
						input2.setEnabled(true);
						input11.setEnabled(true);
						input22.setEnabled(true);
						if(!listTextView.get((Integer)v.getTag() - 1).getText().toString().equalsIgnoreCase("Empty"))
						{
							if(listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[0].contains("."))
							{
								String temp = listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[0];
								StringTokenizer tokens = new StringTokenizer(temp, ".");
								input1.setText(tokens.nextToken());
								input11.setText(tokens.nextToken());
							}
							else
							{
								input1.setText(listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[0]);
							}
				    	  
							if(listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[2].contains("."))
							{
								String temp1 = listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[2];
								StringTokenizer tokens = new StringTokenizer(temp1, ".");
								input2.setText(tokens.nextToken());
								input22.setText(tokens.nextToken());
							}
							else
							{
								input2.setText(listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[2]);
							}
						}
					}
				
				}
			});
			
			if(!listTextView.get((Integer)v.getTag() - 1).getText().toString().equalsIgnoreCase("Empty"))
			{
				if(listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[0].contains("."))
				{
					String temp = listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[0];
					StringTokenizer tokens = new StringTokenizer(temp, ".");
					input1.setText(tokens.nextToken());
					input11.setText(tokens.nextToken());
				}
				else
					input1.setText(listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[0]);
	    	  
				if(listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[2].contains("."))
				{
					String temp1 = listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[2];
					StringTokenizer tokens = new StringTokenizer(temp1, ".");
					input2.setText(tokens.nextToken());
					input22.setText(tokens.nextToken());
				}
				else
					input2.setText(listTextView.get((Integer)v.getTag() - 1).getText().toString().split(" ")[2]);
			}

			alert.setView(textEntryView);
			alert.setPositiveButton(context.getResources().getString(R.string.settings_edit_done), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) 
			{
				if(!empty.isChecked())
				{
					if((input1.length() == 0 && input11.length() == 0) || (input2.length() == 0 && input22.length() == 0)) 
					{
						Taxi_System.showToast(context, context.getResources().getString(R.string.fields_cant_bt_empty));
						return;
					}
				}
				if(empty.isChecked() && ((Integer)v.getTag() - 1) == Integer.parseInt(Taxi_System.getSystem(context, "opt")))
				{
					Taxi_System.addSystem(context, "opt", "0");
				}
				
				if((input1.length() == 0 && input11.length() != 0))
					input1.setText("0");
				if((input2.length() == 0 && input22.length() != 0))
					input2.setText("0");
				setTariff((Integer)v.getTag() - 1, new String[]{
						input1.getText().toString(),
						input11.getText().toString(),
						input2.getText().toString(),
						input22.getText().toString()
						
				});
				setUpButtonState(listButton);
				setFeesKilos();
				new RequestUpdateInfo().execute();
			}
			});

			alert.setNegativeButton(context.getResources().getString(R.string.settings_edit_cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			
			alert.show();
		}
	}
	
	class RequestUpdateInfo extends AsyncTask<String, String, String>{
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
        	String[] nameReq = new String[] {"token","price_per_km","starting_fee","allow_distance", "tafiffs"};
        	String token = Taxi_System.getSystem(context, "token");
        	String startingFree = Taxi_System.getSystem(context, "start_fee");
        	String pricePerKm = Taxi_System.getSystem(context, "per_km");
        	String allowDistance = Taxi_System.getSystem(context, "km");
        	JSONArray jsArray = new JSONArray();
        	for(int i=0; i<listTextView.size(); i++)
        		jsArray.put(listTextView.get(i).getText().toString());
        	
        	JSONObject jsObjectTariff = new JSONObject();
        	try {
				jsObjectTariff.put("tariffs", jsArray);
				jsObjectTariff.put("tick", Taxi_System.getSystem(context, "opt")+"");
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	params = new String[]{token, pricePerKm, startingFree, allowDistance, jsObjectTariff.toString()+""};
	        return Taxi_System.sendRequest(Taxi_Constants.DRIVER_INFO, nameReq, params);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Taxi_System.notShow();
            Taxi_System.testLog(result);
        } 
    }
}
