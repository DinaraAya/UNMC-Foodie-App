package com.example.foodie.views


import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.foodie.R
import org.junit.Test
import org.junit.runner.RunWith
import androidx.fragment.app.testing.launchFragmentInContainer

@RunWith(AndroidJUnit4::class)
class HomePageFragmentTest {

    @Test
    fun testIndoorOutdoorButtonSelection() {
        // Launch HomePageFragment without Hilt
        launchFragmentInContainer<HomePageFragment>(themeResId = R.style.Theme_Foodie)

        // Check that the indoor button is initially displayed and enabled
        onView(withId(R.id.indoor_button))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        // Click the outdoor button
        onView(withId(R.id.outdoor_button)).perform(click())

        // Verify that the outdoor button is displayed and enabled after clicking
        onView(withId(R.id.outdoor_button))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        // Verify that the indoor button is still displayed
        onView(withId(R.id.indoor_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPromoViewPagerIsDisplayed() {
        // Launch HomePageFragment without Hilt
        launchFragmentInContainer<HomePageFragment>(themeResId = R.style.Theme_Foodie)

        // Check that the ViewPager for promos is displayed
        onView(withId(R.id.viewpager_promos))
            .check(matches(isDisplayed()))

        // Check that the TabLayout indicator is also displayed
        onView(withId(R.id.tab_indicator))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRecyclerViewScrolling() {
        // Launch HomePageFragment without Hilt
        launchFragmentInContainer<HomePageFragment>(themeResId = R.style.Theme_Foodie)

        // Scroll to the first item in the RecyclerView
        onView(withId(R.id.recycler_view_stalls))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testNavigationToMenuFragment() {
        // Launch HomePageFragment without Hilt
        launchFragmentInContainer<HomePageFragment>(themeResId = R.style.Theme_Foodie)

        // Click on a RecyclerView item to navigate to MenuFragment
        // Assuming there are items in RecyclerView, otherwise this test will fail.
        onView(withId(R.id.recycler_view_stalls))
            .perform(click())

        // Verify that the MenuFragment is displayed (using some unique view ID from MenuFragment)
        onView(withId(R.id.menuFragment))
            .check(matches(isDisplayed()))
    }
}
