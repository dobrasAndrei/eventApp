package com.example.takeofflabs.eventapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takeofflabs.eventapp.R;
import com.example.takeofflabs.eventapp.database.EventDatabase;
import com.example.takeofflabs.eventapp.models.Event;
import com.example.takeofflabs.eventapp.settings.NetworkingSettings;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.http.PartMap;

public class CreateEventActivity extends AppCompatActivity {

    //region Properties
    private EditText eventText;
    private DatePicker eventDate;
    private Button addEventBtn;
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
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_create_event);
        eventDatabase = new EventDatabase(this);
        progressDialog = new ProgressDialog(CreateEventActivity.this);
        //region widgetsSetup
        eventText = (EditText) findViewById(R.id.eventText);
        eventDate = (DatePicker) findViewById(R.id.datePicker);
        addEventBtn = (Button) findViewById(R.id.addEventBtn);
        //endregion

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Processing data...");
                progressDialog.show();
                boolean makeRequest = true;
                final String text = eventText.getText().toString();
                if (text.equals("")) {
                    Toast.makeText(getBaseContext(), "Please enter a text", Toast.LENGTH_LONG).show();
                    makeRequest = false;
                }
                if (makeRequest) {
                    final Date date = getDateFromDatePicker(eventDate);
                    final Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final NetworkingSettings networkSettings = new NetworkingSettings();
                            final String url = networkSettings.getBaseUrl() + "/event";
                            HttpClient httpclient = getHTTPClient();
                            HttpPost httpPost = new HttpPost(url);

                            JSONObject object = new JSONObject();

                            try {
                                object.put("text", text);
                                object.put("date", date);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            String newEvent = object.toString();
                            try {
                                httpPost.setEntity(new StringEntity(newEvent, "UTF8"));
                                httpPost.setHeader("Content-type", "application/json");
                            }catch (UnsupportedEncodingException uee) {
                                uee.printStackTrace();
                            }


                            HttpResponse response;
                            try {
                                response = httpclient.execute(httpPost);
                                String server_response = EntityUtils.toString(response.getEntity());
                                try {
                                    JSONObject jsonObject = new JSONObject(server_response);
                                    String id = jsonObject.optString(EVENT_ID, DEFAULT);
                                    String text = jsonObject.optString(EVENT_TEXT, DEFAULT);
                                    String date = jsonObject.optString(EVENT_DATE, DEFAULT);
                                    Event event = new Event(id, text, date);
                                    eventDatabase.saveEvent(event);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "Event added!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(CreateEventActivity.this, EventListActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    }catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    Log.d("events.size after add= ", String.valueOf(eventDatabase.getEvents().size()));
                }
            }
        });

    }

    //region privateMethods
    private static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
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
    //endregion

}
