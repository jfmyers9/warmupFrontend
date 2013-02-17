package cs169.warmup.warmupproject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public final static String EXTRA_USER = "cs169.warmup.warmupproject.USER";
	public final static String EXTRA_COUNT = "cs169.warmup.warmupproject.COUNT";
	
	String urlAdd = "http://desolate-savannah-2939.herokuapp.com/users/add";
	String urlLogin = "http://desolate-savannah-2939.herokuapp.com/users/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void addUser(View view) {
    	String user = ((EditText) findViewById(R.id.username_enter)).getText().toString();
    	String password = ((EditText) findViewById(R.id.password_enter)).getText().toString();
    	JSONObject obj = new JSONObject();
    	try {
    		obj.put("user", user);
    		obj.put("password", password);
    		GetJsonObject getJson = new GetJsonObject(obj);
    		getJson.execute(urlAdd);
    	} catch (Exception e) {
			Context context = getApplicationContext();
			CharSequence text = "Fuck Me";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
    	}
    }
    
    public void loginUser(View view) {
    	String user = ((EditText) findViewById(R.id.username_enter)).getText().toString();
    	String password = ((EditText) findViewById(R.id.password_enter)).getText().toString();
    	JSONObject obj = new JSONObject();
    	try {
    		obj.put("user", user);
    		obj.put("password", password);
    		GetJsonObject getJson = new GetJsonObject(obj);
    		getJson.execute(urlLogin);
    	} catch (Exception e) {
			Context context = getApplicationContext();
			CharSequence text = "Fuck Me";
			int duration = Toast.LENGTH_LONG;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
    	}
    }
    
    private class GetJsonObject extends AsyncTask<String, String, JSONObject> {
    	
    	private JSONObject mjson;
    	
        public GetJsonObject(JSONObject json) {
            mjson = json;
        }

		@Override
		protected JSONObject doInBackground(String... urls) {
		   	HttpClient client = new DefaultHttpClient();
	    	HttpPost post = new HttpPost(urls[0]);
	    	HttpResponse resp;
	    	post.addHeader("Content-type", "application/json");
	    	try {
	    		post.setEntity(new StringEntity(mjson.toString()));
	    		resp = client.execute(post);
	    		HttpEntity ent = resp.getEntity();
	            InputStream instream = ent.getContent();
	            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
	            StringBuilder builder = new StringBuilder();
	            String line = br.readLine();
	            while (line != null) {
	            	builder.append(line + "\n");
	            	line = br.readLine();
	            }
	            instream.close();
	            String result = builder.toString();
	            JSONObject respObj = new JSONObject(result);
	            return respObj;
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return new JSONObject();
	    	}
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			try {
				int errCode = (Integer)result.get("errCode");
				if (errCode == 1) {
					int count = (Integer) result.get("count");
					Context context = getApplicationContext();
					Intent intent = new Intent(context, SuccessfulLoginActivity.class);
					String message = "" + count;
					intent.putExtra(EXTRA_COUNT, message);
					intent.putExtra(EXTRA_USER, (String) mjson.get("user")); 
					startActivity(intent);
				} else if (errCode == -1) {
					Context context = getApplicationContext();
					CharSequence text = "Invalid username and password combination. Please try again.";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				} else if (errCode == -2) {
					Context context = getApplicationContext();
					CharSequence text = "User Already Exists, Try Again.";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				} else if (errCode == -3) {
					Context context = getApplicationContext();
					CharSequence text = "Invalid UserName, Try Again.";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				} else if (errCode == -4) {
					Context context = getApplicationContext();
					CharSequence text = "Invalid Password, Try Again.";
					int duration = Toast.LENGTH_LONG;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			} catch (Exception e) {
				Context context = getApplicationContext();
				CharSequence text = "You Fucked UP.";
				int duration = Toast.LENGTH_LONG;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		}
    	
    }
    
}
