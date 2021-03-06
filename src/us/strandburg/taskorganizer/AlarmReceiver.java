package us.strandburg.taskorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast receiver that starts an AlarmActivity whenever it is signaled by the AlarmManager
 */
public class AlarmReceiver extends BroadcastReceiver {
	public AlarmReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent i) {
		
		Intent intent = new Intent( context.getApplicationContext(), AlarmActivity.class);
		int alertID = i.getIntExtra( "AlertID", -1);
		
		if ( alertID >= 0 ) {
		
			intent.putExtra( "AlertID",  alertID);
			intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity( intent);
		}
		else {
			Log.e( "AlarmReceiver", "Received alarm with no alert id");
		}
	}
}
