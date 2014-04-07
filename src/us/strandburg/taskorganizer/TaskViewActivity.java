/**
 * 
 */
package us.strandburg.taskorganizer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

class AlertListAdapter extends BaseAdapter {
	
	Context myContext;
	Model.Task task;
	
	public AlertListAdapter( Context context, Model.Task t) {
		super();
		myContext = context;
		task = t;
	}
	
	@Override
	public int getCount() {
		
		if ( task != null ) {
			return task.alerts.size();
		}
		else {
			return 0;
		}
	}
	
	
	@Override 
	public long getItemId( int position) {
		
		if ( task != null ) {
			return task.alerts.get( position).id;
		}
		else {
			return -1;
		}
	}
	
	@Override
	public Model.Alert getItem( int position) {
		
		if ( task != null ) {
			return task.alerts.get( position);
		}
		else {
			return null;
		}
	}
	
	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		
		if ( row == null ) {
			LayoutInflater inflater = ((Activity)myContext).getLayoutInflater();
			row = inflater.inflate( R.layout.alert_list_item, parent, false);
			
		}
		else {
			
		}

		TextView alertLabel = (TextView)row.findViewById( R.id.AlertListItemLabel);
		
		Model.Alert alert = task.alerts.get( position);
		
		int offsetAmount;
		String offsetInterval;
		
		if ( alert.offset % 1440 == 0 ) {
			offsetAmount = alert.offset/1440;
			if ( offsetAmount == 1 ) {
				offsetInterval = "day";
			}
			else {
				offsetInterval = "days";
			}
		}
		else if ( alert.offset % 60 == 0 ) {
			offsetAmount = alert.offset/60;
			if ( offsetAmount == 1 ) {
				offsetInterval = "hour";
			}
			else {
				offsetInterval = "hours";
			}
		}
		else {
			offsetAmount = alert.offset;
			if ( offsetAmount == 1 ) {
				offsetInterval = "minute";
			}
			else {
				offsetInterval = "minutes";
			}
		}
		
		alertLabel.setText( String.format( "Alert %d %s before", offsetAmount, offsetInterval));		
		return row;
	}
}


/**
 * @author Justin Strandburg
 *
 */
public class TaskViewActivity extends ActionBarActivity implements OnItemClickListener, DataModelListener {

	List<String> listText = new ArrayList<String>();
	BaseAdapter lvAdapter;
	int taskID;
	Model.Task task;
	Model.DateTime taskTime;
	
	View header;
	EditText taskName, taskDesc;
	Button dateButton, timeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		taskID = intent.getIntExtra( "TaskID", -1);
		if ( taskID >= 0 && (task = Model.tasks.get( taskID)) != null ) {
			
			taskTime = task.getDateTime();
			setContentView(R.layout.task_view);
			formatView();
			populateInterface();
			Model.addListener( this);
		}
		else {
			
			Log.e("TaskView", "No task id provided or could not load task");
			finish();
		}
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
	    inflater.inflate(R.menu.task_view_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}	
	
