package com.example.takeofflabs.eventapp.interfaces;

import com.example.takeofflabs.eventapp.settings.Settings;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by takeofflabs on 23/01/17.
 */

public interface RetroManager {

    @GET("/{event}")
    Call<RetroManager> getEvents(@Path("event") String event);

    @POST("/event")
    Call<RetroManager> postEvent(@Body Object postEvent);

}
