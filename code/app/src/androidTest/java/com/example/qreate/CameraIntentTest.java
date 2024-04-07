package com.example.qreate;
import android.app.Activity;
import android.app.Instrumentation;
import android.provider.MediaStore;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qreate.attendee.AttendeeActivity;

import org.junit.Rule;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CameraIntentTest {

    @Rule
    public ActivityScenarioRule<AttendeeActivity> activityRule = new ActivityScenarioRule<>(AttendeeActivity.class);

    @Before
    public void setUp() {
        // Initialize Espresso-Intents
        Intents.init();
        // Stubbing the camera intent to prevent it from opening during tests
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void verifyQRScanIntentOnImageViewClick() {
        // Navigate to the tap_to_scan_page by clicking the scan button
        onView(withId(R.id.qr_icon)).perform(click());

        // Simulate clicking the ImageView to "initiate the QR scan"
        onView(withId(R.id.tap_to_scan_qr_button)).perform(click());

        // Verify an intent to start the QR scan was initiated
        intended(hasAction("com.google.zxing.client.android.SCAN"));
    }

    @After
    public void tearDown() {
        // Clean up
        Intents.release();
    }
}


