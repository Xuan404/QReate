package com.example.qreate.attendee;

public class Notif {
    private String notification;
    private String organizer;

    Notif(String notification, String organizer){
        this.notification = notification;
        this.organizer = organizer;
    }

    String getNotificationDescription(){
        return this.notification;
    }

    String getOrganizerName(){
        return this.organizer;
    }
}