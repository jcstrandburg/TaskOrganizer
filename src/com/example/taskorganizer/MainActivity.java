package com.example.taskorganizer;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnItemClickListener, DataModelListener {

	List<String> listText = new ArrayList<String>();
	ArrayAdapter<String> lvAdapter;
	Boolean dataLoaded = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("MainActivity", "onCreate");
		
		setContentView(R.layout.fragment_main);
		Model.addListener( this);
		Model.SetContext( this);
		
		listText.add( "Loading...");
		lvAdapter = new ArrayAdapter<String>( this, R.layout.text_list_fragment, listText);
		ListView lv = (ListView)findViewById( R.id.TaskList);
		lv.setAdapter( lvAdapter);		
		lv.setOnItemClickListener( this);
		Model.scheduleUpdate( 1);
	}
	
	//user clicks on an item in the task list
	public void onItemClick( AdapterView<?> l, View v, int position, long id) {
		
		if ( dataLoaded) {
			Model.Task task = Model.tasks.valueAt( position);
			Log.i( "Clicked", String.format( "You clicked item %d (%s)", position, task.name));
			Intent intent = new Intent( this, TaskViewActivity.class);
			intent.putExtra( "TaskID", task.id);
					
			startActivity( intent);
		}
	}
	
	public void DoTesting( View view) {

		Long time = new GregorianCalendar().getTimeInMillis();
		time += 1000;
		
		Intent intentAlarm = new Intent( this.getApplicationContext(), AlarmReceiver.class);
		AlarmManager alarmManager = (AlarmManager)getSystemService( Context.ALARM_SERVICE);
		
		alarmManager.set( AlarmManager.RTC_WAKEUP, time-1, 
				PendingIntent.getBroadcast( this,  1,  intentAlarm,  PendingIntent.FLAG_UPDATE_CURRENT));
	}
	
	public void DoPreferences( View view) {
		
		Intent intent = new Intent( this, SettingsActivity.class);
		startActivity( intent);
	}
	
	public void DoNewTask( View view) {
		Model.addTask();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//Log.d("MainActivity", "onStart");
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		//Log.d("MainActivity", "onRestart");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//Log.d("MainActivity", String.format( "onResume, %2d", Model.lock));
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		TextView userName = (TextView)findViewById( R.id.UserNameText);
		TextView userPass = (TextView)findViewById( R.id.UserPassText);
		TextView intervalThing = (TextView)findViewById( R.id.IntervalThing);
		userName.setText( sharedPrefs.getString( "user_name", "username"));
		userPass.setText( sharedPrefs.getString( "user_pass", "userpass"));
		intervalThing.setText( sharedPrefs.getString( "refresh_interval", "15"));
		
		//Model.scheduleUpdate( 1);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//Log.d("MainActivity", "onPause");
	}
	
	@Override
	public void onDestroy() {
		Model.removeListener( this);
		super.onDestroy();
		//Log.d("MainActivity", "onDestroy");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		//Log.d("MainActivity", "onStop");
	}

	@Override
	public void onDataModelUpdated() {
		
		dataLoaded = true;
		listText.clear();
		for ( int i = 0; i < Model.tasks.size(); i++) {
			listText.add( Model.tasks.valueAt(i).name);
		}		
		lvAdapter.notifyDataSetChanged();
	}		
	
}
