package us.strandburg.taskorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver that starts a data model update whenever a broadcast is received from the
 * AlarmManager (or on boot)
 */
public class DatabaseUpdater extends BroadcastReceiver {
	public DatabaseUpdater() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		Model.SetContext( context);
		Model.startDataUpdate();
	}
}
