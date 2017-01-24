package com.example.takeofflabs.eventapp.settings;

/**
 * Created by takeofflabs on 23/01/17.
 */

public class NetworkingSettings {

    //region Properties
    private final String baseUrl = "http://192.168.100.7:3000";
    //endregion

    //region Constructors
    public NetworkingSettings() {
    }
    //endregion

    //region Getters
    public String getBaseUrl() {
        return baseUrl;
    }
    //endregion

}
