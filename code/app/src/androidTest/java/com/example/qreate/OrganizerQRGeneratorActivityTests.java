package com.example.qreate;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.qreate.organizer.OrganizerActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQRGeneratorActivity;
import com.google.zxing.WriterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class OrganizerQRGeneratorActivityTests {
    @Rule
    public ActivityScenarioRule<OrganizerQRGeneratorActivity> scenario = new androidx.test.ext.junit.rules.ActivityScenarioRule<>(OrganizerQRGeneratorActivity .class);
    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }
    @Test
    public void testGenerateQR() {

        String text = "Test QR Code";

        //generate and check qr
        scenario.getScenario().onActivity(activity ->
        {
            try {
                Bitmap qrCodeBitmap = activity.generateQRCode(text);
                assertEquals(512, qrCodeBitmap.getWidth());
                assertEquals(512, qrCodeBitmap.getHeight());

            } catch (WriterException e) {
                throw new RuntimeException(e);
            }
        });
    }

    //CRASHES APP SO TEST NEVER FINISHES NOT SURE WHY
    @Test
    public void testUI() {


        // Perform a click on the button that should start qr menu fragment
        onView(withId(R.id.generate_qr_code_screen_backbutton)).perform(click());

        // Verify that Organizer Activity is launched by checking for a view that is unique to Organizer Activity
        onView(withId(R.id.qr_menu_screen_button_generate_qr_code)).check(matches(isDisplayed()));

        //Swap Back to qr generator
        onView(withId(R.id.qr_menu_screen_button_generate_qr_code)).perform(click());
    }
}