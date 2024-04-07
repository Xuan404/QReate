package com.example.qreate.organizer.qrmenu;

import static org.junit.Assert.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class OrganizerQRGeneratorActivityTest {
    @Rule
    public ActivityScenarioRule<OrganizerQRGeneratorActivity> activityRule =
            new ActivityScenarioRule<>(OrganizerQRGeneratorActivity.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void checkIfQRCodePathExists() {
    }
}