package com.example.soen341

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.*
import org.junit.Assert


import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import java.util.function.Consumer

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
var testAccount1 : MutableMap<String,String> = mutableMapOf("username" to "TestAccount1", "email" to "Test1@email.com", "password" to "test123")
var testAccount2 : MutableMap<String,String> = mutableMapOf("username" to "TestAccount2", "email" to "Test2@email.com", "password" to "test123")
var testAccounts = arrayListOf<MutableMap<String,String>>(testAccount1, testAccount2)

class RegisterActivityTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(RegisterActivity::class.java)

   /* @Before
    fun deleteUser(){
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).deleteUser(applicationContext,"26", object : VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                assertTrue(response!!["message"],response!!["error"].equals("0"))
            }
        })
    } */

    @Before
    fun user_is_logged_out() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext).userLogoutPref()
        assertFalse(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }

    @Test
    fun user_can_register() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext

        testAccounts.forEach(
                Consumer {
                    RequestHandler.getInstance(applicationContext).registerUser(applicationContext, it.getValue("username"),
                        it.getValue("email"), it.getValue("password"),object : VolleyCallback{
                            override fun onResponse(response: MutableMap<String, String>?) {

                                //check if the error flag from the network response is equal to 0, else assert using the message from the response
                                assertTrue(response!!["message"],response!!["error"].equals("0"))
                                it.put("id",
                                    SharedPrefManager.getInstance(applicationContext).getUserID().toString()
                                )
                            }
                        })

                }
        )

        assertFalse(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }
}