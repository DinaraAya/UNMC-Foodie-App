package com.example.foodie.repositories

import android.util.Log
import com.example.foodie.models.CartItemData
import com.example.foodie.models.MyMenuItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository for managing cart operations in Firestore.
 * Handles loading, adding, and clearing cart items for a specific user and stall.
 */
class CartRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    /**
     * Loads the cart data for a specific user and stall from Firestore.
     *
     * @param userId The ID of the user.
     * @param stallId The ID of the stall.
     * @return A map where the key is the item name and the value is the associated cart item data.
     */
    suspend fun loadCartData(userId: String, stallId: String): Map<String, CartItemData> {
        val cartData = mutableMapOf<String, CartItemData>()

        try {
            // Fetch the cart document for the given user and stall
            val document = firestore.collection("customers").document(userId)
                .collection("cart").document(stallId).get().await()

            if (document.exists()) {
                // Extract items as a map and parse each item into CartItemData
                val itemsMap = document.get("items") as? Map<*, *>
                itemsMap?.forEach { (key, value) ->
                    val itemName = key as? String
                    val itemDetails = value as? Map<*, *>

                    // Ensure the item name and details are valid before adding to the cart data
                    if (itemName != null && itemDetails != null) {
                        val quantity = (itemDetails["quantity"] as? Long)?.toInt() ?: 0
                        val itemPrice = (itemDetails["itemPrice"] as? String)?.toDoubleOrNull() ?: 0.0
                        if (quantity > 0) {
                            cartData[itemName] = CartItemData(itemName, quantity, itemPrice)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Log any errors encountered while loading cart data
            Log.e("CartRepository", "Error loading cart data", e)
        }

        return cartData
    }

    /**
     * Adds an item to the cart for a specific user and stall.
     * If the cart document does not exist, it initializes a new document.
     *
     * @param userId The ID of the user.
     * @param stallId The ID of the stall.
     * @param menuItem The menu item to add to the cart.
     * @param quantity The quantity of the item to add.
     */
    suspend fun addItemToCart(userId: String, stallId: String, menuItem: MyMenuItem, quantity: Int) {
        val cartRef = firestore.collection("customers").document(userId).collection("cart").document(stallId)

        try {
            // Use a transaction to ensure atomic updates to the cart document
            firestore.runTransaction { transaction ->
                val cartSnapshot = transaction.get(cartRef)

                // Initialize a new cart document if it doesn't exist
                if (!cartSnapshot.exists()) {
                    transaction.set(cartRef, hashMapOf("items" to mutableMapOf<String, Map<String, Any>>()))
                }

                // Retrieve the existing items map or create a new one
                val itemsMap = cartSnapshot.get("items") as? MutableMap<String, Map<String, Any>> ?: mutableMapOf()
                val currentItemQuantity = itemsMap[menuItem.itemName]?.get("quantity") as? Int ?: 0

                // Update the item's quantity and add it back to the items map
                itemsMap[menuItem.itemName] = mapOf(
                    "itemName" to menuItem.itemName,
                    "itemPrice" to menuItem.itemPrice,
                    "quantity" to currentItemQuantity + quantity
                )

                // Commit the updated items map to Firestore
                transaction.update(cartRef, "items", itemsMap)
            }.await()
            Log.d("CartRepository", "Item added to cart successfully!")
        } catch (e: Exception) {
            // Log any errors encountered while adding the item to the cart
            Log.e("CartRepository", "Error adding item to cart", e)
        }
    }

    /**
     * Clears the cart for a specific user and stall by deleting the cart document.
     *
     * @param userId The ID of the user.
     * @param stallId The ID of the stall.
     */
    suspend fun clearCart(userId: String, stallId: String) {
        try {
            // Delete the cart document from Firestore
            firestore.collection("customers").document(userId).collection("cart")
                .document(stallId).delete().await()
            Log.d("CartRepository", "Cart cleared successfully!")
        } catch (e: Exception) {
            // Log any errors encountered while clearing the cart
            Log.e("CartRepository", "Error clearing cart", e)
        }
    }
}
