package com.example.qreate;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.qreate.organizer.OrganizerActivity;
import com.example.qreate.organizer.OrganizerQRGeneratorActivity;
import com.example.qreate.organizer.OrganizerQRmenuFragment;

import org.junit.Rule;
import org.junit.Test;

public class OrganizerQRmenuFragmentTest {

//    @Test
//    public void testFragmentQRGeneratorButton() {
//
//        // Launch the MyFragment
//        Bundle fragmentArgs = new Bundle();
//        FragmentScenario.launchInContainer(OrganizerQRmenuFragment.class, fragmentArgs);
//
//        // Perform a click on the button
//        onView(withId(R.id.qr_menu_screen_button_generate_qr_code)).perform(click());
//
//        // Assert that the TextView's text has changed to "Button Clicked!"
//        //onView(withId(R.id.generate_qr_code_screen_backbutton)).check(matches(isDisplayed()));
//    }
}
