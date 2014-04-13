package us.strandburg.taskorganizer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

/**
 * A PreferencesActivity
 */
public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate( Bundle savedInstanceState) {
		super.onCreate( savedInstanceState);
		addPreferencesFromResource( R.xml.settings);
		
		Log.d( "wat", "who");
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu) {
		menu.add( Menu.NONE, 0, 0, "Show current settings");
		return super.onCreateOptionsMenu( menu);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Model.credentialsChanged = true;
	}
}
