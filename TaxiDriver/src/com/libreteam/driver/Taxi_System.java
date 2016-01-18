package com.libreteam.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.libreteam.driver.R;
import com.libreteam.driver.extra.HttpConnection;
import com.libreteam.driver.extra.MyApplication;
import com.libreteam.driver.extra.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils.TruncateAt;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class Taxi_System {
	private static SharedPreferences preferences;
	public static void printHashKey(Activity v) {

        try {
            PackageInfo info = v.getPackageManager().getPackageInfo("com.libreteam.taxi", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String myhash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                
                Log.d("TEMPTAGHASH KEY:", myhash);
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
	
	@SuppressWarnings("static-access")
	public static Boolean checkGPS(Context context,final Activity activity)
	{
			LocationManager lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
			if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
			      !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 
			{
			  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			  builder.setTitle("Location Services Not Active");
			  builder.setMessage("Please enable Location Services and GPS");
			  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			  {
			  public void onClick(DialogInterface dialogInterface, int i)
			  {
			    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			    activity.startActivity(intent);
			    }
			  });
			  Dialog alertDialog = builder.create();
			  alertDialog.setCanceledOnTouchOutside(false);
			  alertDialog.show();
			  return false;
			}
			else
				return true;
	}

    public static String jsonString(String[] name,String[] value,
    		String[] hashName, HashMap<String,JSONObject> hashMap,
    		String[] hashInfo, HashMap<String,JSONObject> mapInfor
    		)
    {
    	JSONObject json = new JSONObject();
        for(int i = 0; i< name.length; i++)
        {
        	try {
				json.put(name[i], value[i]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        
        JSONObject temp = new JSONObject();
        for(int i = 0; i< hashName.length; i++)
        {
        	try 
        	{
				temp.put(hashName[i],hashMap.get(hashName[i]).getString(hashName[i]));
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
 
        JSONObject temp1 = new JSONObject();
        if(hashInfo != null && mapInfor != null)
        {       
	        for(int i = 0; i< hashInfo.length; i++)
	        {
	        	try 
	        	{
					temp1.put(hashInfo[i],mapInfor.get(hashInfo[i]).getString(hashInfo[i]));
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        }
        }
    	try 
    	{
			json.put("info", temp);
			if(hashInfo != null && mapInfor != null)
				json.put("userinfo", temp1);
		}
    	catch (JSONException e) 
		{
			e.printStackTrace();
		}
    	
        return json.toString();
    }
    
    static float getTextSize(Activity activity)
    {
     return	(16 * activity.getResources().getDisplayMetrics().density);
    }
    
	 static boolean checkPlayServices(Activity activity) 
	 {
		    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MyApplication.getAppContext());
		    if (resultCode != ConnectionResult.SUCCESS)
		    {
		        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
		            GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
		                    9000).show();
		        }
		        else 
		        {
		            Log.i("", "This device is not supported.");
		            activity.finish();
		        }
		        return false;
		    }
		    return true;
		}
    
    public static void addSystem(Context v,String name, String value)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(v);
    	SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name,value); 
        editor.commit();
    }
	
    public static String getSystem(Context v,String name)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(v);
        String r = preferences.getString(name, "");
        return r;
    }
    
	public static  String md5(String s) {
	    try {
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	
	public static ProgressDialog pd;
	public static void showDialog(Context v)
	{
		if(pd == null)
		    pd = new ProgressDialog(v);
			pd.setTitle(v.getString(R.string.loading));
			pd.setMessage(v.getString(R.string.please_wait));
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
			 Timer t = new Timer();
				t.schedule(new TimerTask() {
				    @Override
				    public void run() 
				    {
				    	notShow();
				    }   
				},
				1000 * 45);  
	}
	
	public static void notShow()
	{
		if(pd != null &&pd.isShowing())
		{
		   pd.cancel();
		   pd = null;
		}
	}
	
	public static Boolean connectionStatus(Context v)
	{
	    ConnectivityManager conMgr =(ConnectivityManager)v.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo info = conMgr.getActiveNetworkInfo();

		if(info != null && info.isConnected())
		{ 
			return true;
		} 
		else
		{ 
			return false;
		}
	}
	
	public static void showToast(Context v , String content)
	{
        Toast.makeText(v.getApplicationContext(), content , Toast.LENGTH_LONG).show();
	}
	
	public static void testLog( Object t)
	{
		Log.e("testLog","" + t);
	}
	
	static void setFontFamily(ViewGroup container, Activity activity)
	{
		   for(int i = 0; i< container.getChildCount(); i++)
		   {
			   if(container.getChildAt(i) instanceof TextView)
			   {
				   Typeface font = Typeface.createFromAsset(activity.getAssets(), "pt_sans.ttf");		   
				   ((TextView)container.getChildAt(i)).setTypeface(font);
			   }
		   }
	}
	
	public static void applyFonts(final View v, Typeface fontToSet)
	{
	    try {
	        if (v instanceof ViewGroup) {
	            ViewGroup vg = (ViewGroup) v;
	            for (int i = 0; i < vg.getChildCount(); i++) {
	                View child = vg.getChildAt(i);
	                applyFonts(child, fontToSet);
	            }
	        } else if (v instanceof TextView) {
	            ((TextView)v).setTypeface(fontToSet);
	            //((TextView)v).setSingleLine();
	            ((TextView)v).setEllipsize(TruncateAt.END);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void applyFonts_singleLine(final View v, Typeface fontToSet)
	{
	    try {
	        if (v instanceof ViewGroup) {
	            ViewGroup vg = (ViewGroup) v;
	            for (int i = 0; i < vg.getChildCount(); i++) {
	                View child = vg.getChildAt(i);
	                applyFonts(child, fontToSet);
	            }
	        } else if (v instanceof TextView) {
	            ((TextView)v).setTypeface(fontToSet);
	            ((TextView)v).setSingleLine();
	            ((TextView)v).setEllipsize(TruncateAt.END);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	static Typeface faceType(Activity activity)
	{
		return Typeface.createFromAsset(activity.getAssets(), "pt_sans.ttf");
	}
	
	public static DisplayMetrics checkScreen(Context v)
	{
			DisplayMetrics metrics = new DisplayMetrics();
	        WindowManager windowManager = (WindowManager) 
	        v.getSystemService(Context.WINDOW_SERVICE);
	        windowManager.getDefaultDisplay().getMetrics(metrics);
	        return metrics;
	}
    
	public static int getHeight(Context v)
	{
			DisplayMetrics metrics = new DisplayMetrics();
	        WindowManager windowManager = (WindowManager) 
	        v.getSystemService(Context.WINDOW_SERVICE);
	        windowManager.getDefaultDisplay().getMetrics(metrics);
	        return metrics.heightPixels;
	}
	
	public static int getWidth(Context v)
	{
			DisplayMetrics metrics = new DisplayMetrics();
	        WindowManager windowManager = (WindowManager) 
	        v.getSystemService(Context.WINDOW_SERVICE);
	        windowManager.getDefaultDisplay().getMetrics(metrics);
	        return metrics.widthPixels;
	}
	
	public static void setContent(View v,Context context,float w,float h)
	{
	    	v.getLayoutParams().height = (int) (Taxi_System.getHeight(context) * h);
	    	v.getLayoutParams().width = (int) (Taxi_System.getWidth(context) * w);
	}
	
	public static void setContentLogo(View v,  Context context,float w,float h)
	{

		Drawable draw = context.getResources().getDrawable(R.drawable.logo_taxi_s);
		int width = draw.getIntrinsicWidth();
		int height = draw.getIntrinsicHeight();
		
//			int height = v.getMeasuredHeight();
//			int width = v.getMeasuredWidth();
			int c_height = (int)(Taxi_System.getHeight(context) * h);
			int c_width = (int)(Taxi_System.getWidth(context) * w);
			float scale = (float)width/(float)height;
			int r_height = c_height;
			int r_width = (int)(r_height*scale);
			if(r_width>c_width)
			{
				r_width = c_width;
				r_height = (int)(c_width/scale);
			}
	    	v.getLayoutParams().height = r_height;
	    	v.getLayoutParams().width = r_width;
	}
	
	public static void didAddFragment(Fragment fr,Fragment frag,String var,String[] list,boolean isCheck)
	{
		final FragmentTransaction ft = fr.getFragmentManager().beginTransaction(); 
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		Bundle data = new Bundle();
		data.putStringArray(var,list);
		frag.setArguments(data);
		ft.replace(R.id.activity_main_content_fragment, frag , "NewFragmentTag"); 
		if(isCheck)
			ft.addToBackStack(null);
		ft.commit(); 
    }
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
	
	static String sendRequest(String...strings) 
	{
		String result = null;
		String url = strings[0];
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			InputStream inputStream = httpResponse.getEntity().getContent();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder stringBuilder = new StringBuilder();
			String bufferedStrChunk = null;

			while((bufferedStrChunk = bufferedReader.readLine()) != null){
				stringBuilder.append(bufferedStrChunk);
			}
			result = stringBuilder.toString();
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return result;
	}
	
	public static String sendRequest(String url, String[] nameReq, String[] valueReq){
		HttpClient httpClient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(url);
        BasicNameValuePair basicNameValuePair = null;
        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();           
        for(int i=0; i<nameReq.length;i++){
        	basicNameValuePair = new BasicNameValuePair(nameReq[i], valueReq[i]);
	       	nameValuePairList.add(basicNameValuePair);
        }
        try {
        	UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
            httpPost.setEntity(urlEncodedFormEntity);
           	try {
           		HttpResponse httpResponse = httpClient.execute(httpPost);
           		InputStream inputStream = httpResponse.getEntity().getContent();
           		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
           		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
           		StringBuilder stringBuilder = new StringBuilder();
	            String bufferedStrChunk = null;
	            while((bufferedStrChunk = bufferedReader.readLine()) != null){
	               	stringBuilder.append(bufferedStrChunk);
	            }
	            return stringBuilder.toString();      
           	} catch (ClientProtocolException cpe) {
	           	Log.i("", "cpe--"+cpe.toString());
	             cpe.printStackTrace();
            	} catch (IOException ioe) {
	              	Log.i("", "ioe--"+ioe.toString());
	                ioe.printStackTrace();
            	}
            } catch (UnsupportedEncodingException uee) {
            	uee.printStackTrace();
            }
        return null;
	}
	
	public static String getInfor(String url){
		InputStream inputStream = null;
		String result = "";
		try {
			// create HttpClient
	        HttpClient httpclient = new DefaultHttpClient();
	        // make GET request to the given URL
	        HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
	    	// receive response as inputStream
	        inputStream = httpResponse.getEntity().getContent();
	        // convert inputstream to string
	        if(inputStream != null)
	        	result = convertInputStreamToString(inputStream);
	    	else
	        	result = "Did not work!";
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
	  	}
		return result;
	}
	  
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
	    String result = "";
	    while((line = bufferedReader.readLine()) != null)
	    	result += line;
	 
	    inputStream.close();
	    return result; 
	}

	public static String getRequest(String string) {
		return null;
	}
	
	public static void setUiMap(GoogleMap mMap)
	{
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.setMyLocationEnabled(false);
		mMap.getUiSettings().setMyLocationButtonEnabled(false);
		mMap.getUiSettings().setCompassEnabled(false);	
		mMap.getUiSettings().setRotateGesturesEnabled(false);
	}
	
	public static Timer t;
	static float k = 0;
	public static void didUpdatePosition(final Context context,final Location position)
	{
//		if(t == null)
//		//{
//			t = new Timer();
//			t.scheduleAtFixedRate(new TimerTask() 
//			{
//			    @Override
//			    public void run() {
//			        k +=  0;//0.01;
			    	HashMap<String, JSONObject> hash = new HashMap<String, JSONObject>();
					try 
					{
						hash.put("token", new JSONObject().put("token", Taxi_System.getSystem(context, "token")));
						hash.put("lat", new JSONObject().put("lat", String.valueOf(position.getLatitude())));
						hash.put("lng", new JSONObject().put("lng", String.valueOf(position.getLongitude() + k)));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				    Socket.socketDidSendMessage(Taxi_System.jsonString(
				    		new String[]{"code","type"},
				    		new String[]{"11",Taxi_System.getSystem(context, "type")},
				    		new String[]{"token","lat","lng"},
				    		hash,
				    		null,null
				    		));
//		        }
//			}, 0, 10000);
		//}
	}
	
	public static void didEndUpdatePosition()
	{
		if(t !=  null)
		{
			t.cancel();
			t = null;
		}
	}
	
	public static void zoomToLocation(double latitude, double longitude, GoogleMap map) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(latitude, longitude)).zoom((float) 16)
				.build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	public static void zoomToLocationRide(double latitude, double longitude, GoogleMap map) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(latitude, longitude)).zoom((float) 16)
				.build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	public static void zoomToFitLatLongs(List<LatLng> latlng,GoogleMap map,int width,int height) {
		LatLngBounds.Builder b = new LatLngBounds.Builder();
		for (LatLng l : latlng) {
			if(l.latitude!=0.0 && l.longitude!=0.0)
				b.include(l);
		}
		LatLngBounds bounds = b.build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,width,height,120));
	}
	
	
	public static String getMapsApiDirectionsUrl(List<LatLng> latlng) {
//		String waypoints = "waypoints=optimize:true|"
//					+ latlng.get(1).latitude + "," + latlng.get(1).longitude
//					+ "|" + "|" + latlng.get(0).latitude + ","
//					+ latlng.get(0).longitude ;
		String waypoints = "origin="
				+ latlng.get(1).latitude + "," + latlng.get(1).longitude
				+ "&destination=" + latlng.get(0).latitude + ","
				+ latlng.get(0).longitude ;

		String sensor = "sensor=false";
		String params = waypoints + "&" + sensor;
		String output = "json";
		String url = "https://maps.googleapis.com/maps/api/directions/"	+ output + "?" + params;
		return url;
	}
	 
	public static String downloadTask(String url) throws InterruptedException, ExecutionException
	{
		class ReadTask extends AsyncTask<String, Void, String> {
			@Override
			protected String doInBackground(String... url) {
				String data = "";
				try 
				{
					HttpConnection http = new HttpConnection();
					data = http.readUrl(url[0]);
				} catch (Exception e) {
					Log.d("Background Task", e.toString());
				}
				return data;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
			//	new ParserTask().execute(result);
			}
		}
		// new ReadTask().execute(url);
		return new ReadTask().execute(url).get();
	}
	
	/**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    private static List<Address> getGeocoderAddress(Context context, double lat, double lng)
    {
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        try {
        	List<Address> addresses = geocoder.getFromLocation(lat, lng, 5);
          	return addresses;
      	} 
    	catch (IOException e) 
    	{
    		Log.e("Error : Geocoder", "Impossible to connect to Geocoder", e);
        }

        return null;
    }
        
    public static String getAddressLine(Context context, double lat, double lng)
    { 
        List<Address> addresses = getGeocoderAddress(context, lat, lng);
        if (addresses != null && addresses.size() > 0)
        {
            Address address = addresses.get(0);
            int n = address.getMaxAddressLineIndex();
            String addressLine = "";
            if(n>1) {
            	for (int i = 0; i < n-1; i++)
            		addressLine += address.getAddressLine(i)+ ", ";
            }
            return addressLine;
        }
        else
            return "";
    }
}
