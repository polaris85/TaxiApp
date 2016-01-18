package com.libreteam.driver.extra;

import java.util.List;

import com.libreteam.driver.R;
import com.libreteam.driver.Taxi_System;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
 
public class CustomListViewAdapter extends ArrayAdapter<RowItem> {
    Context context;
    public ImageLoader imageLoader; 
    public boolean isVisible;
    public CustomListViewAdapter(Context context, int resourceId,
            List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
        imageLoader=new ImageLoader(context);
    }
 
    
    public class ViewHolder {
    	ImageView image;
    	ImageView star;
        TextView driver;
        TextView vehicle;
        TextView heart;
        TextView time;
        TextView price;
        ProgressBarView rate;
        LinearLayout top;
        TextView rating;
        TextView top1;
        TextView starnum;
        TextView response;
        TextView top2;
        TextView responsenum;
    }
    public ViewHolder holder = null;

    public View getView(int position, View convertView, ViewGroup parent) {
        RowItem rowItem = getItem(position);
 
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_listdriver, null);
            holder = new ViewHolder();

//        	int height = Taxi_System.checkScreen(context).heightPixels;
        	int width = Taxi_System.checkScreen(context).widthPixels;

        	TextView driver = (TextView) convertView.findViewById(R.id.driver);
        	TextView vehicle = (TextView) convertView.findViewById(R.id.vehicle);
        	TextView heart = (TextView) convertView.findViewById(R.id.heart);
        	TextView time = (TextView) convertView.findViewById(R.id.time);
        	TextView price = (TextView) convertView.findViewById(R.id.price);
        	ProgressBarView rate = (ProgressBarView) convertView.findViewById(R.id.bar3);
        	
        	ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        	ImageView star = (ImageView) convertView.findViewById(R.id.star);
            LinearLayout icon_holder = (LinearLayout) convertView.findViewById(R.id.top);
            icon_holder.getLayoutParams().height =  (int) (width * 0.25);
            TextView rating = (TextView) convertView.findViewById(R.id.rating);
        	TextView top1 = (TextView) convertView.findViewById(R.id.top1);
        	TextView starnum = (TextView) convertView.findViewById(R.id.starnum);
        	TextView response = (TextView) convertView.findViewById(R.id.response);
        	TextView top2 = (TextView) convertView.findViewById(R.id.top2);
        	TextView responsenum = (TextView) convertView.findViewById(R.id.responsenum);
        	
        	//icon.getLayoutParams().height = (int) ( width * 0.18);
        	//icon.getLayoutParams().width = (int) ( width * 0.25);
//            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) id
//                    .getLayoutParams();
            
        	
            holder.driver = driver;
            holder.vehicle = vehicle;
            holder.heart = heart;
            holder.time = time;
            holder.price = price;
            holder.top = icon_holder;
            holder.rate = rate;
            holder.rating = rating;
            holder.top1 = top1;
            holder.starnum = starnum;
            holder.response = response;
            holder.top2 = top2;
            holder.responsenum = responsenum;
            
            holder.image = icon;
            holder.star = star;
            convertView.setTag(holder);
        } 
        else
            holder = (ViewHolder) convertView.getTag();
        imageLoader.DisplayImage(rowItem.getImage() ,holder.image);
        holder.driver.setText(rowItem.getDriver());
        holder.vehicle.setText(rowItem.getVehicle());
        holder.heart.setText(rowItem.getHeart());
        holder.time.setText(rowItem.getTime());
        holder.price.setText(rowItem.getPrice());
        
        holder.rating.setText("(" +rowItem.getRating() +")");
        holder.top1.setText("Top "+rowItem.getTop1());
        holder.starnum.setText(rowItem.getStarnum());
           
        holder.response.setText("("+rowItem.getResponse()+")");
        holder.top2.setText("Top "+rowItem.getTop2());
        holder.rate.setValue(Integer.parseInt(rowItem.getResponsenum()));
        holder.rate.invalidate();
        holder.responsenum.setText(rowItem.getResponsenum() + "%");
        
        switch(Integer.parseInt(rowItem.getStarnum()) + 1)
        {
        case 1:
            holder.star.setImageResource(R.drawable.mot);
        break;
        case 2:
            holder.star.setImageResource(R.drawable.hai);
            break;
        case 3:
            holder.star.setImageResource(R.drawable.ba);
            break;         
        case 4:
            holder.star.setImageResource(R.drawable.bon);
            break;
        case 5:
            holder.star.setImageResource(R.drawable.nam);
            break;
        case 6:
            holder.star.setImageResource(R.drawable.sau);
            break;
        }
        return convertView;
    }
    
}