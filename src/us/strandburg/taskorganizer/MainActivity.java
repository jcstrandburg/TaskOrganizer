package us.strandburg.taskorganizer;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.example.taskorganizer.R;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return super.onCreateOptionsMenu(menu);
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
	
	public void doTesting( View view) {

		Long time = new GregorianCalendar().getTimeInMillis();
		time += 1000;
		
		Intent intentAlarm = new Intent( this.getApplicationContext(), AlarmReceiver.class);
		AlarmManager alarmManager = (AlarmManager)getSystemService( Context.ALARM_SERVICE);
		
		alarmManager.set( AlarmManager.RTC_WAKEUP, time-1, 
				PendingIntent.getBroadcast( this,  1,  intentAlarm,  PendingIntent.FLAG_UPDATE_CURRENT));
	}
	
	public void doPreferences( View view) {
		
		Intent intent = new Intent( this, SettingsActivity.class);
		startActivity( intent);
	}
	
	public void doNewTask( View view) {
		Model.addTask();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch ( id) {
			case R.id.action_new_task:
				doNewTask( null);
				return true;
			case R.id.action_refresh:
				Model.scheduleUpdate( 1);
				return true;
			case R.id.action_settings:
				doPreferences( null);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		TextView userName = (TextView)findViewById( R.id.UserNameText);
		TextView userPass = (TextView)findViewById( R.id.UserPassText);
		TextView intervalThing = (TextView)findViewById( R.id.IntervalThing);
		userName.setText( sharedPrefs.getString( "user_name", "username"));
		userPass.setText( sharedPrefs.getString( "user_pass", "userpass"));
		intervalThing.setText( sharedPrefs.getString( "refresh_interval", "15"));
		
		//Model.doBlockingUpdate();
		Model.scheduleUpdate( 1);
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
