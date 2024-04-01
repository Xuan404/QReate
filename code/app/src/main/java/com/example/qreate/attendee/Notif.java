package com.example.qreate.attendee;

/**
 * Represents a notification object within the application, specifically for the attendee interface.
 * This class encapsulates the details of a notification, including the notification message
 * and the name of the organizer who issued the notification.
 *
 * this class is used to populate lists or arrays where notification data is displayed
 * to the user in the Qreate app itself.
 *
 * @author Shraddha Mehta
 */

public class Notif {
    private String description;
    private String title;


    /**
     * notification details
     * @param description
     * @param title
     */
    Notif(String description, String title){
        this.description = description;
        this.title = title;
    }

    /**
     * Returns the notification message of this Notif instance.
     *
     * @return The notification message content.
     */

    String getNotificationDescription(){
        return description;
    }

    /**
     * Returns the name of the organizer related to this notification.
     *
     * @return The organizer's name.
     */

    String getTitle(){
        return title;
    }
}