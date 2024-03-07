package com.example.qreate;

public class Event {
    private String event;
    private String detail;

    public Event(String event, String detail){
        this.event = event;
        this.detail = detail;
    }

    String getEventName(){
        return this.event;
    }

    String getDetail(){
        return this.detail;
    }
}
