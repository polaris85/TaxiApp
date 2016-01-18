package com.libreteam.taxi.extra;

import com.libreteam.taxi.R;
import com.libreteam.taxi.Taxi_System;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressLint("InflateParams")
public class Taxi_Menu_Activity extends Fragment implements OnClickListener {

	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_sfc_main);
		View view = inflater.inflate(R.layout.customer_menu, null);

		context = view.getContext();
		//db = new SFC_DataHelper(context);
		initComponents(view);
		return view;
	}

	private void initComponents(View v)
	{
		int height = Taxi_System.checkScreen(context).heightPixels;
//	    	int width = Taxi_System.checkScreen(context).widthPixels;

//	    	RelativeLayout h = (RelativeLayout) v.findViewById(R.id.head);
//
//	    	h.getLayoutParams().height = (int) (height * 0.1);
//	    	h.getLayoutParams().width = (int) (width * 1);
//	    	
//	    	TextView label = (TextView) v.findViewById(R.id.label);
//	    	label.getLayoutParams().height = (int) (height * 0.05);
//	    	label.setTextSize((int)(width * 0.03));
	    	
		Button b = (Button) v.findViewById(R.id.signUp);
		b.getLayoutParams().height = (int) (height * 0.07);
	   	b.getLayoutParams().width = (int) (height * 0.09);
	   	b.setOnClickListener(this);
	}
		
	 
	@Override
	public void onClick(View v) {
		final FragmentTransaction ft = getFragmentManager().beginTransaction(); 
		ft.setCustomAnimations(R.anim.right_b, R.anim.left_b,R.anim.left,R.anim.right);
//		Customer_Login frag = new Customer_Login();
//		Bundle data = new Bundle();
//		data.putString("colId",String.valueOf(rowItems.get(position - 1).getMainId()));
//		data.putString("col_name",rowItems.get(position - 1).getTitle());
//		frag.setArguments(data);

//		ft.replace(R.id.main_content_fragment, frag , "NewFragmentTag"); 
//		ft.addToBackStack(null);
//		ft.commit(); 
	}
}
