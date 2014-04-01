/**
 * 
 */
package com.example.taskorganizer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * @author Justin Strandburg
 *
 */
public class AlertViewActivity extends Activity implements DataModelListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("AlertView", "onCreate");
		
		setContentView(R.layout.alert_view);
		formatView();
	}	
	
	void formatView() {

		//temporary 
		ArrayAdapter<CharSequence> adapter 
			= ArrayAdapter.createFromResource( this, R.array.interval_arrays, android.R.layout.simple_spinner_item);
		Spinner spinner = (Spinner)findViewById( R.id.OffsetInterval);		
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter( adapter);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d("AlertView", "onStart");
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d("AlertView", "onRestart");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Model.lockData();
		Log.d("AlertView", "onResume");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Model.unlockData();
		Log.d("AlertView", "onPause");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("AlertView", "onDestroy");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d("AlertView", "onStop");
	}

	@Override
	public void onDataModelUpdated() {
		// TODO Auto-generated method stub
		
	}
	
	
}
