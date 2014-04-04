/**
 * 
 */
package com.example.taskorganizer;

import java.util.ArrayList;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;


/**
 * @author Justin Strandburg
 *
 */
public class TaskViewActivity extends ActionBarActivity implements OnItemClickListener, DataModelListener {

	List<String> listText = new ArrayList<String>();
	ArrayAdapter<String> lvAdapter;
	int taskID;
	Model.Task task;
	Model.DateTime taskTime;
	
	View header;
	View footer;
	EditText taskName, taskDesc;
	
	Button dateButton, timeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("TaskViewActivity", "onCreate");

		Intent intent = getIntent();
		taskID = intent.getIntExtra( "TaskID", -1);
		if ( taskID >= 0 && (task = Model.tasks.get( taskID)) != null ) {
			
			Log.d("TaskView", String.format( "Loading task view for task id %d", task.id));
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
		footer = getLayoutInflater().inflate( R.layout.task_view_footer, null);
		lv.addHeaderView( header);		
		lv.addFooterView( footer);		
		dateButton = (Button)header.findViewById(R.id.DateTimeButtonWrapper).findViewById(R.id.DateButton);
		timeButton = (Button)header.findViewById(R.id.DateTimeButtonWrapper).findViewById(R.id.TimeButton);
		taskName = (EditText)header.findViewById(R.id.TaskName);
		taskDesc = (EditText)header.findViewById(R.id.TaskDescription);
		lvAdapter = new ArrayAdapter<String>( this, R.layout.text_list_fragment, listText);		
		
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
	
	
	public void DoSaveTask( View view) {
		
		task.SetWhen( taskTime);		
		task.name = taskName.getText().toString();
		task.desc = taskDesc.getText().toString();
		Model.updateTask( task);
		
		finish();
	}
	
	public void DoDeleteTask( View view) {
		
		Model.deleteTask( task);
		finish();		
	}
	
	public void DoPickDate( View view) {

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
	
	public void DoPickTime( View view) {

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
	
	public void DoAddAlert( View view) {
		
		Model.addAlert( task);		
	}

	//FOr when user clicks on alert list
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

		position--;
		Model.Alert alert = task.alerts.get( position);		
		Log.d("Alert Clicked", String.format( "%d %d", position, alert.id));
		Intent intent = new Intent( this, AlertViewActivity.class);
		intent.putExtra( "AlertID", alert.id);
		
		startActivity( intent);		
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d("TaskView", "onStart");
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d("TaskView", "onRestart");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Model.lockData();
		Log.d("TaskView", "onResume");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Model.unlockData();
		Log.d("TaskView", "onPause");
	}
	
	@Override
	public void onDestroy() {
		Model.removeListener( this);
		super.onDestroy();
		Log.d("TaskView", "onDestroy");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d("TaskView", "onStop");
	}

	@Override
	public void onDataModelUpdated() {

		Log.d("TaskView", "onDataModelUpdated");
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
			Log.d("TaskView", String.format( "Unable to load task %d", taskID));
		}
	}
			
}
