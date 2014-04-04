package com.example.taskorganizer;

import android.content.Context;

public class AppContext extends android.app.Application {
	
	private static AppContext instance= null;
	
	public AppContext() {
		getContext();
	}
	
	public static Context getContext() {
		
		if ( instance == null) {
			instance = new AppContext();
		}
		return instance;
	}

}
