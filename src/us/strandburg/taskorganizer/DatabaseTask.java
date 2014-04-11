package us.strandburg.taskorganizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Helper asynctask that performs database queries in a seperate thread to keep the ui responsive
 */
public class DatabaseTask extends AsyncTask< Model.DataResultHandler, Void, JSONObject> {

	HttpPost httpPost;
	Boolean rLock;
	Model.DataResultHandler handler;
	
	/**
	 * @param hp The httpPost object to be sent
	 * @param requireLock Whether the task acquires a data lock while processing its httppost
	 */
	public DatabaseTask( HttpPost hp, Boolean requireLock) {
		
		httpPost = hp;
		rLock = requireLock;
	}
	
	@Override
	protected JSONObject doInBackground( Model.DataResultHandler... params) {
		
		DefaultHttpClient httpclient = new DefaultHttpClient( new BasicHttpParams());
		InputStream inputStream = null;
		String result = null;		
		handler = params[0];
		
		Log.d("DatabaseTask.doInBackground",  "Starting update");
		
		if ( rLock)
			Model.acquireDataLock();
		
		try {
			
			//send off the httppost object and get the result as a String
			HttpResponse response = httpclient.execute( httpPost);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null ) {
				sb.append( line + "\n");
			}
			result = sb.toString();
			Log.d("HTTPResponse", "Result: **"+result+"**");			
			
			//create a json object from the results and send it on to postExecute in the main thread
			JSONObject jobj = new JSONObject( result);			
			return jobj;
		}
		catch  ( JSONException e ) {
			
			Log.e( "DatabaseTask JSON exception", e.getMessage());
			return null;
		}
		catch ( Exception e) {
			
			Log.e( "DatabaseTask Other exception", e.getMessage());
			String s = e.getClass().toString();			
			return null;
		}
		finally {
			
			if ( rLock)
				Model.releaseDataLock();
		}
	}
	
	/**
	 * Handle the model update back int he main thread
	 */
	protected void onPostExecute( JSONObject result) {

		handler.handleResults( new Model.DataResults(result));
		Model.postDataUpdate();
	}	
}
