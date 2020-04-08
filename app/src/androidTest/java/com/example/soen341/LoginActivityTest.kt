package com.example.soen341

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.*
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun user_is_logged_out() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext

        SharedPrefManager.getInstance(applicationContext).userLogoutPref()
        assertFalse(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }

    @Test
    fun user_can_login() {
        onView(withId(R.id.username)).perform(typeText("test123"))
        onView(withId(R.id.pass)).perform(typeText("test123"))
        closeSoftKeyboard()
        onView(withId(R.id.login)).perform(click())
        TimeUnit.SECONDS.sleep(1)
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertTrue(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
        SharedPrefManager.getInstance(applicationContext).userLogoutPref()
    }
}
