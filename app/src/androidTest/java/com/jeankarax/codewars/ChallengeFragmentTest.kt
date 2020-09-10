package com.jeankarax.codewars

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.jeankarax.codewars.utils.EspressoIdlingResource
import com.jeankarax.codewars.view.MainActivity
import com.jeankarax.codewars.view.challenges.ChallengeListAdapter
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChallengeFragmentTest {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Before
    fun registerIdlingResource(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource(){
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun whenOpenChallenges_andClickOnAuthored_andClickOnChallenge_shouldShowChallengeDetails(){
        onView(withId(R.id.et_search_user_name))
            .perform(ViewActions.typeText("g964"))
        onView(withId(R.id.ib_search)).perform(click())
        onView(withId(R.id.nav_authored)).perform(click())
        onView(withId(R.id.rv_authored_challenges_list))
            .perform(actionOnItemAtPosition<ChallengeListAdapter.ChallengeViewHolder>(1, click()))
        onView(ViewMatchers.withText("Sum by Factors"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}