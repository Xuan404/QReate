package com.example.qreate.organizer;

public class OrganizerEvent {
    private String event;
    private String detail;

    public OrganizerEvent(String event, String detail){
        this.event = event;
        this.detail = detail;
    }

    public String getEventName(){
        return this.event;
    }

    String getDetail(){
        return this.detail;
    }
}
