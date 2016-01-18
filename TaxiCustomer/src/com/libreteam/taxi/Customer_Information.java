package com.libreteam.taxi;


import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.libreteam.taxi.R;
import com.libreteam.taxi.extra.CustomGridViewAdapter;
import com.libreteam.taxi.extra.Item;
import com.libreteam.taxi.extra.Customer_Constants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class Customer_Information extends Fragment implements OnClickListener {
	private GridView gridView,botomBar;
	private CustomGridViewAdapter customGridAdapter,customGridBottomBar;
	private ArrayList<Item> gridArray = new ArrayList<Item>() ;
	private ArrayList<Item> footerArray = new ArrayList<Item>() ;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private Context context;
	
	public CustomerMenu customerInterface;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
	    try
	    {
	    	customerInterface = (CustomerMenu) activity;
	    } 
	    catch (ClassCastException e)
	    {
	          throw new ClassCastException(" must implement MenuInterface");
	    }
	 }
	
	public interface CustomerMenu
	{
		public void didPressLogOut();
		public void didPressProfile();
		public void didPressStatistic();
		public void didPressCompany();
		public void didPressHistory();
		public void didPressShowMap();
//		public void didPressSettings();
	}
	
	@SuppressLint("InflateParams")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    View view = inflater.inflate(R.layout.customer_info, null);
	    context = view.getContext();
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		Session session = Session.getActiveSession();

        if (session == null) {
            if (savedInstanceState != null) 
            {
                //session = Session.restoreSession(getActivity(), null, statusCallback, savedInstanceState);
            }
            if (session == null)
            {
                session = new Session(getActivity());
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                //session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }

	    initComponents(view);
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
					return true;
		    	} else {
		    		return false;
		     	}
			}
		});
	    
		return view;
	}
    
	private class SessionStatusCallback implements Session.StatusCallback {
        @Override	        
        public void call(Session session, SessionState state, Exception exception) {
        	if(!session.isOpened()) return;
        		publishFeedDialog(session);
		   	}
		}
		
		@Override
		public void onStart() {
			super.onStart();
			Session.getActiveSession().addCallback(statusCallback);
		}

		@Override
		public void onStop() {
			super.onStop();
			Session.getActiveSession().removeCallback(statusCallback);
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
		  	Session session = Session.getActiveSession();
			Session.saveSession(session, outState);
		}
		
		private void publishFeedDialog(Session s) {
		    Bundle params = new Bundle();
		    params.putString("name", "Taxi");
		    params.putString("caption", "Great Application To Help You Travel.");
		    params.putString("description", "Great Application To Help You Travel.");
		    params.putString("link", "https://sflashcard.com");
		    params.putString("picture", "http://test.sflashcard.com/img/home.png");
		    WebDialog feedDialog = (
		        new WebDialog.FeedDialogBuilder(getActivity(),
		        		s,
		            params))
		        .setOnCompleteListener(new OnCompleteListener() {

		            @Override
		            public void onComplete(Bundle values,
		                FacebookException error) {
		                if (error == null) {
		                    final String postId = values.getString("post_id");
		                    if (postId != null) {
		                    	Taxi_System.showToast(context, context.getResources().getString(R.string.post_done));
		                    } else {
		                    	Taxi_System.showToast(context, context.getResources().getString(R.string.post_cancelled));
		                    }
		                } else if (error instanceof FacebookOperationCanceledException) {
		                	Taxi_System.showToast(context, context.getResources().getString(R.string.post_cancelled));
		                } else {
		                	Taxi_System.showToast(context, context.getResources().getString(R.string.error_post));
		                }
		            }
		        })
		        .build();
		    feedDialog.show();
		}
		
	private void initComponents(View v)
	{		        
        ((TextView)v.findViewById(R.id.prefix)).setText("USER: ");
		try 
		{
			((TextView)v.findViewById(R.id.status)).setText(new JSONObject(Taxi_System.getSystem(context, "userinfo")).getString("email"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
		Typeface font = Typeface.createFromAsset(context.getAssets(), "pt_sans.ttf");
		((TextView)v.findViewById(R.id.prefix)).setTypeface(font);
		((TextView)v.findViewById(R.id.status)).setTypeface(font);
		
        LinearLayout type = (LinearLayout) v.findViewById(R.id.type);
        type.getLayoutParams().width = (int) (Taxi_System.getWidth(context) * 0.9);
		
		botomBar = (GridView) v.findViewById(R.id.bottom); 
		botomBar.setColumnWidth(Taxi_System.getWidth(context) / 3);
		botomBar.setVerticalScrollBarEnabled(false);
		
		Bitmap t1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.rateapp);
		Bitmap t2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.facebook);
		Bitmap t3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.logout);
		
		int size = 32;
		footerArray.clear();
		footerArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(t1, size),"Rate App"));
		footerArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(t2, size),"Share"));
		footerArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(t3, size),"Log Out"));
		
		customGridBottomBar = new CustomGridViewAdapter(getActivity(), R.layout.adapter_gridview_info, footerArray);
		customGridBottomBar.setWidth(Taxi_System.getWidth(context));
		botomBar.setAdapter(customGridBottomBar);
		
		botomBar.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2)
				{
				case 0:
					//didSwitch();
					break;
		        case 1:
		        	if(!Taxi_System.connectionStatus(context))
					{
						Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
						return;
					}
		        	openSession();
					break;
		        case 2:
			        didLogOut();
			        break;
				default:
					break;
				}
			}
	    });	
		
		gridView = (GridView) v.findViewById(R.id.gridView); 
		gridView.setColumnWidth(Taxi_System.getWidth(context) / 3);
		gridView.setVerticalScrollBarEnabled(false);

		Bitmap bg1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.profile);
		Bitmap bg2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.statistics);
		Bitmap bg3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.history);
		Bitmap bg4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.taxicompani);
		Bitmap bg5 = BitmapFactory.decodeResource(this.getResources(), R.drawable.map);
