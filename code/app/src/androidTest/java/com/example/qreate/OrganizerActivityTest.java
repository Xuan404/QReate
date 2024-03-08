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

public class OrganizerActivityTest {
    @Rule
    public ActivityScenarioRule<OrganizerActivity> scenario = new ActivityScenarioRule<>(OrganizerActivity.class);

    @Test
    public void testActivityChangeOrganizer() {
        // Perform a click on the button that should start Organizer Activity
        onView(withId(R.id.test_Organizer)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Organizer Activity
        onView(withId(R.id.organizer_handler)).check(matches(isDisplayed()));
    }
}
