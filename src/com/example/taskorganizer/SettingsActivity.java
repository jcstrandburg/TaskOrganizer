package com.example.taskorganizer;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate( Bundle savedInstanceState) {
		super.onCreate( savedInstanceState);
		addPreferencesFromResource( R.xml.settings);
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu) {
		menu.add( Menu.NONE, 0, 0, "Show current settings");
		return super.onCreateOptionsMenu( menu);
	}
	
	@Override
	public boolean onOptionsItemSelected( MenuItem item) {
		/*switch ( item.getItemID()) {
			case 0:
				startActivity( new Intent( this, ShowSet))
		}*/
		return false;
	}	
	

}
