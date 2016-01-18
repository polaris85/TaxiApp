package com.libreteam.driver;



import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Driver_Fragment_On extends Fragment implements OnClickListener {
	public DriverOn interfaceDriverOn;

	public void onAttachFragment(Fragment fragment)
    {
        try
        {
        	interfaceDriverOn = (DriverOn) fragment;

        } catch (ClassCastException e)
        {
              throw new ClassCastException(fragment.toString() + " must implement On");
        }
     }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_status_on, null);

		if (savedInstanceState == null) {
//			getFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
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
	    	                Log.i("", "onFling has been called!");
	    	                final int SWIPE_MIN_DISTANCE = 120;
	    	                final int SWIPE_MAX_OFF_PATH = 250;
	    	                final int SWIPE_THRESHOLD_VELOCITY = 200;
	    	                try {
	    	                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
	    	                        return false;
	    	                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
	    	                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	    	                       // Log.i("", "Right to Left");
	    	                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
	    	                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
	    	                        //Log.i("", "Left to Right");
	    	                        interfaceDriverOn.DriverOnClick("One");
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
		
	private void initComponents(View v)
	{
		Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));
		LinearLayout bot = (LinearLayout)v.findViewById(R.id.bot);
		Taxi_System.setContent(bot, v.getContext(), 1, (float) 0.15);
		Button submit = (Button) v.findViewById(R.id.on);
		submit.getLayoutParams().height = (int) (Taxi_System.getHeight(v.getContext()) * 0.01);
		submit.getLayoutParams().width = (int) (Taxi_System.getWidth(v.getContext()) * 0.25);
		submit.setOnClickListener(this);
		TextView status = (TextView)v.findViewById(R.id.currently);
		status.setTextSize(100);
		((TextView)v.findViewById(R.id.available)).setTypeface(null, Typeface.BOLD);

		//TextView available = (TextView)v.findViewById(R.id.available);
		//available.setTextSize(100);
		//((TextView)v.findViewById(R.id.swit)).setTextSize(100);
	}

	public interface DriverOn
	{
		public void DriverOnClick(String string);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.on)
			interfaceDriverOn.DriverOnClick("One");		
	}

}
