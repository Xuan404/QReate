package com.example.qreate;

import com.example.qreate.administrator.AdministratorEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;


public class AdministratorEventTest {
    @Test
    void testGetEventName() {
        // Setting up a mock event
        String expectedEventName = "Tech Conference 2024";
        String eventId = "123";
        AdministratorEvent event = new AdministratorEvent(expectedEventName, eventId);

        // Invoking the getEventName() method
        String actualEventName = event.getEventName();

        // Assert
        assertEquals(expectedEventName, actualEventName);
    }
}
