package com.example.qreate.organizer.qrmenu;
/**
 * The following class represents a event
 *
 * Outstanding Issue: Needs to also store a image (the poster)
 * @author Denis Soh
 */
public class OrganizerEvent {
    private String event;
    private String detail;
    /**
     * event constructer
     *
     * @param event the event name
     * @param detail the event details
     *
     */

    public OrganizerEvent(String event, String detail){
        this.event = event;
        this.detail = detail;
    }

    /**
     * returns name
     *
     * @return String
     */
    public String getEventName(){
        return this.event;
    }
    /**
     * returns details
     *
     * @return String
     */

    public String getDetail(){
        return this.detail;
    }
}
