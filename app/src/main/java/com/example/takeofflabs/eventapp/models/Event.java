package com.example.takeofflabs.eventapp.models;

/**
 * Created by takeofflabs on 24/01/17.
 */

public class Event {

    private String id;
    private String text;
    private String date;

    public Event(String id, String text, String date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

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

}
