package com.libreteam.driver.extra;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class Dialog extends DialogFragment {

	
	public OnFragmentClickListener mListener;
	
	public interface OnFragmentClickListener {
	    public void onFragmentClick(int action, Object object);
	}
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    try {
	        mListener = (OnFragmentClickListener) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement listeners!");
	    }
	}
	
	@Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
            .setTitle("Attention!")
            .setMessage("This will cancel this ride?")
            .setNegativeButton(android.R.string.no, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
            })
            .setPositiveButton(android.R.string.yes,  new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                	
                }
            })
            .create();
    }
	
}
