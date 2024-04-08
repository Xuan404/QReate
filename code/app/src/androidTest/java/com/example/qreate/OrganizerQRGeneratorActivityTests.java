package com.example.qreate;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.organizer.OrganizerActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQRGeneratorActivity;
import com.google.zxing.WriterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
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

    //TODO account for usage of finish()
    @Test
    public void testBackButton() {

        // Perform a click on the button that should start qr menu fragment
        onView(withId(R.id.generate_qr_code_screen_backbutton)).perform(click());
        intended(hasComponent(OrganizerActivity.class.getName()));
    }

}