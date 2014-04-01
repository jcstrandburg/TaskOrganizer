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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;


/**
 * @author Justin Strandburg
 *
 */
public class TaskViewActivity extends ActionBarActivity implements OnItemClickListener, DataModelListener {

	View header;
	View footer;
	
	Button dateButton, timeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("TaskViewActivity", "onCreate");
		
		setContentView(R.layout.task_view);
		formatView();
	}	
	
	void formatView() {
		
		//create the header
		ListView lv = (ListView)findViewById( R.id.AlertList);		
		header = getLayoutInflater().inflate( R.layout.task_view_header, null);
		footer = getLayoutInflater().inflate( R.layout.task_view_footer, null);
		
		dateButton = (Button)header.findViewById(R.id.DateTimeButtonWrapper).findViewById(R.id.DateButton);
		timeButton = (Button)header.findViewById(R.id.DateTimeButtonWrapper).findViewById(R.id.TimeButton);
		lv.addHeaderView( header);		
		lv.addFooterView( footer);
		
		//temporary 
		List<String> l = new ArrayList<String>();
		l.add( "Alert A");
		l.add( "Alert B");
		l.add( "Alert C");	
		l.add( "Alert D");
		l.add( "Alert E");
		l.add( "Alert F");	
		l.add( "Alert G");
		ArrayAdapter<String> ad = new ArrayAdapter<String>( this, R.layout.text_list_fragment, l);
		lv.setAdapter( ad);	
		lv.setOnItemClickListener( this);
	}
	
	public void DoSaveTask( View view) {
		Log.e("WIP", "Save task not yet implemented");
	}
	
	public void DoDeleteTask( View view) {
		Log.e("WIP", "Delete task not yet implemented");
	}
	
	public void DoPickDate( View view) {

		DatePickerDialog dpick;
		
		dpick = new DatePickerDialog( this, new OnDateSetListener() {
			public void onDateSet( DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
				
				dateButton.setText( String.format( "%02d/%02d/%04d", selectedmonth+1, selectedday, selectedyear));
			}
		}, 2014, 03, 28);
		dpick.setTitle( "Hey Now");
		dpick.show();		
	}
	
	public void DoPickTime( View view) {

		TimePickerDialog dpick;
		
		dpick = new TimePickerDialog( this, new OnTimeSetListener() {
			public void onTimeSet( TimePicker timepicker, int selectedHour, int selectedMinutes) {
				
				timeButton.setText( String.format( "%02d:%02d", selectedHour, selectedMinutes));
			}
		}, 7, 45, true);
		dpick.setTitle( "Hey Now");
		dpick.show();	
	}

	//FOr when user clicks on alert list
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

		Log.d("Alert Clicked", String.format( "%d", position));
		Intent intent = new Intent( this, AlertViewActivity.class);
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
		// TODO Auto-generated method stub
		
	}
			
}
