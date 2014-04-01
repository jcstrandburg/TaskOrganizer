package com.example.taskorganizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.util.SparseArray;

public class Model {

	
	public static class Task {
		
		String name;
		String desc;
		String whenString;
		//DateTime when;
		ArrayList<Alert> alerts;
		int generation;
		
		public Task( JSONObject obj, int gen) throws JSONException {
			
			name = obj.getString( "TaskName");
			desc = obj.getString( "TaskDesc");
			whenString = obj.getString( "TaskTime");
			generation = gen;
			alerts = new ArrayList<Alert>();
		}
	}
	
	public static class Alert {
		Task task;//the task to which this alert applies
		int id;//id in database
		int offset;//offset used for determining alert time (in seconds)
		
		public Alert( Task t, JSONObject obj, int gen) throws JSONException {
			
			id = obj.getInt( "AlertID");
			offset = obj.getInt( "AlertOffset");
			task = t;
			generation = gen;
		}
	}
	
	public static SparseArray<Task> tasks = new SparseArray<Task>();
	public static SparseArray<Alert> alerts = new SparseArray<Alert>();
	private static int generation = 0;
	public static int lock = 0;
	
	static public void lockData() {
		lock++;
		Log.d("Model", String.format( "Locking Data (%d)", lock));
	}
	
	static public void unlockData() {
		lock--;
		Log.d("Model", String.format( "Unlocking Data (%d)", lock));
	}
	
	static private void doDataUpdate() {

		generation++;
		try {
			
			JSONObject obj = getJSONData();
			if ( isSuccess(obj)) {
				Log.d( "Model", "Got data");
				
				JSONArray results = getResults( obj);
				for ( int i = 0; i < results.length(); i++) {
					JSONObject taskObj = results.getJSONObject( i);
					Task task = new Task( taskObj, generation);
					
					String s = String.format( "(%s): (%s) @(%s)", task.name, task.desc, task.whenString);
					Log.d( "Model", s);
					
					JSONArray alertsArray = taskObj.getJSONArray( "Alerts");
					for ( int j = 0; j < alertsArray.length(); j++) {
						JSONObject alertObj = alertsArray.getJSONObject( j);
						Alert alert = new Alert( task, alertObj, generation);
						
						s = String.format( "   Alert %d %d", alert.id, alert.offset);
						Log.d( "Model", s);
					}					
				}
				
			}
			else {
				Log.e( "Model",  "Data aquisition failed");
			}
			
		}
		catch ( Exception e) {
			Log.d( "Model", "Data update failed: "+e.getClass().toString()+", "+e.getMessage());
		}
	}
	
	static public void forceDataUpdate() {
		Log.d("Model", "Locking before forcing data update");
		Log.d("Model", "Forcing Data Update");
		
		doDataUpdate();
		
		Log.d("Model", "Unlocking after forcing data update");
	}
	
	static public JSONObject getJSONData() {
		
		DefaultHttpClient httpclient = new DefaultHttpClient( new BasicHttpParams());
		HttpPost httppost = new HttpPost( "http://www.strandburg.us/taskorganizer/droid/gettasks.php");
		//httppost.setHeader( "Content-type", "application/json");
		InputStream inputStream = null;
		String result = null;
		
		try {
			HttpResponse response = httpclient.execute( httppost);
			HttpEntity entity = response.getEntity();
			
			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			while ((line = reader.readLine()) != null ) {
				sb.append( line + "\n");
			}
			result = sb.toString();
			Log.d("JSON", result);
			
			
			JSONObject jobj = new JSONObject( result);
			
			return jobj;
		}
		catch  ( JSONException e ) {
			Log.e( "JSON exception", e.getMessage());
			
			
			return null;
		}
		catch ( Exception e) {
			Log.e( "Other exception", e.getMessage());
			String s = e.getClass().toString();
			Log.e( "exception", s);
			
			return null;
		}
	}
	
	static public Boolean isSuccess( JSONObject obj) {
		
		try {
			Boolean success = obj.getBoolean( "success");
			return success;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	static public JSONArray getResults( JSONObject obj) {
		
		try {
			JSONArray array = obj.getJSONArray( "results");
			return array;
		} catch (JSONException e) {
			
			Log.e( "JSON Error", e.getMessage());
			return null;
		}
	}
	
	static public String getErrorMessage( JSONObject obj) {
		try {
			String error = obj.getString( "error");
			return error;
		} catch (JSONException e) {
			
			Log.e( "JSON Error", e.getMessage());
			return null;
		}
	}
	
	static public void addTask() {
		
	}
	
	static public void updateTask( Task task) {
		
	}
	
	static public void deleteTask( Task task) {
		
	}
	
	static public void addAlert( Task task) {
		
	}
	
	static public void updateAlert( Alert alert) {
		
	}
	
	static public void deleteAlert( Alert alert) {
		
	}

}
