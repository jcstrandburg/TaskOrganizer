package com.example.taskorganizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

/*
 * To do:
 * 
 * Individually handle alarm updates when alerts are added and deleted
 * Determine when to save task and alert info, wait for save button or just always do it
 * Set up a background task for db queries
 * Handle data protection on the model, not sure how to do this with different activities and junk
 * Prettify the interface
 * Figure out a better way to handle authentication
 * 
 */


public class Model extends android.app.Application {

	private static final String baseURL = "http://www.strandburg.us/taskorganizer/droid/";
	private static final String dataUpdateURL = baseURL + "getdata.php";
	private static final String addTaskURL = baseURL + "addtask.php";
	private static final String addAlertURL = baseURL + "addalert.php";
	private static final String updateTaskURL = baseURL + "updatetask.php";
	private static final String updateAlertURL = baseURL + "updatealert.php";
	private static final String deleteTaskURL = baseURL + "deletetask.php";
	private static final String deleteAlertURL = baseURL + "deletealert.php";	
	
	final static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	private static Context context = null;
	
	public static class DateTime {
		int month;
		int year;
		int day;
		int hours;
		int minutes;
	}
	
	public static class Task {
		
		int id;
		String name;
		String desc;
		String whenString;
		Date when;
		ArrayList<Alert> alerts;
		int generation;
		
		public Task( JSONObject obj, int gen) throws JSONException, ParseException {
			
			id = obj.getInt( "TaskID");
			name = obj.getString( "TaskName");
			desc = obj.getString( "TaskDesc");
			whenString = obj.getString( "TaskTime");
			when = dateFormat.parse( whenString);	
			Log.d( "DateString", when.toString());
			generation = gen;
			alerts = new ArrayList<Alert>();
		}
		
		public DateTime getDateTime() {
			
			DateTime dt = new DateTime();
			Calendar cal = new GregorianCalendar();
			cal.setTime( when);
			
			dt.year = cal.get( Calendar.YEAR);
			dt.month = cal.get( Calendar.MONTH);
			dt.day = cal.get( Calendar.DAY_OF_MONTH);
			dt.hours = cal.get( Calendar.HOUR_OF_DAY);
			dt.minutes = cal.get( Calendar.MINUTE);
			
			return dt;
		}
		
