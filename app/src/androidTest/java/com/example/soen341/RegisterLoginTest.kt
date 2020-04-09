package com.example.soen341

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.MethodSorters
import org.junit.runners.Parameterized
var testId1 = ""
var testId2 = ""

@RunWith(Parameterized::class)
class RegisterLoginTest(private val testAccount: MutableMap<String,String>) {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(HomeActivity::class.java)

    companion object{
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf<MutableMap<String, String>>(
            mutableMapOf(
                "username" to "TestAccount1",
                "email" to "Test1@email.com",
                "password" to "test123"
            ),
            mutableMapOf(
                "username" to "TestAccount2",
                "email" to "Test2@email.com",
                "password" to "test123"
            )
        )
    }

    @Test
    fun canUserRegister() {

        val applicationContext = activityRule.activity.applicationContext
        RequestHandler.getInstance(applicationContext).registerUser(applicationContext, testAccount.getValue("username"),
            testAccount.getValue("email"), testAccount.getValue("password"),object : VolleyCallback{
                override fun onResponse(response: MutableMap<String, String>?) {

                    //check if the error flag from the network response is equal to 0, else assert using the message from the response
                    Assert.assertTrue(response!!["message"], response!!["error"].equals("0"))
                    if(testAccount.getValue("username").equals("TestAccount1"))
                        testId1 =     SharedPrefManager.getInstance(applicationContext).getUserID().toString()
                    else
                        testId2 =     SharedPrefManager.getInstance(applicationContext).getUserID().toString()

                }
            })
    }

    @Test
    fun canUserLogin() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext

        RequestHandler.getInstance(applicationContext).loginUser(applicationContext,
            testAccount.getValue("username"), testAccount.getValue("password"),object : VolleyCallback{
                override fun onResponse(response: MutableMap<String, String>?) {
                    Assert.assertTrue(response!!["message"], response["error"].equals("0"))
                }
            })

        SharedPrefManager.getInstance(applicationContext).userLogoutPref()
    }

}




