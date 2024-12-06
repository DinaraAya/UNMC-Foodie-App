package com.example.foodie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodie.models.CartItemData
import com.example.foodie.repositories.OrderRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.example.foodie.models.Order
import kotlin.random.Random

/**
 * ViewModel for managing the checkout process, including placing an order and clearing the cart.
 * Interacts with the [OrderRepository] to create and manage orders.
 */
open class CheckoutViewModel (
    private val repository: OrderRepository = OrderRepository()
) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // LiveData to hold the cart items selected by the user
    private val _cartItems = MutableLiveData<Map<String, CartItemData>>()
    open val cartItems: LiveData<Map<String, CartItemData>> get() = _cartItems

    private val _orderNumber = MutableLiveData<Int?>() // Make it nullable
    open val orderNumber: LiveData<Int?> get() = _orderNumber

    /**
     * Sets the cart items in the ViewModel.
     * This method is called when the cart data is loaded or updated.
     *
     * @param items The map of cart items to be set in the ViewModel.
     */
    fun setCartItems(items: Map<String, CartItemData>) {
        _cartItems.value = items
    }

    /**
     * Places an order by creating an Order object and saving it in Firestore.
     *
     * @param userId The ID of the user placing the order.
     * @param stallId The ID of the stall from which the items are ordered.
     * @param stallName The name of the stall from which the items are ordered.
     * @param totalItems The total number of items in the cart.
     * @param totalCost The total cost of the items in the cart.
     * @param items A list of items (name and data) to be included in the order.
     * @param onFailure A callback to handle failures during order placement.
     * @param onSuccess A callback to handle the success of the order placement.
     */
    open fun placeOrder(
        userId: String,
        stallId: String,
        stallName: String,
        totalItems: Int,
        totalCost: Double,
        items: List<Pair<String, CartItemData>>, // Use List<Pair<String, CartItemData>> type here
        onFailure: (Exception) -> Unit,
        onSuccess: () -> Unit
    ) {
        // Generate a random 4-digit order number
        val orderNumber = Random.nextInt(1000, 9999) // Generate a random 4-digit order number

        // Create an Order object with the provided information
        val order = Order(
            userId = userId,
            stallId = stallId,
            stallName = stallName,
            totalItems = totalItems,
            totalCost = totalCost,
            items = items.map { it.second },
            timestamp = Timestamp.now(), // Set the current timestamp
            status = "Order Placed", // Initial order status
            orderNumber = orderNumber // Set the generated order number
        )

        // Call the repository to create the order in Firestore
        repository.createOrder(order,
            onSuccess = {
                // On success, set the order number in LiveData and trigger the success callback
                _orderNumber.value = orderNumber
                onSuccess()
            },
            onFailure = onFailure // Pass the failure callback in case of an error
        )
    }

    /**
     * Clears the cart for a specific user and stall by emptying the "items" field in Firestore.
     *
     * @param userId The ID of the user whose cart is to be cleared.
     * @param stallId The ID of the stall whose cart is to be cleared.
     * @param onSuccess A callback to be triggered upon successful cart clearance.
     * @param onFailure A callback to handle failures during cart clearance.
     */
    open fun clearCart(userId: String, stallId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Reference to the cart document for the specified user and stall
        val cartRef = firestore.collection("customers").document(userId)
            .collection("cart").document(stallId)

        // Update the "items" field to an empty map, effectively clearing the cart
        cartRef.update("items", emptyMap<String, Any>())
            .addOnSuccessListener { onSuccess() } // Trigger success callback on successful update
            .addOnFailureListener { exception -> onFailure(exception) } // Trigger failure callback on error
    }

    /**
     * Clears the order number stored in the ViewModel.
     * This is typically called after the order has been placed or if the user navigates away from the checkout.
     */
    open fun clearOrderNumber() {
        _orderNumber.value = null
    }
}


