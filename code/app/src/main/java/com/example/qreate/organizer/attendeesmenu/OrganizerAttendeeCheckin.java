package com.example.qreate.organizer.attendeesmenu;

/**
 * Attendee class for retriving name and doc id
 * @author Akib Zaman Choudhury
 */
public class OrganizerAttendeeCheckin {
    private String attendeeName;
    private String id;
    private String checkinCount;

    public OrganizerAttendeeCheckin(String attendeeName, String id, Integer checkinCount) {
        this.attendeeName = attendeeName;
        this.id = id;
        this.checkinCount = String.valueOf(checkinCount);
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public String getId() {
        return id;
    }

    public String getCheckinCount() {
        return checkinCount;
    }
}
