package com.example.qreate.administrator;

public class AdministratorEvent {
    private String eventName;
    private String eventOrganizer;

    public AdministratorEvent(String eventName, String eventOrganizer) {
        this.eventName = eventName;
        this.eventOrganizer = eventOrganizer;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }
}
