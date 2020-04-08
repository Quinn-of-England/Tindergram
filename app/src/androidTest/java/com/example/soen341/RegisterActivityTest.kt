package com.example.soen341

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.*


import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */class RegisterActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(RegisterActivity::class.java)

    @Before
    fun user_is_logged_out() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext).userLogoutPref()
        assertFalse(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }

    @Test
    fun user_can_register() {
        onView(withId(R.id.username)).perform(typeText("test123"))
        onView(withId(R.id.email)).perform(typeText("valid@email.com"))
        onView(withId(R.id.enterPass)).perform(typeText("test123"))
        onView(withId(R.id.confirmPass)).perform(typeText("test123"))
        closeSoftKeyboard()
        onView(withId(R.id.register)).perform(click())
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertFalse(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }
}