		public void SetWhen( DateTime dt) {
			Calendar cal = new GregorianCalendar();
			cal.set( dt.year, dt.month, dt.day, dt.hours, dt.minutes);
			when = cal.getTime();
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
	
	public static class DataResults {
		
		public JSONObject myObj;
		
		public DataResults( JSONObject jobj) {
			
			myObj = jobj;
		}
		
		public Boolean isSuccess() {
			try {
				Boolean success = myObj.getBoolean( "success");
				return success;
			}
			catch (Exception e) {
				return false;
			}			
		}

		public JSONArray getResultsAsArray() {
			
			try {
				JSONArray array = myObj.getJSONArray( "results");
				return array;
			} catch (JSONException e) {
				
				Log.e( "JSON Error", e.getMessage());
				return null;
			}
		}
		
		public JSONObject getResultsAsObject() {
			
			try {
				JSONObject o = myObj.getJSONObject( "results");
				return o;
			} catch (JSONException e) {
				
				Log.e( "JSON Error", e.getMessage());
				return null;
			}
		}
		
		public String getErrorCode() {
			try {
				return myObj.getString( "error-code");
			} catch (JSONException e) {
				
				Log.e( "JSON Error", e.getMessage());
				return null;
			}			
		}
		
		public String getErrorMessage() {
			try {
				return myObj.getString( "error-message");
			} catch (JSONException e) {
				
				Log.e( "JSON Error", e.getMessage());
				return null;
			}
		}		
	}
	
	public static interface DataHandler {

		public void handleData( DataResults obj);
	}
	
	public static SparseArray<Task> tasks = new SparseArray<Task>();
	public static SparseArray<Alert> alerts = new SparseArray<Alert>();
	private static ArrayList<DataModelListener> listeners = new ArrayList<DataModelListener>();
	private static int generation = 0;
	public static int lock = 0;
	
	static public void SetContext( Context c) {
		context = c;
	}
	
	static public void lockData() {
		lock++;
		Log.d("Model", String.format( "Locking Data (%d)", lock));
	}
	
	static public void unlockData() {
		lock--;
		Log.d("Model", String.format( "Unlocking Data (%d)", lock));
	}
	
	static public void doDataUpdate( DataResults res) {

		generation++;
		try {
			if ( res.isSuccess()) {
				
				clearAlarms();
				tasks.clear();
				alerts.clear();
				JSONArray results = res.getResultsAsArray();
				for ( int i = 0; i < results.length(); i++) {
					JSONObject taskObj = results.getJSONObject( i);
					Task task = new Task( taskObj, generation);
					
					tasks.put( task.id, task);					
					
					JSONArray alertsArray = taskObj.getJSONArray( "Alerts");
					for ( int j = 0; j < alertsArray.length(); j++) {
						JSONObject alertObj = alertsArray.getJSONObject( j);
						Alert alert = new Alert( task, alertObj, generation);
						
						task.alerts.add( alert);
						alerts.put( alert.id, alert);
					}					
				}
				
				setAlarms();
				notifyListeners();				
			}
			else {
				Log.e( "Model",  "Data aquisition failed");
			}
			
		}
		catch ( Exception e) {
			Log.d( "Model", "Data update failed: "+e.getClass().toString()+", "+e.getMessage());
		}
	}
	
	static private void clearAlarms() {
	
		for ( int i = 0; i < alerts.size(); ++i) {
			
			Alert alert = alerts.valueAt( i);
			
			AlarmManager am = (AlarmManager)getAppContext().getSystemService( Context.ALARM_SERVICE);
			Intent intent = new Intent( getAppContext(), AlarmActivity.class);
			PendingIntent pi = PendingIntent.getBroadcast( getAppContext(), alert.id, intent, Intent.FLAG_ACTIVITY_NEW_TASK);			
			am.cancel( pi);
		}
	}
	
	static private void setAlarms() {
		
		Long nowTime = new GregorianCalendar().getTimeInMillis();
		
		for ( int i = 0; i < alerts.size(); ++i) {
			
			Alert alert = alerts.valueAt( i);
			
			AlarmManager am = (AlarmManager)getAppContext().getSystemService( Context.ALARM_SERVICE);
			Intent intent = new Intent( getAppContext(), AlarmReceiver.class);
			intent.putExtra( "AlertID", alert.id);
			PendingIntent pi = PendingIntent.getBroadcast( getAppContext(), alert.id, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

			Long time = alert.task.when.getTime() - alert.offset*60000;

			if ( time > nowTime) {
				am.set( AlarmManager.RTC_WAKEUP, time, pi);
			}
		}
	}
	
	static private void resetAlarm( Alert alert) {
		
		AlarmManager am = (AlarmManager)getAppContext().getSystemService( Context.ALARM_SERVICE);
		Intent intent = new Intent( getAppContext(), AlarmActivity.class);
		PendingIntent pi = PendingIntent.getBroadcast( getAppContext(), alert.id, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		am.cancel( pi);
		Long time = alert.task.when.getTime();
		time -= alert.offset*60000;
		am.set( AlarmManager.RTC_WAKEUP, time, pi);
	}
	
	static private Context getAppContext() {
		return context.getApplicationContext();
	}
	
	static private HttpPost CreateHttpPost( String URL, List<NameValuePair> postVars) {

		try {
			HttpPost h = new HttpPost( URL);
			
			if ( postVars == null )
				postVars = new ArrayList<NameValuePair>();
			
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences( getAppContext());
					
			String username = sharedPrefs.getString( "user_name", "username");
			String userpass = sharedPrefs.getString( "user_pass", "userpass");
			
						
			postVars.add( new BasicNameValuePair( "UserName", username));
			postVars.add( new BasicNameValuePair( "UserPass", userpass));
			h.setEntity( new UrlEncodedFormEntity(postVars));
			
			return h;
		}
		catch ( Exception e) {
			Log.e( "CreateHttpPost", e.getMessage());
			return null;
		}
	}
	
	static public void startDataUpdate() {

		HttpPost httppost = CreateHttpPost( dataUpdateURL, null);
		DatabaseTask dbtask = new DatabaseTask( httppost);
		dbtask.execute( new DataHandler() {
							@Override
							public void handleData( DataResults res) {
								doDataUpdate( res);
							}
						});
	}
	
	static public void addListener( DataModelListener x) {
		if ( !listeners.contains( x)) {
			listeners.add( x);
		}
	}
	
	static public void removeListener( DataModelListener y) {
		listeners.remove( y);
	}
	
	static private void notifyListeners() {
		for ( int i = 0; i < listeners.size(); ++i) {
			listeners.get(i).onDataModelUpdated();
		}
	}
	
	static public void forceDataUpdate() {

		startDataUpdate();
	}
	
	static public void addTask() {
		
		HttpPost httpPost = CreateHttpPost( addTaskURL, null);
		DatabaseTask dbTask = new DatabaseTask( httpPost);
		dbTask.execute( new DataHandler() {
							@Override
							public void handleData( DataResults res) {
								
								try {
									if ( res.isSuccess()) {
										
										JSONObject taskObj = res.getResultsAsObject();
										Task task = new Task( taskObj, generation);
										tasks.put( task.id, task);
										notifyListeners();
									}
									else {
										Log.e( "Model.addTask", "Failure: "+res.getErrorCode()+", "+res.getErrorMessage());
									}
								}
								catch ( Exception e) {
									
								}
							}
						});
	}
	
	static public void updateTask( Task task) {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair(   "TaskID", String.valueOf( task.id)));
		postData.add( new BasicNameValuePair( "TaskName", String.valueOf( task.name)));
		postData.add( new BasicNameValuePair( "TaskDesc", String.valueOf( task.desc)));
		postData.add( new BasicNameValuePair( "TaskTime", dateFormat.format( task.when)));
		
		HttpPost httpPost = CreateHttpPost( updateTaskURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost);
		dbTask.execute( new DataHandler() {

							@Override
							public void handleData( DataResults res) {
								
								try {
									if ( res.isSuccess()) {
										
										notifyListeners();
									}
									else {
										Log.e( "Model.updateTask", "Failure: "+res.getErrorCode()+", "+res.getErrorMessage());
									}
								}
								catch ( Exception e) {
									Log.e( "Model.updateTask", e.getMessage());
								}
							}
						});		
	}
	
	static public void deleteTask( Task task) {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair( "TaskID", String.valueOf( task.id)));		
		HttpPost httpPost = CreateHttpPost( deleteTaskURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost);
		dbTask.execute( new DataHandler() {

							@Override
							public void handleData( DataResults res) {
								
								Log.d("deleteTask", "handleData");
								try {
									if ( res.isSuccess()) {
										
										JSONObject obj = res.getResultsAsObject();
										int taskID = obj.getInt( "TaskID");
										tasks.remove( taskID);
										notifyListeners();
									}
									else {
										Log.e( "Model.deleteTask", "Failure: "+res.getErrorCode()+", "+res.getErrorMessage());
									}
								}
								catch ( Exception e) {
									Log.e( "Model.deleteTask", e.getMessage());
								}
							}
						});
	}
	
	static public void addAlert( Task task) {
		
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair( "TaskID", String.valueOf( task.id)));			
		HttpPost httpPost = CreateHttpPost( addAlertURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost);
		dbTask.execute( new DataHandler() {

							@Override
							public void handleData( DataResults res) {
								
								
								try {
									if ( res.isSuccess()) {
										
										JSONObject taskObj = res.getResultsAsObject();
										int taskID = taskObj.getInt( "TaskID");
										Task task = tasks.get( taskID);
										Alert alert = new Alert( task, taskObj, generation);
										task.alerts.add( alert);
										alerts.put( alert.id, alert);
										notifyListeners();
									}
								}
								catch ( Exception e) {
									
								}
							}
						});
	}
	
