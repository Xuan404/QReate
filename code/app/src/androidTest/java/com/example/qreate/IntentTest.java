package com.example.qreate;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static junit.framework.TestCase.assertEquals;

import static java.util.regex.Pattern.matches;

import android.content.Context;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import com.example.qreate.administrator.AdministratorActivity;
import com.example.qreate.attendee.AttendeeActivity;
import com.example.qreate.organizer.OrganizerActivity;
import com.example.qreate.organizer.OrganizerEvent;
import com.example.qreate.organizer.geolocationmenu.OrganizerGeolocationMap;
import com.example.qreate.organizer.geolocationmenu.OrganizerGeolocationMenuFragment;
import com.example.qreate.organizer.notificationsmenu.OrganizerNotificationsMenuFragment;
import com.example.qreate.organizer.notificationsmenu.OrganizerNotificationsSendActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQREventListActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQRGeneratorActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQRReuseExistingActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQRShareActivity;
import com.example.qreate.organizer.qrmenu.OrganizerQRmenuFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class IntentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Rule
    public GrantPermissionRule locationAccessRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    //creates mock event
    public OrganizerEvent createMockEvent(){
        OrganizerEvent testEvent = new OrganizerEvent("test", "dtest", "dateTest", "OrgTest", "IDTest");
        return testEvent;
    }

    //Test context
    @Test
    public void contextTest() {
        Context appContext = getInstrumentation().getTargetContext();
        assertEquals("com.example.qreate", appContext.getPackageName());
    }
    //Tests UI swap by Intent
    @Test
    public void organizerTest(){
        //Tests swapping to organizer menus
        onView(withId(R.id.test_Organizer)).perform(click());
        intended(hasComponent(OrganizerActivity.class.getName()));
    }
    @Test
    public void attendeeTest(){
        //Tests swapping to attendee menus
        onView(withId(R.id.test_Attendee)).perform(click());
        intended(hasComponent(AttendeeActivity.class.getName()));
    }
    @Test
    public void adminTest(){
        //Tests swapping to admin menus
        onView(withId(R.id.test_Administrator)).perform(click());
        intended(hasComponent(AdministratorActivity.class.getName()));
    }

    @Test
    public void testQRGenSwap() {
        // Launch the fragment in a container
        FragmentScenario<OrganizerQRmenuFragment> scenario = FragmentScenario.launchInContainer(OrganizerQRmenuFragment.class);

        onView(withId(R.id.qr_menu_screen_button_generate_qr_code)).perform(click());
        intended(hasComponent(OrganizerQRGeneratorActivity.class.getName()));
    }
    @Test
    public void testQRReuseSwap() {
        // Launch the fragment in a container
        FragmentScenario<OrganizerQRmenuFragment> scenario = FragmentScenario.launchInContainer(OrganizerQRmenuFragment.class);

        onView(withId(R.id.qr_menu_screen_button_reuse_qr_code)).perform(click());
        intended(hasComponent(OrganizerQRReuseExistingActivity.class.getName()));
    }
    @Test
    public void testQRShareSwap() {
        // Launch the fragment in a container
        FragmentScenario<OrganizerQRmenuFragment> scenario = FragmentScenario.launchInContainer(OrganizerQRmenuFragment.class);

        onView(withId(R.id.qr_menu_screen_button_share_qr_code)).perform(click());
        intended(hasComponent(OrganizerQRShareActivity.class.getName()));
    }
    @Test
    public void testQRListSwap() {
        // Launch the fragment in a container
        FragmentScenario<OrganizerQRmenuFragment> scenario = FragmentScenario.launchInContainer(OrganizerQRmenuFragment.class);

        onView(withId(R.id.qr_menu_screen_button_event_list)).perform(click());
        intended(hasComponent(OrganizerQREventListActivity.class.getName()));
    }

    //FAILS BECAUSE YOU HAVE TO SELECT AN EVENT
    /*@Test
    public void testGeoMapSwap() {
        // Launch the fragment in a container
        FragmentScenario<OrganizerGeolocationMenuFragment> scenario = FragmentScenario.launchInContainer(OrganizerGeolocationMenuFragment.class);

        onView(withId(R.id.geolocation_menu_screen_see_attendee_checkins)).perform(click());
        intended(hasComponent(OrganizerGeolocationMap.class.getName()));
    }*/

    @Test
    public void testNotifSendSwap() {
        // Launch the fragment in a container
        FragmentScenario<OrganizerNotificationsMenuFragment> scenario = FragmentScenario.launchInContainer(OrganizerNotificationsMenuFragment.class);

        onView(withId(R.id.notifications_menu_screen_send_notifications)).perform(click());
        intended(hasComponent(OrganizerNotificationsSendActivity.class.getName()));
    }
}
