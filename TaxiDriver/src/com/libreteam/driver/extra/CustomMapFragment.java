package com.libreteam.driver.extra;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomMapFragment extends SupportMapFragment {

	//  private static final String LOG_TAG = "CustomMapFragment";
	
    public CustomMapFragment() {
        super();
    }

    public static CustomMapFragment newInstance() {
    	CustomMapFragment	fragment = new CustomMapFragment();
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
        View v = super.onCreateView(arg0, arg1, arg2);
        Fragment fragment = getParentFragment();
        if (fragment != null && fragment instanceof OnMapReadyListener) 
        {
            ((OnMapReadyListener) fragment).onMapReady();
        }
        return v;
    }

    public static interface OnMapReadyListener {
        void onMapReady();
		void onAttachFinish();
    }
    
//    private static final String TAG = "MyFragment";

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

    	try {
        Animation anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), nextAnim);

        anim.setAnimationListener(new AnimationListener() {

            public void onAnimationStart(Animation animation) {
                // additional functionality 
            }

            public void onAnimationRepeat(Animation animation) {
                // additional functionality
            }

            public void onAnimationEnd(Animation animation) {
                if (getParentFragment() != null && getParentFragment() instanceof OnMapReadyListener) 
                {
                    ((OnMapReadyListener) getParentFragment()).onAttachFinish();
                }
            }
        });
        return anim;
    	} catch(Exception e) {
    		
    	}

        return null;
    } 
}