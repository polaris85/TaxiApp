package com.libreteam.taxi;

import com.libreteam.taxi.R;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Customer_DriverLocation extends Fragment implements OnClickListener {
	public DriverLocation interfaceDriverLocation;
	public EditText drLocation;
	
    public void onAttachFragment(Fragment fragment)
    {
        try
        {
        	interfaceDriverLocation = (DriverLocation) fragment;

        } catch (ClassCastException e)
        {
              throw new ClassCastException(fragment.toString() + " must implement DriverLocation");
        }
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_driver_location, null);
		initComponents(view);
		onAttachFragment(getParentFragment());
		return view;
	}
	
	private void initComponents(View v)
	{
	    drLocation = (EditText) v.findViewById(R.id.drLoc);
		Button submit = (Button) v.findViewById(R.id.drLocSubmit);
		submit.setOnClickListener(this);
		
		Bundle bundle = this.getArguments();
		drLocation.setText( bundle.getString("drlocation", "none"));
	}
	
	public interface DriverLocation
	{
		public void driverLocationSubmit(String string);
	}

	public void passData(String i) {
	    
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.drLocSubmit)
		interfaceDriverLocation.driverLocationSubmit(drLocation.getText().toString());
	}
	

}
