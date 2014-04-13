package us.strandburg.taskorganizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

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
import android.widget.Toast;

/*
 * To do: * 
 * Prettify the interface
 * Figure out a better way to handle authentication
 *
 */

/*
 * A pure static class for managing the data model 
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
	private static final String authenticationURL = baseURL + "auth.php";
	
	private static final Semaphore dataLock = new Semaphore( 1);//used for locking data model from concurrent access
	final static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	private static Context context = null;
	private static ArrayList<DataModelListener> listeners = new ArrayList<DataModelListener>();	
	
	public static SparseArray<Task> tasks = new SparseArray<Task>();
	public static SparseArray<Alert> alerts = new SparseArray<Alert>();
	public static Boolean authenticated = false;
	public static Boolean credentialsChanged = false;
	
	/**
	 * Interface for objects requiring notification from the data model on updates
	 */
	public static interface DataModelListener {

		//called when the data model is updated
		public void onDataModelUpdated();
		
	}
	
	/**
	 * POD class for easy manipulation of task times
	 * @author User
	 *
	 */
	public static class DateTime {
		int month;
		int year;
		int day;
		int hours;
		int minutes;
	}
	
	/**
	 * Utility class for manipulating tasks in the database
	 */
	public static class Task {
		
		int id;
		String name;
		String desc;
		String whenString;
		Date when;
		ArrayList<Alert> alerts;
		int generation;
		
		public Task( JSONObject obj) throws JSONException, ParseException {
			
			id = obj.getInt( "TaskID");
			name = obj.getString( "TaskName");
			desc = obj.getString( "TaskDesc");
			whenString = obj.getString( "TaskTime");
			when = dateFormat.parse( whenString);	
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
	
	/**
	 * Utility class for manipulating alerts in the database
	 */
	public static class Alert {
		Task task;//the task to which this alert applies
		int id;//id in database
		int offset;//offset used for determining alert time (in seconds)
		
		public Alert( Task t, JSONObject obj) throws JSONException {
			
			id = obj.getInt( "AlertID");
			offset = obj.getInt( "AlertOffset");
			task = t;
		}
	}
	
	/**
	 * Wrapper class that manages a JSONObject response from the database server
	 */
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
				return "**internal DataResults error**";
			}			
		}
		
		public String getErrorMessage() {
			try {
				return myObj.getString( "error-message");
			} catch (JSONException e) {
				
				Log.e( "JSON Error", e.getMessage());
				return "**internal DataResults error**";
			}
		}		
	}
	
	/**
	 * Helper interface for database tasks 
	 */
	public static interface DataResultHandler {

		public void handleResults( DataResults obj);
	}

	
	/**
	 * Set the static context of the data model so it has a source from which to get the application context
	 */
	static public void SetContext( Context c) {
		context = c;
	}
	
	/**
	 * Acquires a lock on the data model preventing updates from happening while certain
	 * interface activities are active 
	 */
	static public void acquireDataLock() {

		dataLock.acquireUninterruptibly();
		Log.d("Model",  "Lock acquired");
	}
	
	/**
	 * Releases the lock on the data model
	 */
	static public void releaseDataLock() {
		dataLock.release();
		Log.d("Model", "Lock released");
	}
	
	/**
	 * Finallizes a data model update with the given DataResults
	 * @param res The data results to be applied
	 */
	static public void processDataResults( DataResults res) {

		try {
			
			if ( res.isSuccess()) {

				authenticated = true;
				
				//clear out old data
				clearAlarms();
				tasks.clear();
				alerts.clear();
				
				//insert new data
				JSONArray results = res.getResultsAsArray();
				for ( int i = 0; i < results.length(); i++) {
					JSONObject taskObj = results.getJSONObject( i);
					Task task = new Task( taskObj);
					
					tasks.put( task.id, task);					
					
					JSONArray alertsArray = taskObj.getJSONArray( "Alerts");
					for ( int j = 0; j < alertsArray.length(); j++) {
						JSONObject alertObj = alertsArray.getJSONObject( j);
						Alert alert = new Alert( task, alertObj);
						
						task.alerts.add( alert);
						alerts.put( alert.id, alert);
					}					
				}
				setAlarms();
			}
			else {
				
				authenticated = false;
				Log.e( "Model",  "Data update failed: " + res.getErrorMessage());
			}			
		}
		catch ( Exception e) {
			Log.e( "Model", "Data update failed: "+e.getClass().toString()+", "+e.getMessage());
		}
		finally {
		}
	}
	
	/**
	 * Clears the alarm for all alerts currently in the model
	 */
	static private void clearAlarms() {
	
		for ( int i = 0; i < alerts.size(); ++i) {
			
			Alert alert = alerts.valueAt( i);
			clearAlarm( alert);
		}
	}
	
	/**
	 * Clears the scheduled alarm for the given alert
	 */
	static private void clearAlarm( Alert alert) {
		AlarmManager am = (AlarmManager)getAppContext().getSystemService( Context.ALARM_SERVICE);
		Intent intent = new Intent( getAppContext(), AlarmActivity.class);
		PendingIntent pi = PendingIntent.getBroadcast( getAppContext(), alert.id, intent, Intent.FLAG_ACTIVITY_NEW_TASK);			
		am.cancel( pi);
	}

	/**
	 * Sets/resets alarms for all of the alerts currently in the model
	 */
	static private void setAlarms() {
		
		for ( int i = 0; i < alerts.size(); ++i) {
			
			Alert alert = alerts.valueAt( i);
			setAlarm( alert);
		}
	}
	
	/**
	 * Set/reset the alarm for the given alert
	 */
	static private void setAlarm( Alert alert) {
		
		AlarmManager am = (AlarmManager)getAppContext().getSystemService( Context.ALARM_SERVICE);
		Intent intent = new Intent( getAppContext(), AlarmReceiver.class);
		intent.putExtra( "AlertID", alert.id);
		PendingIntent pi = PendingIntent.getBroadcast( getAppContext(), alert.id, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		Long time = alert.task.when.getTime() - alert.offset*60000;
		Long nowTime =Calendar.getInstance().getTimeInMillis();
		
		if ( time > nowTime) {
			am.set( AlarmManager.RTC_WAKEUP, time, pi);
		}		
	}

	/**
	 * Gets the application context from the context provided by the last call to setContext
	 * @return
	 */
	static private Context getAppContext() {
		return context.getApplicationContext();
	}
	
	/**
	 * Creates an httpPost object and populates it's post variables with those supplied in postVars, as well
	 * as authentication information
	 * @param URL The URL the HttpPost is sent to
	 * @param postVars The source of the post variables
	 * @return The HttpPost
	 */
	static private HttpPost createHttpPost( String URL, List<NameValuePair> postVars) {

		try {
			HttpPost h = new HttpPost( URL);			
			if ( postVars == null )
				postVars = new ArrayList<NameValuePair>();
			
			//pull the authentication information from the preferences and add it to the post variables
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences( getAppContext());					
			String username = sharedPrefs.getString( "user_name", "username");
			String userpass = sharedPrefs.getString( "user_pass", "userpass");
			postVars.add( new BasicNameValuePair( "UserName", username));
			postVars.add( new BasicNameValuePair( "UserPass", userpass));
			
			//insert the post variables into the HttpPost and return it
			h.setEntity( new UrlEncodedFormEntity(postVars));			
			return h;
		}
		catch ( Exception e) {
			Log.e( "CreateHttpPost", e.getMessage());
			return null;
		}
	}
	
	/**
	 * Creates a DatabaseTask and gets it running, the data model is then updated in the background
	 */
	static public void startDataUpdate() {

		Log.d( "Model.startDataUpdate", "begin");
		HttpPost httpPost = createHttpPost( dataUpdateURL, null);
		DatabaseTask dbTask = new DatabaseTask( httpPost, true);
		dbTask.execute( new DataResultHandler() {
							@Override
							public void handleResults( DataResults res) {
								processDataResults( res);
							}
						});
	}
	
	/**
	 * Handles scheduling the next data update and notifying the interface elements that the data has changed
	 */
	static public void postDataUpdate() {
		
		//get the database polling interval
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences( context);
		int pollingInterval = Integer.parseInt( sharedPrefs.getString( "refresh_interval", "15"))*60000;
		
		//schedule the next data update
		Intent intent = new Intent( context.getApplicationContext(), DatabaseUpdater.class);
		Long nowTime = Calendar.getInstance().getTimeInMillis();
		AlarmManager am = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast( context.getApplicationContext(), 1, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		am.set( AlarmManager.RTC_WAKEUP, nowTime+pollingInterval, pi);
		
		//notify the interface elements that the data has changed
		notifyListeners();		
	}
	
	
	/**
	 * Adds a DataModelListenter to the list of listeners (if it's not already there)
	 */
	static public void addListener( DataModelListener x) {
		if ( !listeners.contains( x)) {
			listeners.add( x);
		}
	}
	
	/**
	 * Removes a DataModelListenter from the list of listeners 
	 */	
	static public void removeListener( DataModelListener y) {
		listeners.remove( y);
	}
	
	/**
	 * Notifies all listeners that the data model has been updated
	 */
	static public void notifyListeners() {
		
		Log.d("Model.notifyListeners", String.format( "Notifying %d listeners", listeners.size()));
		for ( int i = 0; i < listeners.size(); ++i) {
			listeners.get(i).onDataModelUpdated();
		}
	}
	
	/**
	 * Requests from the database server that a new task be created, 
	 * then received the new task and inserts it into the data model.
	 */
	static public void addTask() {
		
		HttpPost httpPost = createHttpPost( addTaskURL, null);
		DatabaseTask dbTask = new DatabaseTask( httpPost, false);
		dbTask.execute( new DataResultHandler() {
							@Override
							public void handleResults( DataResults res) {
								
								try {
									if ( res.isSuccess()) {
										
										JSONObject taskObj = res.getResultsAsObject();
										Task task = new Task( taskObj);
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
	
	/**
	 * Sends the details of the given task to the database server to perform an update query.
	 * Also reschedules the alarms for all alerts, since the task time may have changed
	 */
	static public void updateTask( Task task) {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair(   "TaskID", String.valueOf( task.id)));
		postData.add( new BasicNameValuePair( "TaskName", String.valueOf( task.name)));
		postData.add( new BasicNameValuePair( "TaskDesc", String.valueOf( task.desc)));
		postData.add( new BasicNameValuePair( "TaskTime", dateFormat.format( task.when)));
		
		HttpPost httpPost = createHttpPost( updateTaskURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost, false);
		dbTask.execute( new DataResultHandler() {

							@Override
							public void handleResults( DataResults res) {
								
								try {
									if ( res.isSuccess()) {

										JSONObject taskObj = res.getResultsAsObject();
										int taskID = taskObj.getInt( "TaskID");
										Task task = tasks.get( taskID);
										for ( int i = 0; i < task.alerts.size(); ++i) {
											Alert alert = task.alerts.get( i);
											setAlarm( alert);											
										}
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
	
	/**
	 * Sends a request to the database server that the given task be deleted, then
	 * upon receiving confirmation removes the task from the data model
	 */
	static public void deleteTask( Task task) {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair( "TaskID", String.valueOf( task.id)));		
		HttpPost httpPost = createHttpPost( deleteTaskURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost, false);
		dbTask.execute( new DataResultHandler() {

							@Override
							public void handleResults( DataResults res) {
								
								try {
									if ( res.isSuccess()) {
										
										//find the task and remove it from the task list
										JSONObject obj = res.getResultsAsObject();
										int taskID = obj.getInt( "TaskID");
										Task task = tasks.get( taskID);
										tasks.remove( taskID);
										
										//find all alerts associated with the given task then remove them and cancel their alarms
										for ( int i = 0; i < task.alerts.size(); ++i) {
											
											Alert alert = task.alerts.get( i);
											alerts.remove( alert.id);
											clearAlarm( alert);
										}
										
										notifyListeners();
									}
									else {
										Log.e( "Model.deleteTask", "Failure: "+res.getErrorCode()+", "+res.getErrorMessage());
									}
								}
								catch ( Exception e) {
									Log.e( "Model.deleteTask exception", e.getMessage());
								}
							}
						});
	}
	
	/**
	 * Requests from the database server that a new alert be created (attached to the given task), 
	 * then received the new alert and inserts it into the data model.
	 */
	static public void addAlert( Task task) {
		
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair( "TaskID", String.valueOf( task.id)));			
		HttpPost httpPost = createHttpPost( addAlertURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost, false);
		dbTask.execute( new DataResultHandler() {

							@Override
							public void handleResults( DataResults res) {
								
								
								try {
									if ( res.isSuccess()) {
										
										JSONObject alertObj = res.getResultsAsObject();
										int taskID = alertObj.getInt( "TaskID");
										Task task = tasks.get( taskID);
										Alert alert = new Alert( task, alertObj);
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
	
	/**
	 * Sends the details of the given task to the database server to perform an update query,
	 * on success reschedules the alarm for the given alert
	 */	
	static public void updateAlert( Alert alert) {
		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair(   "AlertID", String.valueOf( alert.id)));
		postData.add( new BasicNameValuePair( "AlertOffset", String.valueOf( alert.offset)));
		HttpPost httpPost = createHttpPost( updateAlertURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost, false);
		dbTask.execute( new DataResultHandler() {

							@Override
							public void handleResults( DataResults res) {
								
								try {
									if ( res != null ) {
										if ( res.isSuccess()) {
											
											JSONObject taskObj = res.getResultsAsObject();
											int alertID = taskObj.getInt( "AlertID");
											Alert alert = alerts.get( alertID);
											setAlarm( alert);
											notifyListeners();
										}
										else {
											Log.e( "Model.updateAlert", "Failure: "+res.getErrorCode()+", "+res.getErrorMessage());
										}
									}
									else {
										Log.e( "Model.updateAlert", "res is null for some strange reason");
									}
								}
								catch ( Exception e) {
									Log.e( "Model.updateAlert", e.getMessage());
								}
							}
						});		
	}
	
	/**
	 * Sends a request to the database server that the given alert be deleted, then
	 * upon receiving confirmation removes the alert from the data model and cancels
	 * the alarm associated with it.
	 */
	static public void deleteAlert( Alert alert) {

		List<NameValuePair> postData = new ArrayList<NameValuePair>();
		postData.add( new BasicNameValuePair( "AlertID", String.valueOf( alert.id)));		
		HttpPost httpPost = createHttpPost( deleteAlertURL, postData);
		DatabaseTask dbTask = new DatabaseTask( httpPost, false);
		dbTask.execute( new DataResultHandler() {

							@Override
							public void handleResults( DataResults res) {
								
								try {
									if ( res.isSuccess()) {
										
										JSONObject obj = res.getResultsAsObject();
										int alertID = obj.getInt( "AlertID");
										Alert alert = alerts.get( alertID);
										alert.task.alerts.remove( alert);
										clearAlarm( alert);
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