	static public void updateAlert( Alert alert) {
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair(   "AlertID", String.valueOf( alert.id)));
		postData.add( new BasicNameValuePair( "AlertOffset", String.valueOf( alert.offset)));
		HttpPost httpPost = CreateHttpPost( updateAlertURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost);
		dbTask.execute( new DataHandler() {

							@Override
							public void handleData( DataResults res) {
								
								try {
									if ( res.isSuccess()) {
										
										notifyListeners();
									}
									else {
										Log.e( "Model.updateAlert", "Failure: "+res.getErrorCode()+", "+res.getErrorMessage());
									}
								}
								catch ( Exception e) {
									Log.e( "Model.deleteTask", e.getMessage());
								}
							}
						});		
	}
	
	static public void deleteAlert( Alert alert) {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair( "AlertID", String.valueOf( alert.id)));		
		HttpPost httpPost = CreateHttpPost( deleteAlertURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost);
		dbTask.execute( new DataHandler() {

							@Override
							public void handleData( DataResults res) {
								
								Log.d("deleteAlert", "handleData");
								try {
									if ( res.isSuccess()) {
										
										JSONObject obj = res.getResultsAsObject();
										int alertID = obj.getInt( "AlertID");
										Alert alert = alerts.get( alertID);
										alert.task.alerts.remove( alert);
										alerts.remove( alertID);
										
										notifyListeners();
									}
									else {
										Log.e( "Model.deleteAlert", "Failure: "+res.getErrorCode()+", "+res.getErrorMessage());
									}
								}
								catch ( Exception e) {
									Log.e( "Model.deleteAlert", e.getMessage());
								}
							}
						});
	}

}
