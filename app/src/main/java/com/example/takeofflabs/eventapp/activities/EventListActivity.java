package com.example.takeofflabs.eventapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.takeofflabs.eventapp.R;
import com.example.takeofflabs.eventapp.database.EventDatabase;
import com.example.takeofflabs.eventapp.models.Event;
import com.example.takeofflabs.eventapp.settings.NetworkingSettings;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventListActivity extends AppCompatActivity {

    //region Properties
    private final String DEFAULT = "null";
    private final String IF_MODIFIED_SINCE = "If-Modified-Since";
    private final String LAST_MODIFIED = "Last-Modified";
    private final String EVENT_ID = "id";
    private final String EVENT_TEXT = "text";
    private final String EVENT_DATE = "date";
    private EventDatabase eventDatabase;
    private ProgressDialog progressDialog;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region viewSetup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        eventDatabase = new EventDatabase(this);
        progressDialog = new ProgressDialog(EventListActivity.this);
        //endregion

        try {
            Log.d("events.size = ", String.valueOf(eventDatabase.getEvents().size()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //region CheckConnectivity
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (hasActiveInternetConnection()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "You are connected", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "You are not connected", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        thread.start();
        //endregion

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                if (isNetworkAvailable()) {
                    progressDialog.setMessage("Processing data...");
                    progressDialog.show();
                    Thread thread = new Thread() {
                        public void run() {
                            try {
                                final NetworkingSettings networkSettings = new NetworkingSettings();
                                final String url = networkSettings.getBaseUrl() + "/event";
                                HttpClient httpclient = getHTTPClient();
                                HttpGet httpGet = new HttpGet(url);

                                final SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("events", Context.MODE_PRIVATE);
                                final String last_modified = sharedPreferences.getString(LAST_MODIFIED, DEFAULT);
                                if (!last_modified.equals(DEFAULT)) {
                                    httpGet.setHeader(IF_MODIFIED_SINCE, last_modified);
                                }

                                HttpResponse response;
                                try {
                                    response = httpclient.execute(httpGet);
                                    if (response.getStatusLine().getStatusCode() == 200) {
                                        String last_modif_response = get_value(response.getHeaders(LAST_MODIFIED)[0].toString());

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(LAST_MODIFIED, last_modif_response);
                                        editor.apply();

                                        // parsing and saving the events
                                        String server_response = EntityUtils.toString(response.getEntity());
                                        JSONArray jsonArray = new JSONArray(server_response);
                                        for (int i = 0; i < jsonArray.length(); i += 1) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String id = jsonObject.optString(EVENT_ID, DEFAULT);
                                            String text = jsonObject.optString(EVENT_TEXT, DEFAULT);
                                            String date = jsonObject.optString(EVENT_DATE, DEFAULT);
                                            Event event = new Event(id, text, date);
                                            eventDatabase.saveEvent(event);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getBaseContext(), "Sync completed!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(EventListActivity.this, EventListActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                    } else if (response.getStatusLine().getStatusCode() == 304){
                                        Snackbar.make(view, "You are up to date.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                        progressDialog.dismiss();
                                    } else {
                                        Snackbar.make(view, "Something went wrong. Please try again later.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                        progressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    };
                    thread.start();
                    try {
                        thread.join();
                    }catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    Log.d("events.size = ", String.valueOf(eventDatabase.getEvents().size()));
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Please connect to the internet", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_event) {
            Intent intent = new Intent(EventListActivity.this, CreateEventActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_check_connection) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (hasActiveInternetConnection()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "You are connected", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "You are not connected", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
            thread.start();
        }

        return super.onOptionsItemSelected(item);
    }

    //region privateMethods
    private static HttpClient getHTTPClient() {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false); // this
        // line
        // is
        // to
        // not
        // send
        // a
        // 'Expect:
        // 100-continue'
        // handshake
        // request,
        // as
        // this
        // causes
        // problems

        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        HttpConnectionParams.setConnectionTimeout(params, 20000);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(params, 20000);

        HttpClient httpclient = new DefaultHttpClient(params);
        //HttpClient httpclient = HttpClientBuilder.create().build();
        return httpclient;

    }

    private String get_value(String s) {
        int start = 0;
        String value = "";
        for (int i = 0; i <s.length(); i+=1) {
            if (s.charAt(i) == ':') {
                start = i+1;
                break;
            }
        }
        for (int j = start+1; j < s.length(); j+=1) {
            value+= s.charAt(j);
        }
        return value;
    }

    public boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("error ", "Error checking internet connection", e);
            }
        } else {
            return false;
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    //endregion

}
