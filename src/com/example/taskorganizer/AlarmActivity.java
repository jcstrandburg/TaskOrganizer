package com.example.taskorganizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.taskorganizer.Model.Alert;


public class AlarmActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_alarm);
		Intent intent = getIntent();
		int alertID = intent.getIntExtra( "AlertID", -1);
		
		if ( alertID >= 0) {
			
			Alert alert = Model.alerts.get( alertID);
			if ( alert != null) {
				
				TextView tv = (TextView)findViewById( R.id.AlarmLabel);
				tv.setText( String.format( "Alarm %d for task %s", alert.id, alert.task.name));
				
				Model.deleteAlert( alert);				
			}
			else {
				Log.e( "AlarmActivity", "Could not load alert");
				finish();
			}
		}
		else {
			
			Log.e( "AlarmActivity", "No alert id provided");
			finish();
		}
	}
}
