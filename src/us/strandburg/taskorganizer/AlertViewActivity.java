/**
 * 
 */
package us.strandburg.taskorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * @author Justin Strandburg
 *
 */
public class AlertViewActivity extends ActionBarActivity {

	Model.Alert alert;
	TextView alertLabel;
	EditText offsetText;
	Spinner offsetInterval;
	ArrayAdapter<CharSequence> adapter;
	Boolean alertDeleted = false;
	
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
	
	
	/**
	 * Create the action bar options
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.alert_view_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Handle clicks on the action bar options
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch ( id) {
			case R.id.action_delete_alert:
				alertDeleted = true;
				Model.deleteAlert( alert);
				finish();
				return true;
			case R.id.action_settings:
				Intent intent = new Intent( this, SettingsActivity.class);
				startActivity( intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}		

	/**
	 * Convert the alert offset information from the interface into minutes, then update the data model
	 */
	public void doSaveAlert() {

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
	}
	
	public void doForceAlarm( View view) {


		Intent intent = new Intent( this, AlarmActivity.class);
		intent.putExtra( "AlertID",  alert.id);
		startActivity( intent);
	}
	
	void formatView() {

		alertLabel = (TextView)findViewById( R.id.AlertTaskName);
		offsetText = (EditText)findViewById( R.id.OffsetAmount);
		offsetInterval = (Spinner)findViewById( R.id.OffsetInterval);
		
		//Create a key listener for the enter key and assign it to alert offset EditText
		OnKeyListener okl = new OnKeyListener() {
							    public boolean onKey(View v, int keyCode, KeyEvent event) {
							        if (keyCode == 66) {
							            InputMethodManager manager = (InputMethodManager) v.getContext()
							    	            .getSystemService(INPUT_METHOD_SERVICE);
							    	    if (manager != null)
							    	        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
							            return true; //this is required to stop sending key event to parent
							        }
							        return false;
							    }
							};
		offsetText.setOnKeyListener( okl);
		
		adapter = ArrayAdapter.createFromResource( this, R.array.interval_arrays, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		offsetInterval.setAdapter( adapter);
	}	
	
	/**
	 * Uses information from the data model to populate interface items
	 */
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
		
		alertLabel.setText( String.format( "Alert: %s", alert.task.name));
		offsetText.setText( String.format( "%d", offset));
		offsetInterval.setSelection( interval);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Model.acquireDataLock();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if ( !alertDeleted)
			doSaveAlert();
		Model.releaseDataLock();
	}
}
