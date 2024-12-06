package com.example.foodie.views

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.foodie.R
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class RegisterFragmentTest {

    @Test
    fun testRegisterFragmentComponentsDisplayed() {
        // Launch the fragment in a test container
        val scenario = launchFragmentInContainer<RegisterFragment>(themeResId = R.style.Theme_FoodieTest)

        // Verify all input fields and buttons are displayed
        onView(withId(R.id.et_fullname)).check(matches(isDisplayed()))
        onView(withId(R.id.et_email)).check(matches(isDisplayed()))
        onView(withId(R.id.et_password)).check(matches(isDisplayed()))
        onView(withId(R.id.et_password_confirm)).check(matches(isDisplayed()))
        onView(withId(R.id.button_register_2)).check(matches(isDisplayed()))
        onView(withId(R.id.button_sign_in)).check(matches(isDisplayed()))
    }

    @Test
    fun testPasswordToggleVisibility() {
        // Launch the fragment in a test container
        val scenario = launchFragmentInContainer<RegisterFragment>(themeResId = R.style.Theme_FoodieTest)

        // Input a password in the field
        onView(withId(R.id.et_password)).perform(typeText("password123"), closeSoftKeyboard())

        // Verify the password is initially hidden
        onView(withId(R.id.et_password)).check(matches(withText("••••••••••••")))

        // Simulate clicking the toggle visibility icon
        onView(withId(R.id.et_password)).perform(click())

        // Verify the password is now visible
        onView(withId(R.id.et_password)).check(matches(withText("password123")))
    }

    @Test
    fun testRegistrationSuccessful() {
        // Mock NavController for navigation verification
        val mockNavController = mock(NavController::class.java)

        // Launch the fragment and set NavController
        val scenario = launchFragmentInContainer<RegisterFragment>(themeResId = R.style.Theme_FoodieTest)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // Fill out registration form
        onView(withId(R.id.et_fullname)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText("johndoe@example.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.et_password_confirm)).perform(typeText("password123"), closeSoftKeyboard())

        // Click the "Register" button
        onView(withId(R.id.button_register_2)).perform(click())

        // Verify navigation occurs to the next fragment
        verify(mockNavController).navigate(R.id.action_registerFragment_to_loginFragment)
    }

    @Test
    fun testRegistrationPasswordsDoNotMatch() {
        // Launch the fragment in a test container
        val scenario = launchFragmentInContainer<RegisterFragment>(themeResId = R.style.Theme_FoodieTest)

        // Fill out registration form with mismatched passwords
        onView(withId(R.id.et_fullname)).perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText("johndoe@example.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.et_password_confirm)).perform(typeText("password124"), closeSoftKeyboard())

        // Click the "Register" button
        onView(withId(R.id.button_register_2)).perform(click())

    }
}

