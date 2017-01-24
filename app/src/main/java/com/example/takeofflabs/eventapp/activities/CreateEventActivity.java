package com.example.takeofflabs.eventapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.takeofflabs.eventapp.R;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventText;
    private DatePicker eventDate;
    private Button addEventBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_create_event);

        eventText = (EditText) findViewById(R.id.eventText);
        eventDate = (DatePicker) findViewById(R.id.datePicker);
        addEventBtn = (Button) findViewById(R.id.addEventBtn);

        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean makeRequest = false;
                String text = eventText.getText().toString();
                if (text.equals("")) {
                    Toast.makeText(getBaseContext(), "Please enter a text", Toast.LENGTH_LONG).show();
                    makeRequest = true;
                }
            }
        });

    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
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
}
