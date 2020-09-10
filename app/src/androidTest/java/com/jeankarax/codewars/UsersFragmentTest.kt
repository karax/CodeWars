package com.jeankarax.codewars

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.jeankarax.codewars.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsersFragmentTest {

    @get:Rule
    val rule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun whenCLickOnSearch_andUserNameIsEmpty_shouldShowError(){
        onView(withId(R.id.ib_search)).perform(click())
        onView(withId(R.id.tv_empty_user_error)).check(matches(isDisplayed()))
    }

    @Test
    fun whenClickOnSearch_andUserNameIsNotEmpty_shouldShowLoading(){
        onView(withId(R.id.et_search_user_name)).perform(typeText("zruF"))
        onView(withId(R.id.ib_search)).perform(click())
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
    }
}