//		Bitmap bg6 = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings);
		
		gridArray.clear();
		gridArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(bg1, size), 
											context.getResources().getString(R.string.profile)));
		gridArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(bg2, size), 
											context.getResources().getString(R.string.statistics)));
		gridArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(bg3, size),
											context.getResources().getString(R.string.history)));
		gridArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(bg4, size), 
											context.getResources().getString(R.string.taxi_companies)));
		gridArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(bg5, size),
											context.getResources().getString(R.string.show_map)));
//		gridArray.add(new Item(CustomGridViewAdapter.getRoundedCornerBitmap(bg6, size),"Settings"));
		
		customGridAdapter = new CustomGridViewAdapter(getActivity(), R.layout.adapter_gridview_info, gridArray);
		customGridAdapter.setWidth(Taxi_System.getWidth(context));
		gridView.setAdapter(customGridAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				switch(arg2)
				{
				case 0:
//					if(((Customer_Fragment_Activity)getActivity()).isInMenu)
//	            	{
//	            		((Customer_Fragment_Activity)getActivity()).didPopBackStack();
//	            	}
//					customerInterface.didPressProfile();
					new RequestUserInfor().execute();
		        break;
				case 1:
					new RequestUserInfor().execute();
					break;
				case 2:
					new didRequestHistory().execute(new String[]{Taxi_System.getSystem(context, "token"),
									Taxi_System.getSystem(context, "type")});
					break;
				case 3:
					if(((Customer_Fragment_Activity)getActivity()).isInMenu)
						((Customer_Fragment_Activity)getActivity()).didPopBackStack();
					customerInterface.didPressCompany();
					break;
				case 4:
					if(((Customer_Fragment_Activity)getActivity()).isInMenu)
						((Customer_Fragment_Activity)getActivity()).didPopBackStack();
					customerInterface.didPressShowMap();
					break;
//				case 5:
//					if(((Customer_Fragment_Activity)getActivity()).isInMenu)
//						((Customer_Fragment_Activity)getActivity()).didPopBackStack();
//					customerInterface.didPressSettings();
//					break;
				}

			}
	    });
		//new SendPostReqAsyncTask().execute(new String[]{Taxi_System.getSystem(context, "token")});
	}
	
	private void openSession()
	{
		Session session = Session.getActiveSession();

        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(getActivity(), this, true, statusCallback);
        }
	}
	
