package com.example.qreate.administrator;

/**
 * This class defines an event (queried from the database) and is used to store its name and details.
 */
public class AdministratorEvent {
    private String eventName;
    private String id;

    /**
     * This is a constructor for the AdministratorEvent class
     * @param eventName the name of the event
     */
    public AdministratorEvent(String eventName, String id) {
        this.eventName = eventName;
        this.id = id;
    }

    /**
     * This method returns the event name
     * @return the event name
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * This method returns the name of the event organizer
     * @return the name of the event organizer
     */

    public String getId() {
        return id;
    }
}
