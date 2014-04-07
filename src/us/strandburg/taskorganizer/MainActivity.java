package us.strandburg.taskorganizer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

class TaskListAdapter extends BaseAdapter {
	
	Context myContext;
	final static SimpleDateFormat dateFormat = new SimpleDateFormat( "MM/dd/yyyy HH:mm", Locale.ENGLISH);
	
	public TaskListAdapter( Context context) {
		super();
		myContext = context;
	}
	
	@Override
	public int getCount() {
		return Model.tasks.size();
	}
	
	
	@Override 
	public long getItemId( int position) {
		return Model.tasks.valueAt( position).id;
	}
	
	@Override
	public Model.Task getItem( int position) {
		return Model.tasks.valueAt( position);
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		
		if ( row == null ) {
			LayoutInflater inflater = ((Activity)myContext).getLayoutInflater();
			row = inflater.inflate( R.layout.task_list_item, parent, false);
			
		}
		else {
			
		}

		TextView taskName = (TextView)row.findViewById( R.id.TaskListItemName);
		TextView taskTime = (TextView)row.findViewById( R.id.TaskListItemTime);
		TextView taskAlerts = (TextView)row.findViewById( R.id.TaskListItemNumAlerts);
		
		Model.Task task = Model.tasks.valueAt( position);
		taskName.setText( task.name);		
		taskTime.setText( dateFormat.format( task.when));
		taskAlerts.setText( String.format( "%d alerts", task.alerts.size()));
		return row;
	}
}

public class MainActivity extends ActionBarActivity implements OnItemClickListener, DataModelListener {

	List<String> listText = new ArrayList<String>();
	BaseAdapter lvAdapter;
	Boolean dataLoaded = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_main);
		Model.addListener( this);
		Model.SetContext( this);
		
		Log.d( "What", "Why");
		
		listText.add( "Loading...");
		//lvAdapter = new ArrayAdapter<String>( this, R.layout.text_list_fragment, listText);
		lvAdapter = new TaskListAdapter( this);
		ListView lv = (ListView)findViewById( R.id.TaskList);
		lv.setAdapter( lvAdapter);		
		lv.setOnItemClickListener( this);
		Model.startDataUpdate();		
	}
	
	@Override
	protected void onDestroy() {
		Model.removeListener( this);
		super.onDestroy();
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
		Log.d( "MainActivity", "options item selected");
		int id = item.getItemId();
		switch ( id) {
			case R.id.action_new_task:
				doNewTask( null);
				return true;
			case R.id.action_refresh:
				Model.startDataUpdate();
				return true;
			case R.id.action_settings:
				doPreferences( null);
				return true;
		}
		return super.onOptionsItemSelected(item);
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
