package com.example.takeofflabs.eventapp.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by takeofflabs on 23/01/17.
 */

public class Settings {

    // region Getters And Setters
    @SerializedName("eventId")
    @Expose
    private String eventId = "";

    @SerializedName("eventText")
    @Expose
    private String eventText = "";

    @SerializedName("eventDate")
    @Expose
    private Date eventDate;
    // endregion

    // region Getters And Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventText() {
        return eventText;
    }

    public void setEventText(String eventText) {
        this.eventText = eventText;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    // endregion

}
