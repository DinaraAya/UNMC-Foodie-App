package com.example.foodie.views

import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.foodie.R
import com.example.foodie.viewmodels.LoginViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.widget.EditText
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {
    private lateinit var scenario: FragmentScenario<LoginFragment>  // Changed to LoginFragment
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var navController: NavController
    private val loginResult = MutableLiveData<Boolean>()
    private val errorMessage = MutableLiveData<String>()

    @Before
    fun setup() {
        // Initialize mocks directly
        loginViewModel = Mockito.mock(LoginViewModel::class.java)
        navController = Mockito.mock(NavController::class.java)

        Mockito.`when`(loginViewModel.loginResult).thenReturn(loginResult)
        Mockito.`when`(loginViewModel.errorMessage).thenReturn(errorMessage)

        val fragmentFactory = object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                return when (className) {
                    LoginFragment::class.java.name -> {  // Changed to LoginFragment
                        LoginFragment().apply {
                            var viewModel =
                                this@LoginFragmentTest.loginViewModel  // Assuming you have a viewModel property in LoginFragment
                        }
                    }
                    else -> super.instantiate(classLoader, className)
                }
            }
        }

        scenario = launchFragmentInContainer(
            fragmentArgs = null,
            factory = fragmentFactory,
            themeResId = R.style.Theme_Foodie
        )

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }


    @Test
    fun testEmptyFields_showsError() {
        // Click login with empty fields
        onView(withId(R.id.login_button))
            .perform(click())

        // Verify error message is shown
        onView(withText("Please fill in all fields"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testValidLogin_navigatesToHome() {
        // Enter valid credentials
        onView(withId(R.id.et_email_login))
            .perform(typeText("test@email.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password_login))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Click login button
        onView(withId(R.id.login_button))
            .perform(click())

        // Verify login attempt
        Mockito.verify(loginViewModel).loginUser("test@email.com", "password123")

        // Simulate successful login
        loginResult.postValue(true)

        // Verify navigation to home
        Mockito.verify(navController).navigate(R.id.action_loginFragment_to_homePageFragment)
    }

    @Test
    fun testInvalidLogin_showsError() {
        // Enter credentials
        onView(withId(R.id.et_email_login))
            .perform(typeText("test@email.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password_login))
            .perform(typeText("wrongpassword"), closeSoftKeyboard())

        // Click login
        onView(withId(R.id.login_button))
            .perform(click())

        // Simulate failed login
        loginResult.postValue(false)
        errorMessage.postValue("Invalid credentials")

        // Verify error message
        onView(withText("Invalid credentials"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRegisterButton_navigatesToRegister() {
        // Click register button
        onView(withId(R.id.button_register))
            .perform(click())

        // Verify navigation to register
        Mockito.verify(navController).navigate(R.id.action_loginFragment_to_registerFragment)
    }

    @Test
    fun testPasswordVisibilityToggle() {
        // Enter password
        onView(withId(R.id.et_password_login))
            .perform(typeText("password123"), closeSoftKeyboard())

        // Verify initial state - password should be hidden
        onView(withId(R.id.et_password_login))
            .check(matches(hasTransformationMethod(PasswordTransformationMethod::class.java)))

        // Click on password field
        onView(withId(R.id.et_password_login))
            .perform(click())

        // Verify password is now visible
        onView(withId(R.id.et_password_login))
            .check(matches(hasTransformationMethod(HideReturnsTransformationMethod::class.java)))
    }

    private fun hasTransformationMethod(expected: Class<*>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("has transformation method ${expected.simpleName}")
            }

            override fun matchesSafely(item: View): Boolean {
                return item is EditText && expected.isInstance(item.transformationMethod)
            }
        }
    }
}