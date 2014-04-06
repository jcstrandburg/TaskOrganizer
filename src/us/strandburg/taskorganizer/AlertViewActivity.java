/**
 * 
 */
package us.strandburg.taskorganizer;

import com.example.taskorganizer.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author Justin Strandburg
 *
 */
public class AlertViewActivity extends Activity {

	Model.Alert alert;
	TextView alertLabel;
	EditText offsetText;
	Spinner offsetInterval;
	ArrayAdapter<CharSequence> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		int alertID = intent.getIntExtra( "AlertID", -1);
		alert = Model.alerts.get( alertID);
		
		setContentView(R.layout.alert_view);
		formatView();
		populateInterface();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.alert_view_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch ( id) {
			case R.id.action_delete_alert:
				doDeleteAlert( null);
				return true;
			case R.id.action_settings:
				doPreferences( null);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}		

	public void doSaveAlert( View view) {

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
		finish();
	}	
	
	public void doDeleteAlert( View view) {
		Model.deleteAlert( alert);
		finish();
	}
	
	void formatView() {

		alertLabel = (TextView)findViewById( R.id.AlertTaskName);
		offsetText = (EditText)findViewById( R.id.OffsetAmount);
		offsetInterval = (Spinner)findViewById( R.id.OffsetInterval);
		
		adapter = ArrayAdapter.createFromResource( this, R.array.interval_arrays, android.R.layout.simple_spinner_item);
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
	public void onResume() {
		super.onResume();
		try {
			Model.acquireDataLock();
		} catch (InterruptedException e) {
			Log.e( "AlertViewActivity", "Interupted data lock");
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		doSaveAlert( null);
		Model.releaseDataLock();
	}
	
	public void doPreferences( View view) {
		
		Intent intent = new Intent( this, SettingsActivity.class);
		startActivity( intent);
	}		
}
