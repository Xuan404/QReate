package com.example.qreate.organizer.attendeesmenu;

/**
 * Represents attendee check-in details for an organizer.
 */
public class OrganizerAttendeeCheckin {
    private String attendeeName;
    private String id;
    private String checkinCount;

    /**
     * Constructs an OrganizerAttendeeCheckin object with provided attendee details.
     *
     * @param attendeeName The name of the attendee.
     * @param id           The unique ID of the attendee.
     * @param checkinCount The number of times the attendee has checked in.
     */
    public OrganizerAttendeeCheckin(String attendeeName, String id, Integer checkinCount) {
        this.attendeeName = attendeeName;
        this.id = id;
        this.checkinCount = String.valueOf(checkinCount);
    }

    /**
     * Retrieves the name of the attendee.
     *
     * @return The name of the attendee.
     */
    public String getAttendeeName() {
        return attendeeName;
    }

    /**
     * Retrieves the unique ID of the attendee.
     *
     * @return The ID of the attendee.
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the number of times the attendee has checked in.
     *
     * @return The check-in count of the attendee.
     */
    public String getCheckinCount() {
        return checkinCount;
    }
}
