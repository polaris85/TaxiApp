package com.libreteam.driver;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libreteam.driver.extra.ProgressBarView;


public class Driver_StatisTic extends Fragment {
    private Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.driver_statistic, null);
		context = view.getContext();
		if (savedInstanceState == null) 
	         initComponents(view);
		return view;
	}
	
	private void initComponents(View v)
	{
		Taxi_System.applyFonts_singleLine(v, Taxi_System.faceType(getActivity()));
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
		
		setStar(((ImageView)v.findViewById(R.id.star)),4);
		ProgressBarView rate = (ProgressBarView) v.findViewById(R.id.bar);
		rate.setValue(86);
        rate.invalidate();
        
//        ((TextView)v.findViewById(R.id.rating_total)).setText("total");
 //       ((TextView)v.findViewById(R.id.rating_average)).setText("average");
        ((TextView)v.findViewById(R.id.rating_average)).setTypeface(null, Typeface.BOLD);
//        
//        ((TextView)v.findViewById(R.id.response_total)).setText("total");
//        ((TextView)v.findViewById(R.id.response_average)).setText("average");
        ((TextView)v.findViewById(R.id.response_average)).setTypeface(null, Typeface.BOLD);
//        
//        ((TextView)v.findViewById(R.id.monthly_total)).setText("total");
//        ((TextView)v.findViewById(R.id.monthly_average)).setText("average");
//        
//        ((TextView)v.findViewById(R.id.ridecomplete_total)).setText("total");
//        ((TextView)v.findViewById(R.id.ridecomplete_average)).setText("average");
        ((TextView)v.findViewById(R.id.ridecomplete_average)).setTypeface(null, Typeface.BOLD);
//        
//        ((TextView)v.findViewById(R.id.totalcomplete_total)).setText("total");
//        ((TextView)v.findViewById(R.id.totalcomplete_average)).setText("average");
//        
//        ((TextView)v.findViewById(R.id.percent_total)).setText("total");
//        ((TextView)v.findViewById(R.id.percent_average)).setText("average");
//        
//        ((TextView)v.findViewById(R.id.cancel_total)).setText("total");
//        ((TextView)v.findViewById(R.id.cancel_average)).setText("average");
//        
//        ((TextView)v.findViewById(R.id.earn_total)).setText("total");
//        ((TextView)v.findViewById(R.id.earn_average)).setText("average");
//        
        ((TextView)v.findViewById(R.id.earn_average)).setTypeface(null, Typeface.BOLD);
//        ((TextView)v.findViewById(R.id.wait_total)).setText("total");
//        ((TextView)v.findViewById(R.id.wait_average)).setText("average");
//        
//        ((TextView)v.findViewById(R.id.status_total)).setText("total");
//        ((TextView)v.findViewById(R.id.status_average)).setText("average");
//        
//        ((TextView)v.findViewById(R.id.return_total)).setText("total");
//        ((TextView)v.findViewById(R.id.return_average)).setText("average");
        ((TextView)v.findViewById(R.id.return_average)).setTypeface(null, Typeface.BOLD);
//        
//        ((TextView)v.findViewById(R.id.bad_total)).setText("total");
//        ((TextView)v.findViewById(R.id.bad_average)).setText("average");
//        
//        ((TextView)v.findViewById(R.id.note_total)).setText("total");
//        ((TextView)v.findViewById(R.id.note_average)).setText("average");
	}
	
	private void setStar(ImageView image, int no)
	{
		switch(no)
        {
        case 1:
        	image.setImageResource(R.drawable.mot);
        break;
        case 2:
        	image.setImageResource(R.drawable.hai);
            break;
        case 3:
        	image.setImageResource(R.drawable.ba);
            break;         
        case 4:
        	image.setImageResource(R.drawable.bon);
            break;
        case 5:
        	image.setImageResource(R.drawable.nam);
            break;
        case 6:
        	image.setImageResource(R.drawable.sau);
            break;
        }
	}
		
}
