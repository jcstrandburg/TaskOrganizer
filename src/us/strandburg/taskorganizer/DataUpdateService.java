package us.strandburg.taskorganizer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class DataUpdateService extends IntentService {

	/**
	 * Starts this service to perform action a Model update
	 * @see IntentService
	 */
	// TODO: Customize helper method
	public static void startModelUpdate(Context context) {

		Intent intent = new Intent(context, DataUpdateService.class);
		context.startService(intent);
	}

	public DataUpdateService() {
		super("MyIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		try {
			Model.acquireDataLock();
			Model.doBlockingUpdate();
			Model.releaseDataLock();
			
			Intent intent2=new Intent(getApplicationContext(),InterfaceUpdateReceiver.class);
			sendBroadcast(intent2);
		}
		catch (InterruptedException e) {
			Log.e( "DataBaseUpdater", "Interrupted data lock");
		}		
	}

}
