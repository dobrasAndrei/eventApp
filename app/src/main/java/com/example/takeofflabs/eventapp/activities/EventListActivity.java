package com.example.takeofflabs.eventapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.takeofflabs.eventapp.interfaces.RetroManager;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Toast.makeText(getBaseContext(), "S@l", Toast.LENGTH_LONG).show();

                Thread thread = new Thread() {
                    public void run() {
                        try {
                            final NetworkingSettings networkSettings = new NetworkingSettings();
                            final String url = networkSettings.getBaseUrl() + "/event";
                            HttpClient httpclient = getHTTPClient();
                            HttpGet httpGet = new HttpGet(url);
                            httpGet.setHeader("If-Modified-Since","Mon, 23 Jan 2017 21:56:16 GMT");
                            HttpResponse response;
                            try {
                                response = httpclient.execute(httpGet);
                                String server_response = EntityUtils.toString(response.getEntity());
                                JSONArray jsonArray = new JSONArray(server_response);
                                for (int i = 0; i < jsonArray.length(); i+=1) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                }
                                String a = "";
                            } catch (Exception e) {
                                e.printStackTrace();
                                String a = "";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            String a = "";
                        }
                    }

                };
                thread.start();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
}
