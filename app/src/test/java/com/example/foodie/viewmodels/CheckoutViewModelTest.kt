package com.example.foodie.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.foodie.models.CartItemData
import com.example.foodie.repositories.OrderRepository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CheckoutViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // To run LiveData synchronously

    private lateinit var viewModel: CheckoutViewModel
    private val mockRepository = mockk<OrderRepository>(relaxed = true)
    private val mockFirestore = mockk<FirebaseFirestore>(relaxed = true)
    private val cartObserver = mockk<Observer<Map<String, CartItemData>>>(relaxed = true)
    private val orderNumberObserver = mockk<Observer<Int?>>(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(FirebaseApp::class)
        mockkStatic(FirebaseFirestore::class)

        val mockFirebaseApp = mockk<FirebaseApp>(relaxed = true)
        every { FirebaseApp.getInstance() } returns mockFirebaseApp
        every { FirebaseFirestore.getInstance() } returns mockFirestore

        // Initialize ViewModel
        viewModel = CheckoutViewModel(mockRepository).apply {
            cartItems.observeForever(cartObserver)
            orderNumber.observeForever(orderNumberObserver)
        }
    }

    @Test
    fun `setCartItems updates cartItems LiveData`() {
        val cartData = mapOf("item1" to CartItemData("item1", 2, 20.0))
        viewModel.setCartItems(cartData)
        verify { cartObserver.onChanged(cartData) }
        assertEquals(cartData, viewModel.cartItems.value)
    }

    @Test
    fun `placeOrder triggers repository and updates orderNumber`() {
        val userId = "testUser"
        val stallId = "stall1"
        val stallName = "Test Stall"
        val totalItems = 3
        val totalCost = 45.0
        val cartItems = listOf("item1" to CartItemData("item1", 2, 20.0))
        val successCallback = mockk<() -> Unit>(relaxed = true)
        val failureCallback = mockk<(Exception) -> Unit>(relaxed = true)

        every { mockRepository.createOrder(any(), any(), any()) } answers {
            secondArg<() -> Unit>().invoke()
        }

        viewModel.placeOrder(
            userId = userId,
            stallId = stallId,
            stallName = stallName,
            totalItems = totalItems,
            totalCost = totalCost,
            items = cartItems,
            onSuccess = successCallback,
            onFailure = failureCallback
        )

        verify { mockRepository.createOrder(any(), any(), any()) }
        verify { successCallback.invoke() }
        assert(viewModel.orderNumber.value in 1000..9999)
    }

    @Test
    fun `clearCart clears cart items`() {
        val userId = "testUser"
        val stallId = "stall1"
        val successCallback = mockk<() -> Unit>(relaxed = true)
        val failureCallback = mockk<(Exception) -> Unit>(relaxed = true)

        val mockTask = mockk<com.google.android.gms.tasks.Task<Void>>()
        val mockDocumentRef = mockk<com.google.firebase.firestore.DocumentReference>()

        every {
            mockFirestore.collection("customers").document(userId).collection("cart").document(stallId)
        } returns mockDocumentRef

        every { mockDocumentRef.update("items", emptyMap<String, Any>()) } returns mockTask

        every { mockTask.addOnSuccessListener(any()) } answers {
            firstArg<OnSuccessListener<Void>>().onSuccess(null)
            mockTask
        }

        every { mockTask.addOnFailureListener(any()) } returns mockTask

        viewModel.clearCart(userId, stallId, successCallback, failureCallback)

        verify { mockDocumentRef.update("items", emptyMap<String, Any>()) }
        verify { successCallback.invoke() }
        verify(exactly = 0) { failureCallback.invoke(any()) }
    }

    @Test
    fun `clearOrderNumber sets orderNumber LiveData to null`() {
        viewModel.clearOrderNumber()
        verify { orderNumberObserver.onChanged(null) }
        assertEquals(null, viewModel.orderNumber.value)
    }
}


