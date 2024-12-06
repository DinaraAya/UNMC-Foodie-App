package com.example.foodie.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.foodie.models.Order
import com.example.foodie.repositories.OrderRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class TrackOrderViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var ordersObserver: Observer<List<Order>>

    private lateinit var viewModel: TrackOrderViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Set up the Main dispatcher with a TestDispatcher
        Dispatchers.setMain(testDispatcher)

        // Mock initialization
        MockitoAnnotations.openMocks(this)

        // Create ViewModel with the mocked repository
        viewModel = TrackOrderViewModel(orderRepository)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to its original state
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchOrders updates LiveData with orders`() = runTest {
        // Arrange: Mock the repository response
        val mockOrders = listOf(
            Order(orderNumber = 1, status = "In the Kitchen", stallName = "JAPANESE"),
            Order(orderNumber = 2, status = "Order Ready", stallName = "CHINESE")
        )
        `when`(orderRepository.fetchPendingOrders()).thenReturn(mockOrders)

        // Observe the orders LiveData
        viewModel.orders.observeForever(ordersObserver)

        // Act: Fetch orders
        viewModel.fetchOrders()
        advanceUntilIdle()

        // Assert: Verify LiveData is updated with the mock orders
        verify(ordersObserver).onChanged(mockOrders)
        assertEquals(mockOrders, viewModel.orders.value)

        // Clean up
        viewModel.orders.removeObserver(ordersObserver)
    }
}

