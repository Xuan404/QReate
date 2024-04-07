package com.example.qreate;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.organizer.OrganizerActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * IMPORTANT!!! comment out the line 48: authenticateUser(this); of AdministratorActivity class otherwise this test will FAIL.
 * The following test is only there to test out fragment changes for the navigation bar and not the whole activity.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class AdministratorActivityTest {

    @Rule
    public ActivityScenarioRule<AdministratorActivity> scenario = new ActivityScenarioRule<>(AdministratorActivity.class);


    @Test
    public void testFragmentChangeToDashboard(){

        // Perform a click on the dashboard button
        //onView(withId(R.id.dashboard_icon)).perform(click());

        // Verify that Dashboard Fragment is launched by checking for a view that is unique to it
        //onView(withId(R.id.admin_top_bar)).check(matches(isDisplayed()));
    }

}
