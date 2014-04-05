package com.example.taskorganizer;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
		Model.lockData();
		Model.doBlockingUpdate();
		Model.unlockData();
		
		//schedule the next update
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences( context);
		int pollingInterval = Integer.parseInt( sharedPrefs.getString( "refresh_interval", "15"))*60000;
		Model.scheduleUpdate( pollingInterval);
	}
}
