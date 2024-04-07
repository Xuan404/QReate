package com.example.qreate.organizer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class OrganizerEventTest {
    String event = "test event name";
    String detail = "test event details";
    String organizer = "test event organizer";
    String date = "test event date";
    String documentID = "test event id";

    //GETTERS
    @Test
    void getDocumentID() {
        OrganizerEvent mockEvent = new OrganizerEvent(event, detail, date, organizer, documentID);
        assertEquals(documentID, mockEvent.getDocumentID());
    }

    @Test
    void getEvent() {
        OrganizerEvent mockEvent = new OrganizerEvent(event, detail, date, organizer, documentID);
        assertEquals(event, mockEvent.getEvent());
    }

    @Test
    void getOrganizer() {
        OrganizerEvent mockEvent = new OrganizerEvent(event, detail, date, organizer, documentID);
        assertEquals(organizer, mockEvent.getOrganizer());
    }

    @Test
    void getDate() {
        OrganizerEvent mockEvent = new OrganizerEvent(event, detail, date, organizer, documentID);
        assertEquals(date, mockEvent.getDate());
    }

    @Test
    void getDetail() {
        OrganizerEvent mockEvent = new OrganizerEvent(event, detail, date, organizer, documentID);
        assertEquals(detail, mockEvent.getDetail());
    }

}