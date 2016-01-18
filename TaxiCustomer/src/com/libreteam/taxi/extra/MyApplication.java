package com.libreteam.taxi.extra;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;

public class MyApplication extends Application{

    private static Context context;
    Map<String, Fragment.SavedState> savedStateMap;

    public void onCreate(){
        super.onCreate();
        savedStateMap = new HashMap<String, Fragment.SavedState>();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public void setFragmentSavedState(String key, Fragment.SavedState state){
        savedStateMap.put(key, state);
    }

    public Fragment.SavedState getFragmentSavedState(String key){
        return savedStateMap.get(key);
    }
}