//	private void didSwitch()
//	{
//		//Taxi_System.didAddFragment(this, new Customer_ListDriver(), "code",new String[]{},true);
//	}
	
	private void didLogOut()
	{
		Taxi_System.addSystem(context, "token", "");
        customerInterface.didPressLogOut();
		Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
	}
	
	class didRequestHistory extends AsyncTask<String, String, String>{
    	@Override
    	protected void onPreExecute()
    	{
    		if(Taxi_System.connectionStatus(context))
	    		Taxi_System.showDialog(context);
    		else
    		{
    			Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
    			return;
    		}
        }
    	
        @Override
        protected String doInBackground(String... params) {
        	String[] nameReq = new String[] {"token","type"};	       		
        	return Taxi_System.sendRequest(Customer_Constants.CUSTOMER_HISORY, nameReq, params);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Taxi_System.testLog(result);
            Taxi_System.notShow();
            if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
            JSONObject json;
			try {
				json = new JSONObject(result);
				  switch(Integer.parseInt(json.getString("code").toString()))
                    {
                    	case 200:
                    	{
                    		Taxi_System.addSystem(context, "userhistory", result);
                            if(((Customer_Fragment_Activity)getActivity()).isInMenu)
                        	{
                        		((Customer_Fragment_Activity)getActivity()).didPopBackStack();
                        	}
                			customerInterface.didPressHistory();
                    	}
            			break;
                        case 401:
                        	Taxi_System.showToast(context, context.getResources().getString(R.string.invalid_type));
                        	break;   
                        case 500:
                        	Taxi_System.showToast(context, context.getResources().getString(R.string.server_error));
             			   	break; 
                        case 400:
                        	Taxi_System.showToast(context, context.getResources().getString(R.string.check_infor));
             			   	break;
             		   default:
             		   break;
                    }
			} catch (JSONException e) {
				e.printStackTrace();
			}
        } 
    }
	

	class RequestUserInfor extends AsyncTask<String, String, String>{
		@Override
    	protected void onPreExecute()
    	{
    		if(Taxi_System.connectionStatus(context))
	    		Taxi_System.showDialog(context);
    		else
    		{
    			Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
    			return;
    		}
        }

        @Override
        protected String doInBackground(String... params) {
        	return Taxi_System.getInfor(Customer_Constants.CUSTOMER_SERVICE + "info?token=" + Taxi_System.getSystem(context, "token"));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Taxi_System.notShow();
            if(result==null) {
				Taxi_System.showToast(context, context.getResources().getString(R.string.check_internet_connection));
				return;
			}
            JSONObject json ;
            try {
            	json = new JSONObject(result);
            	switch(Integer.parseInt(json.getString("code").toString()))
            	{
            	case 200:
            		JSONObject parse = new JSONObject(json.getString("info"));
            		Taxi_System.addSystem(context, "username", parse.getString("full_name"));
            		Taxi_System.addSystem(context, "userinfo", parse.toString());
            		if(((Customer_Fragment_Activity)getActivity()).isInMenu)
	            	{
	            		((Customer_Fragment_Activity)getActivity()).didPopBackStack();
	            	}
					customerInterface.didPressStatistic();
            		break;
                case 401:
                	Taxi_System.showToast(context, context.getResources().getString(R.string.invalid_type));
                	break;
                case 500:	
                	Taxi_System.showToast(context, context.getResources().getString(R.string.server_error));
     			   	break; 
                case 400:
                	Taxi_System.showToast(context, context.getResources().getString(R.string.not_internet_connection));
     			   	break;
                default:
                	break;
            	}
            }
            catch (JSONException e) {
				e.printStackTrace();
			}            
        } 
	}
	
	@Override
	public void onClick(View v) {

	}
}
