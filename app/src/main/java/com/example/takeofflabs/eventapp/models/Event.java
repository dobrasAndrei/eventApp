package com.example.takeofflabs.eventapp.models;

/**
 * Created by takeofflabs on 24/01/17.
 */

public class Event {

    //region Properties
    private String id;
    private String text;
    private String date;
    //endregion

    //region Constructors
    public Event(String id, String text, String date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }
    //endregion

    //region Getters And Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    //endregion

}
