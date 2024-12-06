package com.example.foodie.views

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.foodie.R
import org.junit.Test
import org.junit.runner.RunWith
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.action.ViewActions.scrollTo
import org.mockito.Mockito
import org.hamcrest.CoreMatchers.containsString // Import containsString from Hamcrest

@RunWith(AndroidJUnit4::class)
class MenuFragmentTest {

    @Test
    fun testMenuCategoriesAreDisplayed() {
        // Launch MenuFragment in a container
        val scenario = launchFragmentInContainer<MenuFragment>(themeResId = R.style.Theme_Foodie)

        // Check that the RecyclerView for the menu is displayed
        onView(withId(R.id.recyclerViewMenu))
            .check(matches(isDisplayed()))

        // Assuming a category item is available, scroll to it and check it's displayed
        onView(withId(R.id.recyclerViewMenu))
            .perform(scrollTo())
            .check(matches(hasDescendant(withText("Category Name")))) // Replace with actual category name
    }

    @Test
    fun testAddItemToCart() {
        // Launch MenuFragment in a container
        val scenario = launchFragmentInContainer<MenuFragment>(themeResId = R.style.Theme_Foodie)

        // Assuming that the add button for a menu item is present, click it
        onView(withId(R.id.btnAdd)) // Replace `add_button` with the actual ID in your layout
            .perform(click())

        // Check that the cart summary is updated
        onView(withId(R.id.tvTotalItems))
            .check(matches(withText(containsString("1 Item"))))

        // Check that the cart layout container is now visible
        onView(withId(R.id.cart_layout_container))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRemoveItemFromCart() {
        // Launch MenuFragment in a container
        val scenario = launchFragmentInContainer<MenuFragment>(themeResId = R.style.Theme_Foodie)

        // Assuming the add button is clicked first to add an item
        onView(withId(R.id.btnAdd)) // Replace `add_button` with the actual ID
            .perform(click())

        // Click minus button to remove item
        onView(withId(R.id.btnMinus)) // Replace `minus_button` with the actual ID
            .perform(click())

        // Verify that the cart is now empty and the container is gone
        onView(withId(R.id.cart_layout_container))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun testNavigateToCheckoutFragment() {
        // Mock the NavController
        val mockNavController = Mockito.mock(NavController::class.java)

        // Launch MenuFragment and set the NavController
        val scenario = launchFragmentInContainer<MenuFragment>(themeResId = R.style.Theme_Foodie)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }

        // Assuming there are items in the cart
        onView(withId(R.id.btnAdd)) // Replace `add_button` with the actual ID
            .perform(click())

        // Click on the cart layout container to navigate to CheckoutFragment
        onView(withId(R.id.cart_layout_container))
            .perform(click())

        // Verify that navigation to the CheckoutFragment was triggered
        Mockito.verify(mockNavController).navigate(
            MenuFragmentDirections.actionMenuFragmentToCheckoutFragment(
                cartData = arrayOf(), // Replace with mock data if necessary
                stallId = "",
                stallName = ""
            )
        )
    }
}

