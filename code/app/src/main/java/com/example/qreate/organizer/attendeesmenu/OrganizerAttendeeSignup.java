package com.example.qreate.organizer.attendeesmenu;

public class OrganizerAttendeeSignup {

    private String attendeeName;
    private String id;


    public OrganizerAttendeeSignup(String attendeeName, String id) {
        this.attendeeName = attendeeName;
        this.id = id;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public String getId() {
        return id;
    }


}
