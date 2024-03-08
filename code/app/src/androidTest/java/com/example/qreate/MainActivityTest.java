package com.example.qreate;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testActivityChangeOrganizer() {
        // Perform a click on the button that should start Organizer Activity
        onView(withId(R.id.test_Organizer)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Organizer Activity
        onView(withId(R.id.organizer_handler)).check(matches(isDisplayed()));
    }

    @Test
    public void testActivityChangeAttendee() {
        // Perform a click on the button that should start Attendee Activity
        onView(withId(R.id.test_Attendee)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Attendee Activity
        onView(withId(R.id.attendee_handler)).check(matches(isDisplayed()));
    }

    @Test
    public void testActivityChangeAdministrator() {
        // Perform a click on the button that should start Admin Activity
        onView(withId(R.id.test_Administrator)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Admin Activity
        onView(withId(R.id.administrator_handler)).check(matches(isDisplayed()));
    }

}



