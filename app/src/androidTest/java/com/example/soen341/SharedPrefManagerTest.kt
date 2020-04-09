package com.example.soen341

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.*

class SharedPrefManagerTest {

    private val testAccount1 : MutableMap<String,String> = RegisterLoginTest.data()[0]
    private val testAccount2 : MutableMap<String,String> = RegisterLoginTest.data()[1]

   @Before
    fun userIsLoggedIn() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext)
            .userLoginPref(testId2.toInt(), testId2, testAccount2.getValue("email"))
        Assert.assertTrue(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }


    @Test
    fun userCanChangeUsername() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).changeUsername(applicationContext,"testAccountChange", testId2,object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
            }
        })
    }

    @Test
    fun userCanChangeEmail() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).changeEmail(applicationContext,"changetest@email.com", testId2,object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
            }
        })
    }


    @After
    fun userCanLogout() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext).userLogoutPref()
        Assert.assertFalse(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }

}