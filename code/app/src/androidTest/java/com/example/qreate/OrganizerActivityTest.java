package com.example.qreate;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.qreate.organizer.OrganizerActivity;

import org.junit.Rule;
import org.junit.Test;


/**
 * IMPORTANT!!! comment out the line 53: authenticateUser(this); otherwise this test will FAIL
 * The following test is only there to test out fragment changes for the navigation bar and not the
 * whole activity
 */
public class OrganizerActivityTest {
    @Rule
    public ActivityScenarioRule<OrganizerActivity> scenario = new ActivityScenarioRule<>(OrganizerActivity.class);

    @Test
    public void testFragmentChangeToQROrganizer() {
        // Perform a click on the button that should start qr menu fragment
        onView(withId(R.id.qr_menu)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Organizer Activity
        onView(withId(R.id.organizer_qr_menu_screen)).check(matches(isDisplayed()));
    }

    @Test
    public void testFragmentChangeToNotificationOrganizer() {
        // Perform a click on the button that should start notification menu fragment
        onView(withId(R.id.notifications_menu)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Organizer Activity
        onView(withId(R.id.organizer_notifications_menu)).check(matches(isDisplayed()));
    }

    @Test
    public void testFragmentChangeToAttendeeListOrganizer() {
        // Perform a click on the button that should start attendee menu fragment
        onView(withId(R.id.attendee_list_menu)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Organizer Activity
        onView(withId(R.id.organizer_attendee_list_menu)).check(matches(isDisplayed()));
    }

    @Test
    public void testFragmentChangeToGeolocationOrganizer() {
        // Perform a click on the button that should start geolocation menu fragment
        onView(withId(R.id.geolocation_menu)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Organizer Activity
        onView(withId(R.id.organizer_geolocation_menu)).check(matches(isDisplayed()));
    }


}
