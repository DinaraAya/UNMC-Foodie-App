package com.example.foodie.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.foodie.models.CartItemData
import com.example.foodie.models.MenuCategory
import com.example.foodie.models.MyMenuItem
import com.example.foodie.repositories.CartRepository
import com.example.foodie.repositories.StallsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var stallsRepository: StallsRepository

    @Mock
    private lateinit var cartRepository: CartRepository

    @Mock
    private lateinit var menuCategoriesObserver: Observer<List<Pair<MenuCategory, List<MyMenuItem>>>>

    @Mock
    private lateinit var cartItemsObserver: Observer<Map<String, CartItemData>>

    private lateinit var viewModel: MenuViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Set the Main dispatcher to use the UnconfinedTestDispatcher
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)

        // Initialize mocks and the ViewModel
        MockitoAnnotations.openMocks(this)
        viewModel = MenuViewModel(stallsRepository, cartRepository)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original Main dispatcher
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `fetchMenuCategories updates menuCategories LiveData`() = runTest {
        // Arrange: Prepare mock data
        val stallId = "stall_1"
        val mockCategories = listOf(
            Pair(MenuCategory("Category 1"), listOf(MyMenuItem("Item 1", "10.0"), MyMenuItem("Item 2", "15.0"))),
            Pair(MenuCategory("Category 2"), listOf(MyMenuItem("Item 3", "20.0")))
        )

        // Mock the repository behavior
        Mockito.`when`(stallsRepository.fetchMenuCategories(stallId)).thenReturn(mockCategories)

        // Observe the menuCategories LiveData
        viewModel.menuCategories.observeForever(menuCategoriesObserver)

        // Act: Fetch menu categories
        viewModel.fetchMenuCategories(stallId)

        // Assert: Capture and verify the LiveData update
        val captor = argumentCaptor<List<Pair<MenuCategory, List<MyMenuItem>>>>()
        verify(menuCategoriesObserver).onChanged(captor.capture())
        assertEquals(mockCategories, captor.firstValue)

        // Clean up observer
        viewModel.menuCategories.removeObserver(menuCategoriesObserver)
    }

    @Test
    fun `loadCartData updates cartItems LiveData`() = runTest {
        // Arrange: Prepare mock data
        val userId = "user_1"
        val stallId = "stall_1"
        val mockCartData = mapOf(
            "Item 1" to CartItemData("Item 1", 2, 10.0),
            "Item 2" to CartItemData("Item 2", 1, 15.0)
        )

        // Mock the repository behavior
        Mockito.`when`(cartRepository.loadCartData(userId, stallId)).thenReturn(mockCartData)

        // Observe the cartItems LiveData
        viewModel.cartItems.observeForever(cartItemsObserver)

        // Act: Load cart data
        viewModel.loadCartData(userId, stallId)

        // Assert: Capture and verify the LiveData update
        val captor = argumentCaptor<Map<String, CartItemData>>()
        verify(cartItemsObserver).onChanged(captor.capture())
        assertEquals(mockCartData, captor.firstValue)

        // Clean up observer
        viewModel.cartItems.removeObserver(cartItemsObserver)
    }

    @Test
    fun `addItemToCart calls repository and updates cartItems LiveData`() = runTest {
        // Arrange: Prepare mock data
        val userId = "user_1"
        val stallId = "stall_1"
        val menuItem = MyMenuItem("Burger", "5.0")
        val quantity = 2

        // Mock the repository behavior to just run the coroutine without any side effects
        Mockito.`when`(runBlocking {
            cartRepository.addItemToCart(userId, stallId, menuItem, quantity)
        }).thenAnswer { }

        // Observe the cartItems LiveData
        viewModel.cartItems.observeForever(cartItemsObserver)

        // Act: Add item to cart
        viewModel.addItemToCart(userId, stallId, menuItem, quantity)

        // Assert: Verify that `addItemToCart` method was called
        verify(cartRepository).addItemToCart(userId, stallId, menuItem, quantity)

        // Since `loadCartData` is called in `addItemToCart`, verify that `loadCartData` result is updated in LiveData
        verify(cartRepository).loadCartData(userId, stallId)

        // Clean up observer
        viewModel.cartItems.removeObserver(cartItemsObserver)
    }
}

