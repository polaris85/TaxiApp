package com.libreteam.driver;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.libreteam.driver.extra.Taxi_Constants;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Driver_Fragment_Off extends Fragment implements OnClickListener{

public DriverOff interfaceDriverOff;
private Context context;
public void onAttachFragment(Fragment fragment)
{
    try
    {
    	interfaceDriverOff = (DriverOff) fragment;

    } catch (ClassCastException e)
    {
          throw new ClassCastException(fragment.toString() + " must implement Off");
    }
 }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState)
{
super.onCreate(savedInstanceState);
View view = inflater.inflate(R.layout.fragment_status_off, null);
context = view.getContext();

	if (savedInstanceState == null) 
	{
//		getFragmentManager().beginTransaction()
//				.add(R.id.container, new PlaceholderFragment()).commit();
	}
	 initComponents(view);
     onAttachFragment(getParentFragment());
     final GestureDetector gesture = new GestureDetector(getActivity(),
    	        new GestureDetector.SimpleOnGestureListener() {

    	            @Override
    	            public boolean onDown(MotionEvent e) {
    	                return true;
    	            }

    	            @Override
    	            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
    	                float velocityY) {
    	                //Log.i("", "onFling has been called!");
    	                final int SWIPE_MIN_DISTANCE = 120;
    	                final int SWIPE_MAX_OFF_PATH = 250;
    	                final int SWIPE_THRESHOLD_VELOCITY = 200;
    	                try {
    	                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
    	                        return false;
    	                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
    	                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
    	                        //Log.i("", "Right to Left");
    	                        interfaceDriverOff.DriverOffClick("Two");
    	                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
    	                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
    	                       // Log.i("", "Left to Right");
    	                    }
    	                } catch (Exception e) {
    	                    // nothing
    	                }
    	                return super.onFling(e1, e2, velocityX, velocityY);
    	            }
    	        });

    	    view.setOnTouchListener(new View.OnTouchListener() {
    	        @Override
    	        public boolean onTouch(View v, MotionEvent event) {
    	            return gesture.onTouchEvent(event);
    	        }
    	    });
     
     return view;
	}
	private List<TextView> missCall = new ArrayList<TextView>();
	private void initComponents(View v)
	{
		Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));
		LinearLayout bot = (LinearLayout)v.findViewById(R.id.bot);
		Taxi_System.setContent(bot, v.getContext(), 1, (float) 0.15);
		Button submit = (Button) v.findViewById(R.id.off);
		submit.getLayoutParams().height = (int) (Taxi_System.getHeight(v.getContext()) * 0.01);
		submit.getLayoutParams().width = (int) (Taxi_System.getWidth(v.getContext()) * 0.25);
		submit.setOnClickListener(this);
		
		((TextView)v.findViewById(R.id.currently)).setTextSize(100);
		((TextView)v.findViewById(R.id.busy)).setTypeface(null, Typeface.BOLD);
		
		missCall.add(((TextView)v.findViewById(R.id.no1)));
		missCall.add(((TextView)v.findViewById(R.id.no2)));
		missCall.add(((TextView)v.findViewById(R.id.no3)));
		missCall.add(((TextView)v.findViewById(R.id.no4)));
		
		if(!Taxi_System.getSystem(context, "missCall").equalsIgnoreCase("") && Taxi_System.getSystem(context, "missCall") != null)
		{
			for(int i = 0; i< Taxi_System.getSystem(context, "missCall").length(); i++)
			{
				((TextView)missCall.get(i)).setText(String.valueOf("  "+Taxi_System.getSystem(context, "missCall").charAt(i))+"  ");
				missCall.get(i).setVisibility(View.VISIBLE);
			}
	    }
		new SendPostReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token")});
	}

public interface DriverOff
{
	public void DriverOffClick(String string);
}

public void getMissedCall()
{
	Taxi_System.testLog("fap fap fawp");
	new SendPostReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token")});
}

class SendPostReqAsyncTask extends AsyncTask<String, String, String>{
	@Override
	protected void onPreExecute()
	{
		if(Taxi_System.connectionStatus(context))
		{
    		//Taxi_System.showDialog(context);
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
    	String[] nameReq = new String[] {"token"};
        return Taxi_System.sendRequest(Taxi_Constants.DRIVER_SERVICES + "get_missed_call", nameReq, params);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Taxi_System.notShow();
        Taxi_System.testLog(result);
        if(result==null) {
			Taxi_System.showToast(context, getResources().getString(R.string.check_internet_connection));
			return;
		}
        JSONObject json;
		try {
			json = new JSONObject(result);
			if(json.getString("code").toString().equalsIgnoreCase("200"))
			{
				Taxi_System.testLog(json.getString("numMissed"));
				Taxi_System.addSystem(context, "missCall", json.getString("numMissed"));
				for(int i = 0; i< json.getString("numMissed").length(); i++)
				{
					Taxi_System.testLog(json.getString("numMissed").charAt(i));
                    char temp = json.getString("numMissed").charAt(i);
					((TextView)missCall.get(i)).setText("  "+String.valueOf(temp)+"  ");
					missCall.get(i).setVisibility(View.VISIBLE);
				}
			}
			else
			{
				Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
				for(int i = 0; i< Taxi_System.getSystem(context, "missCall").length(); i++)
				{
					((TextView)missCall.get(i)).setText(String.valueOf("  "+Taxi_System.getSystem(context, "missCall").charAt(i))+"  ");
					missCall.get(i).setVisibility(View.VISIBLE);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
}


@Override
public void onClick(View v) {
	if(v.getId() == R.id.off)
		interfaceDriverOff.DriverOffClick("Two");		
}

}
