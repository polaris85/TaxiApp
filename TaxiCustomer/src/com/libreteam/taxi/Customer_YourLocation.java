package com.libreteam.taxi;

import com.libreteam.taxi.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("InflateParams")
public class Customer_YourLocation extends Fragment implements OnClickListener {
	public YourLocation interfaceYourLocation;
	public EditText urLocation;
	
    public void onAttachFragment(Fragment fragment)
    {
        try
        {
        	interfaceYourLocation = (YourLocation) fragment;

        }
        catch (ClassCastException e)
        {
              throw new ClassCastException(fragment.toString() + " must implement YourLocation");
        }
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_your_location, null);
		initComponents(view);
		onAttachFragment(getParentFragment());
		return view;
	}
	
	private void initComponents(View v)
	{
	    urLocation = (EditText) v.findViewById(R.id.urLoc);
		Button submit = (Button) v.findViewById(R.id.urLocSubmit);
		submit.setOnClickListener(this);
		
		Bundle bundle = this.getArguments();
		urLocation.setText(bundle.getString("urlocation", ""));
	}
	
	public interface YourLocation
	{
		public void yourLocationSubmit(String string);
	}

	public void passData(String i) {
	    
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.urLocSubmit)
		interfaceYourLocation.yourLocationSubmit(urLocation.getText().toString());
	}
}