	private void hideKeyboard(View view) {
	    InputMethodManager manager = (InputMethodManager) view.getContext()
	            .getSystemService(INPUT_METHOD_SERVICE);
	    if (manager != null)
	        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	void formatView() {
		
		//get handles on important interface elements, create header and footer
		ListView lv = (ListView)findViewById( R.id.AlertList);
		header = getLayoutInflater().inflate( R.layout.task_view_header, null);
		lv.addHeaderView( header);		
		dateButton = (Button)header.findViewById(R.id.DateTimeButtonWrapper).findViewById(R.id.DateButton);
		timeButton = (Button)header.findViewById(R.id.DateTimeButtonWrapper).findViewById(R.id.TimeButton);
		taskName = (EditText)header.findViewById(R.id.TaskName);
		taskDesc = (EditText)header.findViewById(R.id.TaskDescription);
		//lvAdapter = new ArrayAdapter<String>( this, R.layout.text_list_fragment, listText);
		lvAdapter = new AlertListAdapter( this, task);
				
		OnKeyListener okl = new OnKeyListener() {
							    public boolean onKey(View v, int keyCode, KeyEvent event) {
							        if (keyCode == 66) {
							            hideKeyboard(v);
							            return true; //this is required to stop sending key event to parent
							        }
							        return false;
							    }
							};
		taskName.setOnKeyListener( okl);
		taskDesc.setOnKeyListener( okl);
		
		//create the list adapter and set it up and junk
		lv.setAdapter( lvAdapter);	
		lv.setOnItemClickListener( this);		

		//force load the correct information from the data model
		onDataModelUpdated();		
	}
	
	public void populateInterface() {
		
		taskName.setText( task.name);
		taskDesc.setText( task.desc);
		updateDateTimeButtons();
	}
	
	public void updateDateTimeButtons() {
		dateButton.setText( String.format( "%02d/%02d/%04d", taskTime.month+1, taskTime.day, taskTime.year));
		timeButton.setText( String.format( "%02d:%02d", taskTime.hours, taskTime.minutes));		
	}
	
	
	public void doSaveTask() {
		
		task.SetWhen( taskTime);		
		task.name = taskName.getText().toString();
		task.desc = taskDesc.getText().toString();
		Model.updateTask( task);
	}
	
	public void doDeleteTask() {
		
		Model.deleteTask( task);
		finish();		
	}
	
	public void doPickDate( View view) {

		DatePickerDialog dpick;
		
		dpick = new DatePickerDialog( this, new OnDateSetListener() {
			public void onDateSet( DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
				
				taskTime.month = selectedmonth;
				taskTime.day = selectedday;
				taskTime.year = selectedyear;
				updateDateTimeButtons();
			}
		}, taskTime.year, taskTime.month, taskTime.day);
		dpick.setTitle( "Hey Now");
		dpick.show();		
	}
	
	public void doPickTime( View view) {

		TimePickerDialog dpick;
		
		dpick = new TimePickerDialog( this, new OnTimeSetListener() {
			public void onTimeSet( TimePicker timepicker, int selectedHour, int selectedMinutes) {
				
				taskTime.hours = selectedHour;
				taskTime.minutes = selectedMinutes;
				updateDateTimeButtons();
			}
		}, taskTime.hours, taskTime.minutes, true);
		dpick.setTitle( "Hey Now");
		dpick.show();	
	}
	
	public void doAddAlert() {
		
		Model.addAlert( task);		
	}

	//FOr when user clicks on alert list
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

		Log.d( "TaskViewActivity.onItemClick", String.format( "Clicked on item %d (id %d)", position, id));
		position--;
		Model.Alert alert = task.alerts.get( position);		
		Intent intent = new Intent( this, AlertViewActivity.class);
		intent.putExtra( "AlertID", alert.id);
		startActivity( intent);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch ( id) {
			case R.id.action_new_alert:
				doAddAlert();
				return true;
			case R.id.action_delete_task:
				Log.d( "What", "who");
				doDeleteTask();
				return true;
			case R.id.action_settings:
				doPreferences();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	@Override
	public void onResume() {
		super.onResume();
		Model.acquireDataLock();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		doSaveTask();
		Model.releaseDataLock();
		Log.d("TaskView", "onPause");
	}

	@Override
	public void onDataModelUpdated() {

		//Log.d("TaskView", "onDataModelUpdated");
		task = Model.tasks.get( taskID);
		
		if ( task != null ) {
			
			listText.clear();
			for ( int i = 0; i < task.alerts.size(); i++) {
				Model.Alert alert = task.alerts.get( i);
				String s = String.format( "Alert %d (%d)", alert.id, alert.offset);			
				listText.add( s);
			}		
			lvAdapter.notifyDataSetChanged();
			populateInterface();
		}
		else {
			//Log.d("TaskView", String.format( "Unable to load task %d", taskID));
		}
	}
	
	public void doPreferences() {
		
		Intent intent = new Intent( this, SettingsActivity.class);
		startActivity( intent);
	}	
			
}
