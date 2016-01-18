package com.libreteam.driver.extra;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.libreteam.driver.R;
import com.libreteam.driver.Taxi_System;

public class CustomHistoryAdapter extends ArrayAdapter<HistoryRow> {
    private Context context;
	public CustomHistoryAdapter(Context context, int textViewResourceId,
			List<HistoryRow> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	 public class ViewHolder 
	 {
	    	ImageView image;
	        TextView date;
	        TextView time;
	        TextView name;
	 }

	 public ViewHolder holder = null;

	    public View getView(int position, View convertView, ViewGroup parent) {
	    	HistoryRow rowItem = getItem(position);
	 
	    	 LayoutInflater mInflater = (LayoutInflater) context
		                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		        if (convertView == null) {
		            convertView = mInflater.inflate(R.layout.adapter_history, null);
		            holder = new ViewHolder();
	  
		            ImageView icon = (ImageView) convertView.findViewById(R.id.image);
		            icon.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
		            icon.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.07);
		            
		            TextView date = (TextView) convertView.findViewById(R.id.date);
		        	TextView time = (TextView) convertView.findViewById(R.id.time);
		        	TextView name = (TextView) convertView.findViewById(R.id.name);
		           
		        	
		            holder.date = date;
		            holder.time = time;
		            holder.name = name;
		            holder.image = icon;
		            convertView.setTag(holder);
		        }
		        else
		        {
		        	holder = (ViewHolder) convertView.getTag();
		        }
		        
		        holder.date.setText(rowItem.getDate()+" | ");
		        holder.time.setText(rowItem.getTime());
		        holder.name.setText(rowItem.getName());
		        Typeface font = Typeface.createFromAsset(context.getAssets(), "pt_sans.ttf");
		        holder.date.setTypeface(font);
		        holder.time.setTypeface(font);
		        holder.name.setTypeface(font);
		        holder.image.setBackgroundResource(setImageWithCode(rowItem.getImage()));
		        
	        
	 return convertView;
	    }
	    
	    private int setImageWithCode(String code)
	    {
	    	int c = 0;
	    	switch (Integer.parseInt(code))
	    	{
	    	case 0:
	    	
	    	c = R.drawable.tick_ac_b;
	    	
	    	break;
	    	case 1:
	    		c = R.drawable.button_u;
	    		break;
	    	case 2:
	    		c = R.drawable.button_d;
	    		break;
	    	}
			return c;
	    }
	    
}
