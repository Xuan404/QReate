package com.example.qreate.organizer.attendeesmenu;

/**
 * Class for retrieving signed-up Attendee name and ID.
 * This class represents an Attendee signed up for an event.
 *
 */
public class OrganizerAttendeeSignup {

    private String attendeeName;
    private String id;


    /**
     * Constructs a new OrganizerAttendeeSignup object with the given attendee name and ID.
     *
     * @param attendeeName The name of the signed-up Attendee.
     * @param id           The ID of the signed-up Attendee.
     */
    public OrganizerAttendeeSignup(String attendeeName, String id) {
        this.attendeeName = attendeeName;
        this.id = id;
    }

    /**
     * Gets the name of the signed-up Attendee.
     *
     * @return The name of the Attendee.
     */
    public String getAttendeeName() {
        return attendeeName;
    }

    /**
     * Gets the ID of the signed-up Attendee.
     *
     * @return The ID of the Attendee.
     */
    public String getId() {
        return id;
    }


}
