package com.jeankarax.codewars

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.jeankarax.codewars.utils.EspressoIdlingResource
import com.jeankarax.codewars.view.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChallengesListsFragmentTest {

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
    fun whenClickOnSearch_andUseNameIsNotEmpty_andUserIsFound_shouldShowCompletedChallenges(){
        onView(withId(R.id.et_search_user_name))
            .perform(ViewActions.typeText("g964"))
        onView(withId(R.id.ib_search)).perform(ViewActions.click())
        onView(ViewMatchers.withText("Turkish Numbers, 0-99"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun whenOpenChallenges_andClickOnAuthored_shouldShowAuthoredChallenges(){
        onView(withId(R.id.et_search_user_name))
            .perform(ViewActions.typeText("g964"))
        onView(withId(R.id.ib_search)).perform(ViewActions.click())
        onView(withId(R.id.nav_authored)).perform(ViewActions.click())
        onView(ViewMatchers.withText("Sum by Factors"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun whenOpenChallenges_andScrollToLastItemOfThePage_shouldLoadNextPage(){
        onView(withId(R.id.et_search_user_name))
            .perform(ViewActions.typeText("g964"))
        onView(withId(R.id.ib_search)).perform(ViewActions.click())
        scrollToTheEnd()
        onView(ViewMatchers.withText("Even numbers in an array"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun scrollToTheEnd() {
        for(i in 0..16){
            onView(withId(R.id.rv_completed_challenges_list)).perform(
                ViewActions
                    .swipeUp()
            )
        }
    }


}