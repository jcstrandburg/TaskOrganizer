package us.strandburg.taskorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InterfaceUpdateReceiver extends BroadcastReceiver {
	public InterfaceUpdateReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d( "InterfaceUpdateReceiver", "onReceive, notifying listeners");
		Model.notifyListeners();
	}
}
