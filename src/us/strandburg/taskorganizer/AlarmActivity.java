package us.strandburg.taskorganizer;

import us.strandburg.taskorganizer.Model.Alert;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;



public class AlarmActivity extends Activity {

	private Alert alert;
	private MediaPlayer mPlayer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.alarm_view);
		Intent intent = getIntent();
		int alertID = intent.getIntExtra( "AlertID", -1);
		mPlayer = new MediaPlayer();
		
		if ( alertID >= 0) {
			
			alert = Model.alerts.get( alertID);
			if ( alert != null) {
				
				TextView tv = (TextView)findViewById( R.id.AlarmLabel);
				tv.setText( String.format( "Alarm for task %s", alert.task.name));

				//start the ring tone
				SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences( this);
				String ringTone = spref.getString( "alarm_tone", "default ringtone");
				Uri uri = Uri.parse( ringTone);
				playSound( uri);	
				
				//start the vibration
				Vibrator v = (Vibrator)this.getSystemService( Context.VIBRATOR_SERVICE);
				v.vibrate( 2000);
			}
			else {
				Log.e( "AlarmActivity", "Could not load alert");
				finish();
			}
		}
		else {
			
			Log.e( "AlarmActivity", "No alert id provided");
			finish();
		}
	}
	
	private void playSound( Uri uri) {
		
		try {
			mPlayer.setDataSource( this, uri);
            final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setVolume( 0.5f, 0.5f);
                mPlayer.prepare();
                mPlayer.start();
            }
		}
		catch (Exception e) {
			Log.e("AlarmActivity.playSound", e.getMessage());
		}
	}
	
	public void doDismissAlarm( View view) {
		Log.d( "doDismissAlarm", "Not implemented");
		Model.deleteAlert( alert);
		mPlayer.stop();
		finish();
	}
	
	public void onPause() {
		super.onPause();
		doDismissAlarm( null);
	}
}
