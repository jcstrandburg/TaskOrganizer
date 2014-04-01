package com.example.taskorganizer;

import java.util.ArrayList;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements OnItemClickListener, DataModelListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("MainActivity", "onCreate");
		
		setContentView(R.layout.fragment_main);
		formatView();
	}
	
	//user clicks on an item in the task list
	public void onItemClick( AdapterView<?> l, View v, int position, long id) {
		
		Log.i( "Clicked", String.format( "You clicked item %d", position));
		Intent intent = new Intent( this, TaskViewActivity.class);
		startActivity( intent);		
	}
	
	public void DoPreferences( View view) {
		
		DatePickerDialog dpick;
		
		dpick = new DatePickerDialog( this, new OnDateSetListener() {
			public void onDateSet( DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
				Log.d( "datepick", "she wants the d");
				Button b = (Button)findViewById( R.id.PreferencesButton);
				b.setText( String.format( "%02d/%02d/%04d", selectedmonth, selectedday, selectedyear));
			}
		}, 2014, 03, 28);
		dpick.setTitle( "Hey Now");
		dpick.show();
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

	private void formatView() {

		//temporary 
		List<String> l = new ArrayList<String>();
		l.add( "Item 1");
		l.add( "Potato");
		l.add( "Blue");
		ArrayAdapter<String> ad = new ArrayAdapter<String>( this, R.layout.text_list_fragment, l);
		ListView lv = (ListView)findViewById( R.id.TaskList);
		lv.setAdapter( ad);		
		lv.setOnItemClickListener( this);
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d("MainActivity", "onStart");
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d("MainActivity", "onRestart");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d("MainActivity", String.format( "onResume, %2d", Model.lock));
		Model.forceDataUpdate();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d("MainActivity", "onPause");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("MainActivity", "onDestroy");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d("MainActivity", "onStop");
	}

	@Override
	public void onDataModelUpdated() {
		// TODO Auto-generated method stub
		
	}
		
	
}
