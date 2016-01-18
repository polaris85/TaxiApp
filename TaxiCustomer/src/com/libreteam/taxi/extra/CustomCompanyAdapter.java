package com.libreteam.taxi.extra;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.libreteam.taxi.R;
import com.libreteam.taxi.Taxi_System;

@SuppressLint("InflateParams")
public class CustomCompanyAdapter extends ArrayAdapter<CompanyRow>  {
	
	Context context;

	public CustomCompanyAdapter(Context context, int textViewResourceId, List<CompanyRow> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}
	private AdapterInterface callback;
	
	public void setCallback(AdapterInterface callback){

		this.callback = callback;
	}
	
	public interface AdapterInterface
	{
		public void callbackF(String id, boolean type);
	}

    public class ViewHolder{
    	
    	CheckBox check;
    	TextView name;
    	TextView position;
    	TextView rate;
    }

    public ViewHolder holder = null;
   
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        CompanyRow rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) 
        {
            convertView = mInflater.inflate(R.layout.adapter_company, null);
            holder = new ViewHolder();
            
            CheckBox check = (CheckBox)convertView.findViewById(R.id.che);
            TextView name = (TextView) convertView.findViewById(R.id.name);
        	TextView pos = (TextView) convertView.findViewById(R.id.pos);
        	TextView rat = (TextView) convertView.findViewById(R.id.rat);
        	ImageView rate = (ImageView)convertView.findViewById(R.id.rater);
        	rate.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
        	rate.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.06);
        	
        	ImageView poser = (ImageView)convertView.findViewById(R.id.poser);
        	poser.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
        	poser.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.06);
            
        	holder.check = check;
            holder.name = name;
            holder.position = pos;
            holder.rate = rat;
            convertView.setTag(holder);
            
            holder.check.setOnClickListener( new View.OnClickListener() {  
                public void onClick(View v) {  
                 CheckBox cb = (CheckBox) v ;  
                 CompanyRow country = (CompanyRow)cb.getTag();  
                 country.setCheck(cb.isChecked());
                 callback.callbackF(country.getId(), cb.isChecked());
                }  
               });  
        }
        else	
        {
        	holder = (ViewHolder) convertView.getTag();
        }

        holder.check.setChecked(rowItem.getCheck());
        holder.check.setTag(rowItem);
        holder.name.setText(rowItem.getName());
        holder.position.setText(rowItem.getPosition());
        holder.rate.setText(rowItem.getRate());
        Typeface font = Typeface.createFromAsset(context.getAssets(), "pt_sans.ttf");
	 	holder.position.setTypeface(font);
	  	holder.rate.setTypeface(font);
		holder.name.setTypeface(font);
        
	 	return convertView;
	}
}
