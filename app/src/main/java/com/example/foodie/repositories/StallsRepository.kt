package com.example.foodie.repositories

import android.util.Log
import com.example.foodie.models.MenuCategory
import com.example.foodie.models.MyMenuItem
import com.example.foodie.models.Stalls
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Repository for managing stall and menu data from Firestore.
 * Provides methods for fetching stalls and their associated menu categories and items.
 */
class StallsRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    /**
     * Fetches all stalls from the Firestore "stalls" collection.
     *
     * @return A list of [Stalls] objects, each representing a stall.
     */
    suspend fun fetchStalls(): List<Stalls> {
        val stallList = mutableListOf<Stalls>()
        try {
            // Query Firestore to retrieve all stall documents
            val documents = firestore.collection("stalls").get().await()
            for (document in documents) {
                val stall = document.toObject(Stalls::class.java)
                stall.id = document.id // Assign the document ID as the stall ID
                stallList.add(stall) // Add the stall to the list
            }
        } catch (e: Exception) {
            // Log any errors that occur during the fetch operation
            Log.e("StallsRepository", "Error fetching stalls", e)
        }
        return stallList
    }

    /**
     * Fetches menu categories and their items for a specific stall.
     *
     * @param stallId The ID of the stall whose menu is to be fetched.
     * @return A list of pairs where each pair consists of a [MenuCategory] and its associated list of [MyMenuItem].
     */
    suspend fun fetchMenuCategories(stallId: String): List<Pair<MenuCategory, List<MyMenuItem>>> {
        val categoriesWithItems = mutableListOf<Pair<MenuCategory, List<MyMenuItem>>>()
        try {
            // Query Firestore to retrieve menu categories for the given stall
            val categorySnapshot = firestore.collection("stalls").document(stallId).collection("menu").get().await()
            for (categoryDoc in categorySnapshot) {
                val category = categoryDoc.toObject(MenuCategory::class.java)
                // Retrieve items within the category
                val itemsSnapshot = categoryDoc.reference.collection("items").get().await()
                val items = itemsSnapshot.toObjects(MyMenuItem::class.java)
                categoriesWithItems.add(category to items)
            }
        } catch (e: Exception) {
            // Log any errors that occur during the fetch operation
            Log.e("StallsRepository", "Error fetching menu categories", e)
        }
        return categoriesWithItems
    }
}

