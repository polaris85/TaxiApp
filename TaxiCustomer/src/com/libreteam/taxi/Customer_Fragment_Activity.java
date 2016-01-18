package com.libreteam.taxi;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.libreteam.taxi.Customer_Information.CustomerMenu;
import com.libreteam.taxi.extra.Customer_Constants;
import com.libreteam.taxi.extra.Socket;
import com.libreteam.taxi.extra.Socket.SocketRespond;
import com.libreteam.taxi.extra.Taxi_Menu;

@SuppressLint({ "Recycle", "InflateParams" })
public class Customer_Fragment_Activity extends FragmentActivity implements CustomerMenu {
	public String ride_id,customer_token,username,address,latlng;
	public LatLng driverLatLng;
	private Taxi_Menu mLayout;
	public Button btMenu,button;
    private Context context;
    public boolean isMenu,isInMenu,isInCalling;
    private String token;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		isMenu = false;
		isInMenu = false;
		isInCalling = false;
		setContentView(this.getLayoutInflater().inflate(R.layout.main_fragment_activity, null));
		Taxi_System.setContent((FrameLayout)findViewById(R.id.dummy), getApplicationContext(), 1, (float)0.07);
		Taxi_System.setContent((FrameLayout)findViewById(R.id.banner), getApplicationContext(), 1, (float)0.05);
		button = (Button)findViewById(R.id.menu);
		button.getLayoutParams().width = (int) (Taxi_System.getHeight(context) * 0.07);
		button.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * 0.07);
		button.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
			    if(!isMenu)
			    	getSupportFragmentManager()
			    	.beginTransaction()
			    	.setCustomAnimations(R.anim.abc_fade_in, 0)
			    	.replace(R.id.menuframe, new Customer_Information(),"menu")
			    	.disallowAddToBackStack()
			    	.commitAllowingStateLoss();
			    else			    	
			    	getSupportFragmentManager()
			    	.beginTransaction()
			    	.setCustomAnimations(0, R.anim.abc_fade_out)
			    	.remove(getSupportFragmentManager()
			    			.findFragmentByTag("menu"))
			    	.disallowAddToBackStack()
			    	.commitAllowingStateLoss();
			    isMenu =! isMenu;
			}
		});

		setActivity();		
		Socket.respond = new SocketRespond() {
			@Override
			public void respondData(String string) {
				Taxi_System.testLog("CUSTOMER-MAIN" + string);
				if(Taxi_System.getSystem(context, "type").equalsIgnoreCase("user"))
				{
					try 
					{
						didReceiveCode(Integer.valueOf(new JSONObject(string).getString("code").toString()),string);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
			    }
			}
		};
	
	}
	
	private void setActivity() {
		didAddFragment(new Customer_Login() , "code", new String[]{}, true);
		if(!Taxi_System.connectionStatus(context)){ 
			Taxi_System.showToast(context,context.getResources().getString(R.string.not_internet_connection)); 
			return;
		}
		Taxi_System.checkGPS(getApplicationContext(), this);
		if(!Taxi_System.getSystem(context, "token").equalsIgnoreCase("") && Taxi_System.getSystem(context, "token") != null)
		{
			didAddFragment(new Customer_Map() , "code", new String[]{}, true);
			button.setVisibility(View.VISIBLE);
	    } else
			button.setVisibility(View.GONE);
	}
	
	public void didReceiveCode(int code,String string) throws JSONException
	{
		switch(code)
		{
		case 0:
			break;
		case 1:
			try 
			{
				ride_id = new JSONObject(string).getJSONObject("info").getString("ride_id").toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case 2:
		case 3:
			didCancelRide();
			break;
		case 4:
			Taxi_System.testLog(new JSONObject(string).getJSONObject("userinfo"));
			customer_token = new JSONObject(string).getJSONObject("info").getString("token").toString();
			latlng = new JSONObject(string).getJSONObject("userinfo").getString("latlng").toString();
			address = new JSONObject(new JSONObject(string).getString("userinfo")).getString("address");
			Taxi_System.addSystem(context, Customer_Constants.PICK_UP, address+"");
			didReceiveRide(
					  new String[]{
					  new JSONObject(string).getJSONObject("info").getString("ride_id").toString(),
					  new JSONObject(string).getJSONObject("info").getString("token").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("address").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("latlng").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("car_model").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("time").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("username").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("taxi_company").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("license_plate").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("avatar").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("des_lat").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("des").toString(),
					  new JSONObject(string).getJSONObject("userinfo").getString("start_lat").toString()
			          });
			break;
		case 7:
			 didGetCall(new String[]{
			 new JSONObject(string).getJSONObject("userinfo").getString("name").toString(),		 
			 new JSONObject(string).getJSONObject("userinfo").getString("taxi_company").toString(),
			 new JSONObject(string).getJSONObject("userinfo").getString("car_model").toString(),
			 ride_id,
			 customer_token,
			 latlng,
			 new JSONObject(string).getJSONObject("userinfo").getString("des_lat").toString(),		 
			 new JSONObject(string).getJSONObject("userinfo").getString("des_address").toString(),
			 new JSONObject(string).getJSONObject("userinfo").getString("start_lat").toString(),
			 new JSONObject(string).getJSONObject("userinfo").getString("latlng").toString()
			 });
			 break;
		case 11:
			 driverLatLng = new LatLng
			 (
				Double.valueOf(new JSONObject(string).getJSONObject("info").getString("lat")),
				Double.valueOf(new JSONObject(string).getJSONObject("info").getString("lng"))
			 );
			 Handler handler = new Handler(Looper.getMainLooper());
			 handler.post(new Runnable(){
				 @Override
				 public void run() {
					 new Customer_Boarding().didGetPosition(driverLatLng);
					 new Customer_Finish_Rating().didGetPosition(driverLatLng);
				 } 
			 });
			 break;
		}
	}
	
	//CUSTOMER_GETCANCEL_REJECT_RIDE_POPBACKSTACK
	public void didPopBackStack() {
		getSupportFragmentManager().popBackStack();
	}
	
	public void didFinish() {
		didAddFragment(new Customer_Map(), "code",new String[]{},true);
	}
	
	private void didCancelRide() {
		didReceiveAlert(new String[]{"Driver is busy","0"});
		didAddFragment(new Customer_Map(), "code",new String[]{},true);
	}
	
	public void didRejectRide()
	{
		didAddFragment(new Customer_Reject(), "code",new String[]{},true);
	}	
	
	//CUSTOMER_LOGIN	
	public void didLogIn()
	{
		button.setVisibility(View.VISIBLE);
		didAddFragment(new Customer_Map() , "code", new String[]{}, true);
	}	
	
	//CUSTOMER_MAP
	public void didGetListDriver(String[] string)
	{
		didAddFragment(new Customer_ListDriver(), "drivers",string,true);
	}

	//CUSTOMER_LISTDRIVERS
	public void didCallDriver(String[] string)
	{
		didAddFragment(new Customer_Calling(), "driverid",string,true);
	}
	
	public void didMap()
	{
		didAddFragment(new Customer_Map(), "code",new String[]{},true);
	}
	
	//CUSTOMER_CALLING	
	private void didReceiveRide(String[] string)
	{
		didReceiveAlert(new String[]{"Driver has accepted your offer","1"});
		didAddFragment(new Customer_Boarding(),"rideid",string,true); 
	}
	
	//CUSTOMER_BOARDING	
	public void didAcceptRide(String[] string)
	{
		didAddFragment(new Customer_Finish_Rating(),"rideid",string,true); 
	}
	
	public void didGetCall(String[] string)
	{
		didReceiveAlert(new String[]{"Taxi has arrived","1"});
		if(isInCalling) return;
		didAddFragment(new Customer_GotCalling(), "rideid",string,true);
		isInCalling = true;
	}
	
	public void didStartTracking()
	{
		
	}
	
	//CUSTOMER_FINISH	
	public void didFisishRating(String[] string)
	{
		didAddFragment(new Customer_Rating(),"rideid",string,true); 
	}
	
	public void didFinishAll()
	{
		finish();
	}		
	
	public void didReceiveAlert(String[] string)
	{
		final Fragment alert = new Banner();
		Bundle alertText = new Bundle();
		alertText.putString("content", string[0]);
		alertText.putString("isAccept", string[1]);
		alert.setArguments(alertText);
		getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_fade_out).replace(R.id.dummy, alert,"banner").disallowAddToBackStack().commitAllowingStateLoss();
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				new Handler().postDelayed(new Runnable(){
					@Override
			 		public void run() {
			 			getSupportFragmentManager()
			 			.beginTransaction()
			 			.setCustomAnimations(R.anim.abc_fade_out,R.anim.abc_slide_out_top)
			 			.remove(alert)
			 			.commitAllowingStateLoss();
			 		}},2500);
			}
		});
	}
	
	@SuppressLint("ValidFragment")
	private class Banner extends Fragment
	{
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState)
	  {
		super.onCreate(savedInstanceState);
	    View view = inflater.inflate(R.layout.fragment_alert, null);
	    TextView alert = (TextView)view.findViewById(R.id.alert);
	    alert.setText(getArguments().getString("content"));

	    if(getArguments().getString("isAccept").equalsIgnoreCase("1"))
	    {
		    ((ImageView)view.findViewById(R.id.alertimg)).setImageDrawable(getResources().getDrawable(R.drawable.accept));
		    alert.setTextColor(Color.parseColor("#479917"));
	    }
	    else
	    {
		    ((ImageView)view.findViewById(R.id.alertimg)).setImageDrawable(getResources().getDrawable(R.drawable.decline));
		    alert.setTextColor(Color.RED);
	    }
	    return view;
	  }
	}
	
	
	@Override
	protected void onResume() 
	{
	    super.onResume();
	    Taxi_System.checkPlayServices(this);
	    token = Taxi_System.getSystem(context, "token");
//	    Toast.makeText(context, token, Toast.LENGTH_SHORT).show();
//	    if(token!=null && !token.equals("") && Socket.socketIO()==null) {
//	    	Socket.getConnection();
//	    	Socket.socketDidConnect();
//	    }
//	    Socket.socketDidConnect(context);
//	    countTime();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	 
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
	
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    }
	
	public void toggleMenu(View v) {
		mLayout.toggleMenu();
	}
	
	@Override
	public void onBackPressed() 
	{
		isInMenu = false;
		isInCalling = false;
        super.onBackPressed();
    }
	
	public void didAddFragment(Fragment frag,String var,String[] list,boolean isCheck)
	{
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			Bundle data = new Bundle();
			data.putStringArray(var,list);
			frag.setArguments(data);
			ft.replace(R.id.activity_main_content_fragment, frag , "NewFragmentTag"); 
			if(isCheck)
			ft.addToBackStack(null);
			ft.commitAllowingStateLoss(); 
    }
	
	public void didHideMenu()
	{
	  if(isMenu)
        {
	    	getSupportFragmentManager().beginTransaction().setCustomAnimations(0, R.anim.abc_fade_out).remove(getSupportFragmentManager().findFragmentByTag("menu")).disallowAddToBackStack().commitAllowingStateLoss();
	    	isMenu = false;
        	return;
        }
	}

	@Override
	public void didPressLogOut() {
		/* changeable GPS setting by Ying */
		Taxi_System.changeGPS(getApplicationContext(), this);
		/**********************************/
		didHideMenu();
		button.setVisibility(View.GONE);
		FragmentManager manager = getSupportFragmentManager();
		if (manager.getBackStackEntryCount() > 0) 
		{
			FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
			manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		Socket.socketDidDisconnect();    
		Taxi_System.addSystem(context, "token", "");
		didAddFragment(new Customer_Login(), "code", new String[] {}, true);
	}

	@Override
	public void didPressProfile() {
		didHideMenu();
		didAddFragment(new Customer_Profile(), "code", new String[] {}, true);
		isInMenu = true;
	}
	
	@Override
	public void didPressStatistic() {
		didHideMenu();
		didAddFragment(new Customer_Profile(), "code", new String[] {}, true);
		isInMenu = true;
	}
	
	@Override
	public void didPressCompany() {
		didHideMenu();
		didAddFragment(new Customer_Company(), "code", new String[] {}, true);
		isInMenu = true;
	}

	@Override
	public void didPressHistory() {
		didHideMenu();
		didAddFragment(new Customer_History(), "code", new String[] {}, true);
		isInMenu = true;
	}

	@Override
	public void didPressShowMap() {
		didHideMenu();
		didAddFragment(new Customer_Map(), "code", new String[] {}, true);
		isInMenu = true;
	}

}
