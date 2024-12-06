package com.example.foodie.repositories

import android.content.ContentValues.TAG
import android.util.Log
import com.example.foodie.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository for managing order operations in Firestore.
 * Handles creating new orders and fetching pending orders for the logged-in user.
 */
class OrderRepository @Inject constructor(){
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val ordersCollection = FirebaseFirestore.getInstance().collection("orders")

    /**
     * Creates a new order in the Firestore database.
     *
     * @param order The order to create.
     * @param onSuccess A callback function invoked when the order is created successfully.
     * @param onFailure A callback function invoked if an error occurs during order creation.
     */
    fun createOrder(order: Order, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val orderId = ordersCollection.document().id
        ordersCollection.document(orderId).set(order)
            .addOnSuccessListener {
                // Log success and invoke success callback
                Log.d("OrderRepository", "Order successfully created!")
                onSuccess()
            }
            .addOnFailureListener{ e ->
                // Log failure and invoke failure callback
                Log.e("OrderRepository", "Error creating order", e)
                onFailure(e)
            }
    }

    /**
     * Fetches pending orders for the currently logged-in user.
     * Pending orders are orders that do not have a "Completed" status.
     *
     * @return A list of pending orders, sorted by descending timestamp.
     */
    suspend fun fetchPendingOrders(): List<Order> {
        return try {
            // Retrieve the current user's ID
            val userId = auth.currentUser?.uid ?: run {
                Log.e(TAG, "No user logged in")
                return emptyList()
            }

            // Fetch orders for the user from Firestore
            val snapshot = ordersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Convert the snapshot to a list of Order objects, filtering and sorting them
            snapshot.toObjects<Order>()
                .filter { it.status != "Completed" }
                .sortedByDescending { it.timestamp }  // Sort orders by timestamp in descending order
        } catch (e: Exception) {
            // Log any errors encountered while fetching orders
            Log.e(TAG, "Error fetching orders", e)
            emptyList()
        }
    }

}