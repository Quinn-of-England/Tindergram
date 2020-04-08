package com.example.soen341

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.*

class SharedPrefManagerTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(HomeActivity::class.java)

    @Before
    fun user_is_logged_in() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext)
            .userLoginPref(20, "test123", "test123@test.net")
        Assert.assertTrue(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }

    @Test
    fun get_username() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext)
            .getUserUsername()
        Assert.assertEquals(
            "test123",
            SharedPrefManager.getInstance(applicationContext).getUserUsername()
        )
    }

    @Test
    fun get_email() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext)
            .getUserUsername()
        Assert.assertEquals(
            "test123@test.net",
            SharedPrefManager.getInstance(applicationContext).getUserEmail()
        )
    }

    @Test
    fun get_user_id() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext)
            .getUserUsername()
        Assert.assertEquals(
            20,
            SharedPrefManager.getInstance(applicationContext).getUserID()
        )
    }

    @Test
    fun user_can_change_username() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).changeUsername(applicationContext,"testAccountChange","26",object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
            }
        })
    }

    @Test
    fun user_can_change_email() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        RequestHandler.getInstance(applicationContext).changeEmail(applicationContext,"changetest@email.com","26",object :VolleyCallback{
            override fun onResponse(response: MutableMap<String, String>?) {
                Assert.assertTrue(response!!["message"],response!!["error"].equals("0"))
            }
        })
    }

    @After
    fun user_can_logout() {
        val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        SharedPrefManager.getInstance(applicationContext).userLogoutPref()
        Assert.assertFalse(SharedPrefManager.getInstance(applicationContext).isUserLoggedIn())
    }

}