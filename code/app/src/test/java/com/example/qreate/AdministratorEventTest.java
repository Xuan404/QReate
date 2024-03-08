package com.example.qreate;

import com.example.qreate.administrator.AdministratorEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;


public class AdministratorEventTest {
    @Test
    void testGetEventName() {
        // Setting up a mock event
        String expectedEventName = "Tech Conference 2024";
        String eventOrganizer = "Tech Innovators";
        AdministratorEvent event = new AdministratorEvent(expectedEventName, eventOrganizer);

        // Invoking the getEventName() method
        String actualEventName = event.getEventName();

        // Assert
        assertEquals(expectedEventName, actualEventName);
    }

    @Test
    void testGetEventOrganizer() {
        // Setting up a mock event
        String eventName = "Tech Conference 2024";
        String expectedEventOrganizer = "Tech Innovators";
        AdministratorEvent event = new AdministratorEvent(eventName, expectedEventOrganizer);

        // Invoking the getEventOrganizer() method
        String actualEventOrganizer = event.getEventOrganizer();

        // Assert
        assertEquals(expectedEventOrganizer, actualEventOrganizer);
    }
}
