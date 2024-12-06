package com.example.foodie.views

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.foodie.R
import com.example.foodie.models.CartItemData
import com.example.foodie.viewmodels.CheckoutViewModel
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.platform.app.InstrumentationRegistry
import androidx.fragment.app.FragmentActivity

@MediumTest
@RunWith(AndroidJUnit4::class)
class CheckoutFragmentTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private class TestCheckoutViewModel : CheckoutViewModel() {
        override val cartItems = MutableLiveData<Map<String, CartItemData>>()
        override val orderNumber = MutableLiveData<Int>()

        var placeOrderCalled = false
        var clearCartCalled = false

        init {
            cartItems.value = mapOf(
                "Item 1" to CartItemData("Item 1", 2, 10.0),
                "Item 2" to CartItemData("Item 2", 1, 20.0)
            )
        }

        override fun placeOrder(
            userId: String,
            stallId: String,
            stallName: String,
            totalItems: Int,
            totalCost: Double,
            items: List<Pair<String, CartItemData>>,
            onFailure: (Exception) -> Unit,
            onSuccess: () -> Unit
        ) {
            placeOrderCalled = true
            orderNumber.value = 12345
            onSuccess()
        }

        override fun clearCart(
            userId: String,
            stallId: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            clearCartCalled = true
            cartItems.value = emptyMap()
            onSuccess()
        }

        override fun clearOrderNumber() {
            orderNumber.value = null
        }
    }

    private lateinit var viewModel: TestCheckoutViewModel
    private lateinit var navController: NavController
    private lateinit var activityScenario: ActivityScenario<EmptyFragmentActivity>

    // Empty activity for testing fragments
    class EmptyFragmentActivity : FragmentActivity()

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        viewModel = TestCheckoutViewModel()
        navController = NavController(context)

        // Launch empty activity to host our fragment
        val startActivityIntent = Intent.makeMainActivity(
            ComponentName(
                ApplicationProvider.getApplicationContext(),
                EmptyFragmentActivity::class.java
            )
        ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        activityScenario = ActivityScenario.launch(startActivityIntent)

        activityScenario.onActivity { activity ->
            // Add the fragment into the activity
            activity.supportFragmentManager.beginTransaction()
                .add(android.R.id.content, CheckoutFragment(), "CheckoutFragment")
                .commitNow()

            // Get the fragment
            val fragment = activity.supportFragmentManager.findFragmentByTag("CheckoutFragment")

            // Inject view model
            val field = CheckoutFragment::class.java.getDeclaredField("viewModel")
            field.isAccessible = true
            field.set(fragment, viewModel)

            // Set up navigation
            Navigation.setViewNavController(fragment!!.requireView(), navController)
        }
    }

    @Test
    fun testRecyclerViewDisplaysCartItems() {
        // Verify RecyclerView is displayed
        onView(withId(R.id.item_view_checkout))
            .check(matches(isDisplayed()))

        // Verify first item is displayed
        onView(withId(R.id.item_view_checkout))
            .check(matches(hasDescendant(withText("Item 1"))))
            .check(matches(hasDescendant(withText("$20.00"))))

        // Scroll to and verify second item
        onView(withId(R.id.item_view_checkout))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
            .check(matches(hasDescendant(withText("Item 2"))))
            .check(matches(hasDescendant(withText("$20.00"))))
    }

    @Test
    fun testPlaceOrderButtonClickNavigatesToStatusFragment() {
        onView(withId(R.id.place_order_button))
            .check(matches(isDisplayed()))
            .perform(click())

        assert(viewModel.placeOrderCalled) { "Place order was not called" }
        assert(viewModel.orderNumber.value == 12345) { "Order number was not set correctly" }
    }

    @Test
    fun testCheckoutTitleIsDisplayed() {
        onView(withId(R.id.arrow_text))
            .check(matches(isDisplayed()))
            .check(matches(withText("CHECKOUT")))
    }

    @Test
    fun testBackArrowNavigation() {
        onView(withId(R.id.arrow))
            .check(matches(isDisplayed()))
            .perform(click())
    }
}

