package us.strandburg.taskorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class DatabaseUpdater extends BroadcastReceiver {
	public DatabaseUpdater() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		//do the update
		Log.d( "DatebaseUpdater", "Running a model data update");
		Model.SetContext( context);

		//DataUpdateService.startModelUpdate( context);
		try {
			Model.acquireDataLock();
			Model.doBlockingUpdate();
			Model.releaseDataLock();
			
			//Intent intent2=new Intent(getApplicationContext(),InterfaceUpdateReceiver.class);
			//sendBroadcast(intent2);
		}
		catch (InterruptedException e) {
			Log.e( "DataBaseUpdater", "Interrupted data lock");
		}			
		
		//schedule the next update
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences( context);
		int pollingInterval = Integer.parseInt( sharedPrefs.getString( "refresh_interval", "15"))*60000;
		Model.scheduleUpdate( pollingInterval);
	}
}
