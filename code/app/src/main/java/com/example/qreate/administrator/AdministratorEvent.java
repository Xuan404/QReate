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
     * @param id the document ID of the event in the Events collection
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
     * This method returns the event Id
     * @return the document ID of the event in the Events collection
     */
    public String getId() {
        return id;
    }
}
