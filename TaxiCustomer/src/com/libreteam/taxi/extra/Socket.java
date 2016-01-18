package com.libreteam.taxi.extra;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;
import java.net.MalformedURLException;
import org.json.JSONObject;

import com.libreteam.taxi.Taxi_System;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

public class Socket{
	private static SocketIO socket;
	public static SocketRespond respond;
	private static boolean isDisconnected=false;
	
	public interface SocketRespond
	{
		public void respondData(String string);
	}
	
	public static SocketIO getConnection() {
	    if( socket == null) { 
	        // initialize connection object here
	    	try {
				socket = new SocketIO(Customer_Constants.SOCKET_IP);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
	    }
	    return socket;
	}
	
	public static void socketDidSendMessage(String message)
	{
		if(socket!=null)
			socket.send(message);
	}
	
	
	public static void socketDidDisconnect()
	{
		if(socket != null)
			if(socket.isConnected())
				socket.disconnect();
		socket = null;
	}
	
	public static boolean isConnected() {
		if(socket!=null)
			if(socket.isConnected())
				return true;
		return false;
	}
	
	public static boolean isDisconnected() {
		return isDisconnected;
	}
	
	public static SocketIO socketIO() {
		return socket;
	}
	
	public static void socketReconnect()
	{
		if(socket!=null && !socket.isConnected())
			socket.reconnect();
	}
	
	public static void socketDidConnect(final Context context){		
		if(Taxi_System.connectionStatus(context)){
			try {				
				if(socket == null)
				{
					isDisconnected = true;
					socket = new SocketIO(Customer_Constants.SOCKET_IP);					
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
		                    isDisconnected = true;
		                    Log.e("Server-Disconnect", "Server Disconnected");
		                    socket.reconnect();
		                }
		
		                @Override
		                public void onConnect() {
		                    System.out.println("Connection established");
		                    Log.e("Server-Connect", "Server Connected");
		                    isDisconnected = false;
		                }
		
		                @Override
		                public void on(String event, IOAcknowledge ack, Object... args) {
		                    System.out.println("Server triggered event '" + event + "'");
		                }
		            });
				}
				/* For reconnecting socket by Ying */
				else	{					
					if (!socket.isConnected())	{						
						socket = null;
						isDisconnected = true;
						socket = new SocketIO(Customer_Constants.SOCKET_IP);
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
			                    isDisconnected = true;
			                    Log.e("Server-Disconnect", "Server Disconnected");
			                    socket.reconnect();
			                }
			
			                @Override
			                public void onConnect() {
			                    System.out.println("Connection established");
			                    Log.e("Server-Connect", "Server Connected");
			                    isDisconnected = false;
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

//	public static void socketDidConnect(){
//		if(socket!=null && !isConnected()) {
//			socket.connect(new IOCallback() {
//		        	@Override
//		          	public void onMessage(JSONObject json, IOAcknowledge ack) {
//		            	respond.respondData(json.toString());
//		           	}
//		                
//		        	@Override
//		        	public void onMessage(String data, IOAcknowledge ack) {
//		            	respond.respondData(data);
//		         	}
//		
//		         	@Override
//		         	public void onError(SocketIOException socketIOException) {
//		         		socketIOException.printStackTrace();
//		          	}
//		
//		         	@Override
//		         	public void onDisconnect() {
//		         		isDisconnected = true;
//		         		Log.e("Server-Disconnect", "Server Disconnected");
//		         	}
//		
//		        	@Override
//		        	public void onConnect() {
//		        		isDisconnected = false;
//		        		Log.e("Server-Connect", "Server Connected");
//		        	}   
//		
//		        	@Override
//		         	public void on(String event, IOAcknowledge ack, Object... args) {
//		        		System.out.println("Server triggered event '" + event + "'");
//		        	}
//			});
//		}
//	}
}
