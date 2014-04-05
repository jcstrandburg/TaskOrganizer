/**
 * 
 */
package com.example.taskorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Justin Strandburg
 *
 */
public class AlertViewActivity extends Activity implements DataModelListener {

	Model.Alert alert;
	TextView alertLabel;
	EditText offsetText;
	Spinner offsetInterval;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Log.d("AlertView", "onCreate");
		
		Intent intent = getIntent();
		int alertID = intent.getIntExtra( "AlertID", -1);
		//Log.d("AlertView",  String.format( "Loading alert %d", alertID));
		alert = Model.alerts.get( alertID);
		//Log.d("AlertView",  String.format( "%d - %d", alert.id, alert.offset));
		
		setContentView(R.layout.alert_view);
		formatView();
		populateInterface();
	}	

	public void DoSaveAlert( View view) {

		int offsetMult = 1;
		
		switch ( offsetInterval.getSelectedItemPosition()) {
		case 0:
			offsetMult = 1;
			break;
		case 1:
			offsetMult = 60;
			break;
		case 2:
			offsetMult = 1440;
			break;
		}
		
		int offsetBase = Integer.parseInt( offsetText.getText().toString());
		alert.offset = offsetBase*offsetMult;
		Model.updateAlert( alert);
		
		//Log.d("DoSaveAlert", String.format("%d, %d, %d", offsetMult, offsetBase, alert.offset));
		finish();
	}	
	
	public void DoDeleteAlert( View view) {
		Model.deleteAlert( alert);
		finish();
	}
	
	void formatView() {

		alertLabel = (TextView)findViewById( R.id.AlertTaskName);
		offsetText = (EditText)findViewById( R.id.OffsetAmount);
		offsetInterval = (Spinner)findViewById( R.id.OffsetInterval);
		
		ArrayAdapter<CharSequence> adapter 
		= ArrayAdapter.createFromResource( this, R.array.interval_arrays, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		offsetInterval.setAdapter( adapter);
	}
	
	void populateInterface() {
		
		int offset, interval;
		
		if ( alert.offset % 1440 == 0 ) {
			interval = 2;
			offset = alert.offset / 1440;
		}
		else if ( alert.offset % 60 == 0 ) {
			interval = 1;
			offset = alert.offset / 60;
		}
		else {
			interval =  0;
			offset = alert.offset;
		}
		
		alertLabel.setText( String.format( "Alert: %s", alert.id, alert.task.name));
		offsetText.setText( String.format( "%d", offset));
		offsetInterval.setSelection( interval);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//Log.d("AlertView", "onStart");
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		//Log.d("AlertView", "onRestart");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Model.lockData();
		//Log.d("AlertView", "onResume");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Model.unlockData();
		//Log.d("AlertView", "onPause");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.d("AlertView", "onDestroy");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		//Log.d("AlertView", "onStop");
	}

	@Override
	public void onDataModelUpdated() {
		// TODO Auto-generated method stub
		
	}
	
	
}
