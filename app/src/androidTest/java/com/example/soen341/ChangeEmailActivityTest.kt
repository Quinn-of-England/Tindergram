package com.example.soen341

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(SettingsActivity::class.java)

    @Before
    fun user_is_logged_in() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext)
            .userLoginPref(20, "test123", "test123@test.net")
        Assert.assertTrue(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }

    @Test
    fun user_can_change_email() {

        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext)
            .userLoginPref(20, "test123", "test123@test.net")
    }

}