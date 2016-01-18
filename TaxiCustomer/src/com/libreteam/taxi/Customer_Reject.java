package com.libreteam.taxi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.libreteam.taxi.extra.Socket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Customer_Reject extends Fragment implements OnClickListener{
	private Context context;
	private String reasons;
	private List<CheckBox> listCheckbox = new ArrayList<CheckBox>();

	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.customer_reject, null);
		context = view.getContext();
		if (savedInstanceState == null) {
			initComponents(view);
		}
	
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new View.OnKeyListener() {
	        @Override
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	            if( keyCode == KeyEvent.KEYCODE_BACK ) {
	            	if(((Customer_Fragment_Activity)getActivity()).isMenu)
	            	{
	            		((Customer_Fragment_Activity)getActivity()).didHideMenu();
	            		return true;
	            	}
	            	((Customer_Fragment_Activity)getActivity()).didFinish();
	                return true;
	            } else {
	                return false;
	            }
	        }
	    });	
		
		return view;
	}

	private void initComponents(View v)
	{
		Taxi_System.applyFonts(v, Taxi_System.faceType(getActivity()));
		Taxi_System.setContent((LinearLayout)v.findViewById(R.id.header),context,1,(float) 0.07);

		LinearLayout thirdsection = (LinearLayout)v.findViewById(R.id.thirdsection);
		Taxi_System.setContent(thirdsection, context, (float) 0.125, (float)0.07);
		
		ImageView thirdimg = (ImageView)v.findViewById(R.id.thirdimg);
		Taxi_System.setContent(thirdimg, context, (float)0.06, (float)0.04);
		
		LinearLayout logosection = (LinearLayout)v.findViewById(R.id.logosection);
		Taxi_System.setContent(logosection, context, (float) 0.25, (float)0.07);
		ImageView logo = (ImageView)v.findViewById(R.id.logo);
//		Taxi_System.setContent(logo, context, Float.parseFloat((String) getResources().getText(R.string.logosize_w)), Float.parseFloat((String) getResources().getText(R.string.logosize)));
		Taxi_System.setContentLogo(logo, context, (float) 0.25, (float)0.07);
		
		((TextView)v.findViewById(R.id.send)).setTypeface(null, Typeface.BOLD);
		LinearLayout border = (LinearLayout)v.findViewById(R.id.border);
		Taxi_System.setContent(border, context, (float) 0.5, (float)0.05);
		
		Button send = (Button) v.findViewById(R.id.send);
		send.setOnClickListener(this);
		
		CheckBox notwait = (CheckBox) v.findViewById(R.id.notwait);
		notwait.setOnClickListener(this);
		CheckBox toolong = (CheckBox) v.findViewById(R.id.toolong);
		toolong.setOnClickListener(this);
		CheckBox wrongdir = (CheckBox) v.findViewById(R.id.wrongdir);
		wrongdir.setOnClickListener(this);
		CheckBox other = (CheckBox) v.findViewById(R.id.other);
		other.setOnClickListener(this);
		
		listCheckbox.add(notwait);
		listCheckbox.add(toolong);
		listCheckbox.add(wrongdir);
		listCheckbox.add(other);
	}
    
	private String didSendData()
	{
		reasons = "";
		for(CheckBox checkB : listCheckbox)
		{
			if(checkB.isChecked())
			{
				reasons = reasons + checkB.getText() + "-";
			}
		}
		return reasons + ((EditText)getView().findViewById(R.id.note)).getText().toString();
	}
	
	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.send:
//			Taxi_System.didAddFragment(this, new Customer_Map(), "code",new String[]{},true);
//			getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			((Customer_Fragment_Activity)getActivity()).didFinish();
			HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
			try {
				hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
				hash.put("note", new JSONObject().put("note", didSendData()));
			} catch (JSONException e) {
				e.printStackTrace();
			}
				
			Socket.socketDidSendMessage(Taxi_System.jsonString(
				new String[]{"code","type"},
				new String[]{"9",Taxi_System.getSystem(context, "type")},
				new String[]{"token","note"},
				hash,
				null,null
			));
			break;
		}
	}
	
}
