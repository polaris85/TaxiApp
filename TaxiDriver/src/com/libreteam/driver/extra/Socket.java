package com.libreteam.driver.extra;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.Timer;

import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import com.libreteam.driver.Taxi_System;

public  class Socket{
	public static SocketIO socket;
	public static SocketRespond respond;
	public static boolean isConnected;
	private static Timer t;
	/* For keeping connection by Ying */
	public static WifiManager.WifiLock wifiLock;
	public static PowerManager.WakeLock wakeLock;
	/**********************************/
   
	public interface SocketRespond
	{
		public void respondData(String string);
	}
	
	public static void socketDidSendMessage(String message)
	{
//	    if(isConnected)
//		{
//		if(!Socket.isConnected())
//			Socket.socketReconnect();
		if(socket!=null)
			socket.send(message);
//		}
//	    else
//	    {
//	    	socketDidConnect(MyApplication.getAppContext());
//	    }
	}
	
	
	public static void socketDidDisconnect()
	{
		if(socket != null)
		{
			socket.disconnect();
			socket = null;
			isConnected = false;
			/* For releasing by Ying */
			wifiLock.release();
			wakeLock.release();
			/**********************************/
		}
	}
	
	public static boolean isConnected() {
		if(socket!=null)
			if(socket.isConnected())
				return true;
		return false;
	}
	
	public static void socketReconnect()
	{
		socket.reconnect();
	}
	
	public static void socketDidConnect(final Context context){		
		if(Taxi_System.connectionStatus(context)){
			try {				
				if(socket == null)
				{
					isConnected = false;
					socket = new SocketIO(Taxi_Constants.SOCKET_IP);					
		            socket.connect(new IOCallback() {
		                @Override
		                public void onMessage(JSONObject json, IOAcknowledge ack) {
		                     respond.respondData(json.toString());
		                }
		                
		                @Override
		                public void onMessage(String data, IOAcknowledge ack) {
		                     respond.respondData(data);
		                }
		
		                @Override
		                public void onError(SocketIOException socketIOException) {
		                    socketIOException.printStackTrace();
//		                    if(t == null)
//		                    t = new Timer();
//		            		t.scheduleAtFixedRate(new TimerTask() 
//		            		{
//		            		    @Override
//		            		    public void run() {
//		            		    	socketDidConnect(context);
//		            	        } 
//		            		},
//		            		0,
//		            		5000);
		                }
		
		                @Override
		                public void onDisconnect() {
		                    System.out.println("Connection terminated.");
		                    if(t != null) {t.cancel(); t =null;}
		                    isConnected = false;
		                    Log.e("Server-Disconnect", "Server Disconnected");
		                    socket.reconnect();
		                }
		
		                @Override
		                public void onConnect() {
		                    System.out.println("Connection established");
		                    Log.e("Server-Connect", "Server Connected");
		                    if(t != null) {t.cancel(); t =null;}
		                    isConnected = true;
		                }
		
		                @Override
		                public void on(String event, IOAcknowledge ack, Object... args) {
		                    System.out.println("Server triggered event '" + event + "'");
		                }
		            });
		        	/* For keeping connection by Ying */
		            wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "MyWifiLock");
		            wifiLock.acquire();
		            
		            wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
		            wakeLock.acquire();
		        	/**********************************/
				}
				/* For reconnecting socket by Ying */
				else	{					
					if (!socket.isConnected())	{						
						socket = null;
						isConnected = false;
						socket = new SocketIO(Taxi_Constants.SOCKET_IP);
			            socket.connect(new IOCallback() {
			                @Override
			                public void onMessage(JSONObject json, IOAcknowledge ack) {
			                     respond.respondData(json.toString());
			                }
			                
			                @Override
			                public void onMessage(String data, IOAcknowledge ack) {
			                     respond.respondData(data);
			                }
			
			                @Override
			                public void onError(SocketIOException socketIOException) {
			                    socketIOException.printStackTrace();
			                }
			
			                @Override
			                public void onDisconnect() {
			                    System.out.println("Connection terminated.");
			                    if(t != null) {t.cancel(); t =null;}
			                    isConnected = false;
			                    Log.e("Server-Disconnect", "Server Disconnected");
			                    socket.reconnect();
			                }
			
			                @Override
			                public void onConnect() {
			                    System.out.println("Connection established");
			                    Log.e("Server-Connect", "Server Connected");
			                    if(t != null) {t.cancel(); t =null;}
			                    isConnected = true;
			                }
			
			                @Override
			                public void on(String event, IOAcknowledge ack, Object... args) {
			                    System.out.println("Server triggered event '" + event + "'");
			                }
			            });
					}
				}
				/***************************/
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        }
		}
	}

}
