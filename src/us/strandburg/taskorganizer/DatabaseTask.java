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

import android.os.AsyncTask;
import android.util.Log;

public class DatabaseTask extends AsyncTask< Model.DataResultHandler, Void, JSONObject> {

	HttpPost httpPost;
	Boolean rLock;
	Model.DataResultHandler handler;
	
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
			//Log.d("HTTPResponse", "Result: **"+result+"**");
			
			
			JSONObject jobj = new JSONObject( result);
			
			return jobj;
		}
		catch  ( JSONException e ) {
			Log.e( "JSON exception", e.getMessage());
			
			
			return null;
		}
		catch ( Exception e) {
			Log.e( "Other exception", e.getMessage());
			String s = e.getClass().toString();
			Log.e( "exception", s);
			
			return null;
		}
		finally {
			if ( rLock)
				Model.releaseDataLock();
		}
	}
	
	protected void onPostExecute( JSONObject result) {

		handler.handleResults( new Model.DataResults(result));
		Model.postDataUpdate();
	}	
}
