package com.example.qreate;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.example.qreate.attendee.AttendeeActivity;

/**
 * IMPORTANT!!! comment out the line 80: authenticateUser(this); of AttendeeActivity class otherwise this test will FAIL.
 * The following test is only there to test out fragment changes for the navigation bar and not the whole activity.
 */

@RunWith(AndroidJUnit4ClassRunner.class)
public class AttendeeActivityTest {

    @Rule
    public ActivityScenarioRule<AttendeeActivity> activityRule = new ActivityScenarioRule<>(AttendeeActivity.class);

    @Test
    public void navigateToNotificationsPage() {
        // Click the "Events" icon in the navigation bar
        onView(withId(R.id.notifications_icon)).perform(click());
        // Check that the Events page is displayed
        onView(withId(R.id.notif_list_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void navigateToScanPage() {
        // Click the "Scan" icon in the navigation bar
        onView(withId(R.id.qr_icon)).perform(click());
        // Check that the Scan page is displayed
        onView(withId(R.id.logo_text_top)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void navigateToEventsPage() {
        // Click the "Notifications" icon in the navigation bar
        onView(withId(R.id.events_icon_nav)).perform(click());
        // Check that the Notifications page is displayed
        onView(withId(R.id.qr_menu_screen_image)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}