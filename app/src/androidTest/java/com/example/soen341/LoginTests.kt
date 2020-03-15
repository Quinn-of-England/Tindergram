package com.example.soen341

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.NoMatchingViewException

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class LoginTests {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun user_is_logged_out() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext).userLogoutPref()
        assertFalse(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }

    @Test
    fun user_can_enter_username() {
        user_is_logged_out()
        onView(withId(R.id.username)).perform(typeText("test123"))
    }



    @Test
    fun user_can_enter_password() {
        user_is_logged_out()
        onView(withId(R.id.pass)).perform(typeText("test123"))
    }

    @Test
    fun user_can_login() {
        user_is_logged_out()
        onView(withId(R.id.username)).perform(typeText("test123"))
        onView(withId(R.id.pass)).perform(typeText("test123"))
        closeSoftKeyboard()
        onView(withId(R.id.login)).perform(click())
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertTrue(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
        user_is_logged_out()
    }
}
