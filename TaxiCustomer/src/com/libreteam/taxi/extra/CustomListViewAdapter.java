package com.libreteam.taxi.extra;

import java.util.List;

import com.libreteam.taxi.R;
import com.libreteam.taxi.Taxi_System;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
 
@SuppressLint("InflateParams")
public class CustomListViewAdapter extends ArrayAdapter<RowItem> {
    private Context context;
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
        TextView rate_h;
        TextView response_h;
        TextView esti_h;
//        RatingBar barStar;
        ImageView ivStar[] = new ImageView[5];
    }
    public ViewHolder holder = null;

    public View getView(int position, View convertView, ViewGroup parent)
    {
        RowItem rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_listdriver, null);
            holder = new ViewHolder();
            
        	int height = Taxi_System.checkScreen(context).heightPixels;
        	int width = Taxi_System.checkScreen(context).widthPixels;

        	holder.driver = (TextView) convertView.findViewById(R.id.driver);
        	holder.vehicle = (TextView) convertView.findViewById(R.id.vehicle);
        	holder.heart = (TextView) convertView.findViewById(R.id.heart);
        	holder.time = (TextView) convertView.findViewById(R.id.time);
        	holder.price = (TextView) convertView.findViewById(R.id.price);
        	holder.rate = (ProgressBarView) convertView.findViewById(R.id.bar3);
//        	holder.barStar = (RatingBar) convertView.findViewById(R.id.barStar);
        	holder.ivStar[0] = (ImageView) convertView.findViewById(R.id.ivRankStar1);
        	holder.ivStar[1] = (ImageView) convertView.findViewById(R.id.ivRankStar2);
        	holder.ivStar[2] = (ImageView) convertView.findViewById(R.id.ivRankStar3);
        	holder.ivStar[3] = (ImageView) convertView.findViewById(R.id.ivRankStar4);
        	holder.ivStar[4] = (ImageView) convertView.findViewById(R.id.ivRankStar5);
        	
        	holder.image  = (ImageView) convertView.findViewById(R.id.icon);
        	holder.top = (LinearLayout) convertView.findViewById(R.id.top);
        	holder.top.getLayoutParams().height =  (int) (width * 0.25);
            
        	holder.rating = (TextView) convertView.findViewById(R.id.rating);
        	holder.top1 = (TextView) convertView.findViewById(R.id.top1);
        	holder.starnum = (TextView) convertView.findViewById(R.id.starnum);
        	holder.response = (TextView) convertView.findViewById(R.id.response);
        	holder.top2 = (TextView) convertView.findViewById(R.id.top2);
        	holder.responsenum = (TextView) convertView.findViewById(R.id.responsenum);
        	
        	LinearLayout detail = (LinearLayout) convertView.findViewById(R.id.detail);
        	detail.getLayoutParams().height =  (int) (height * 0.08);
        	            
            holder.rate_h = (TextView) convertView.findViewById(R.id.rate_h);
            holder.response_h = (TextView) convertView.findViewById(R.id.response_h);
            holder.esti_h = (TextView) convertView.findViewById(R.id.esti);
            
            convertView.setTag(holder);
        } 
        else
            holder = (ViewHolder) convertView.getTag();
        

        imageLoader.DisplayImage(rowItem.getImage() ,holder.image);
        holder.driver.setText(rowItem.getAppName());        
        holder.vehicle.setText(rowItem.getVehicle());
        holder.heart.setText(String.format("%.2f", Float.parseFloat(rowItem.getHeart())));
        holder.time.setText(rowItem.getTime());
        if(rowItem.getPrice().equals("0.0"))
        	holder.price.setText("--");
        else
        	holder.price.setText(rowItem.getPrice());
        
        holder.rating.setText("(" +rowItem.getRating() +")");
        holder.top1.setText("Top "+rowItem.getTop1());
        holder.starnum.setText(rowItem.getStarnum());
           
        holder.response.setText("("+rowItem.getResponse()+")");
        holder.top2.setText("Top "+rowItem.getTop2());
        holder.rate.setValue((int)Float.parseFloat(rowItem.getResponsenum()));
        holder.rate.invalidate();
        holder.responsenum.setText((int)Float.parseFloat(rowItem.getResponsenum()) + "%");

        ////////////////////////////////////
//        float step = (float) (Float.parseFloat(rowItem.getStarnum()) /1000 * 5);
//        holder.barStar.setStepSize((step));
        ////////////////////////////////////
//        holder.barStar.setRating(Float.parseFloat(rowItem.getStarnum()));

//        holder.ivStar[i].setBackground(context.getResources().getDrawable(R.drawable.star_acivite));
        for(int i=0; i<5; i++)
        	Taxi_System.setContent(holder.ivStar[i], context, 0.066f, 0.03f);
        
        float rankstarnum = Float.parseFloat(rowItem.getStarnum());
        int full_star = (int) rankstarnum;
        
        for (int i=0; i<5; i++) {
        	if (i < full_star)
        		holder.ivStar[i].setImageDrawable(context.getResources().getDrawable(R.drawable.star_acivite));
//	        	holder.ivStar[i].setBackgroundResource(R.drawable.star_acivite);
	        else
//	        	holder.ivStar[i].setBackgroundResource(R.drawable.star_background);
	        	holder.ivStar[i].setImageDrawable(context.getResources().getDrawable(R.drawable.star_background));
	    }
        if ((rankstarnum - full_star) > 0.85) {
        	holder.ivStar[full_star].setImageDrawable(context.getResources().getDrawable(R.drawable.star_acivite));
        } else if((rankstarnum - full_star) > 0.65) {
        	holder.ivStar[full_star].setImageDrawable(context.getResources().getDrawable(R.drawable.star_acivite3));
        } else if((rankstarnum - full_star) > 0.4) {
        	holder.ivStar[full_star].setImageDrawable(context.getResources().getDrawable(R.drawable.star_acivite2));
        } else if((rankstarnum - full_star) > 0.15) {
        	holder.ivStar[full_star].setImageDrawable(context.getResources().getDrawable(R.drawable.star_acivite1));
        } 
        int w = holder.ivStar[0].getLayoutParams().width;
        int h = holder.ivStar[0].getLayoutParams().height;
        int w1 = holder.rate.getLayoutParams().width;
//    	for(int i=0;i<5;i++) {
//    		holder.ivStar[i].getLayoutParams().width = holder.rate.getLayoutParams().width/6;
//    		holder.ivStar[i].getLayoutParams().height = holder.rate.getLayoutParams().width/6;
//    	}
        Taxi_System.applyFonts(convertView, Typeface.createFromAsset(context.getAssets(), "pt_sans.ttf"));
        Typeface font = Typeface.createFromAsset(context.getAssets(), "pt_sans_normal.ttf");		   
        holder.rate_h.setTypeface(font);
        holder.response_h.setTypeface(font);
        holder.esti_h.setTypeface(font);

        holder.time.setTypeface(null, Typeface.BOLD);
        holder.price.setTypeface(null, Typeface.BOLD);
        return convertView;
    }
    
    public int generateRandomNumber(int min,int max)
    {
    long randomNumber = 0L;

    if(min==max)
    return min;

    if(min>max)
    return min;

    if((max-min)<=1)
    return min;

    if((max-min)==2)
    return (min+1);

    randomNumber = min + (int)(Math.random() * ((max - min) + 1));
    return (int) randomNumber;
    }
    
}