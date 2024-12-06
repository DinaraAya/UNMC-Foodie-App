package com.example.foodie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodie.models.CartItemData
import com.example.foodie.models.MenuCategory
import com.example.foodie.models.MyMenuItem
import com.example.foodie.repositories.CartRepository
import com.example.foodie.repositories.StallsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * ViewModel class for managing the menu data and cart items for a specific stall.
 * It handles interactions with the repositories to fetch menu categories, load cart data, and update the cart.
 */
@HiltViewModel
class MenuViewModel @Inject constructor(
    private val stallsRepository: StallsRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    // LiveData to hold the menu categories and items for the specific stall
    private val _menuCategories = MutableLiveData<List<Pair<MenuCategory, List<MyMenuItem>>>>()
    val menuCategories: LiveData<List<Pair<MenuCategory, List<MyMenuItem>>>> get() = _menuCategories

    // LiveData to hold the cart items for the specific user and stall
    private val _cartItems = MutableLiveData<Map<String, CartItemData>>()
    val cartItems: LiveData<Map<String, CartItemData>> get() = _cartItems

    /**
     * Fetches the menu categories and items for a specific stall and updates the [menuCategories] LiveData.
     *
     * @param stallId The ID of the stall for which to fetch the menu categories.
     */
    fun fetchMenuCategories(stallId: String) {
        // Launch a coroutine to fetch menu categories from the repository
        viewModelScope.launch {
            val categories = stallsRepository.fetchMenuCategories(stallId)
            // Update the LiveData with the fetched categories
            _menuCategories.value = categories
        }
    }

    /**
     * Loads the cart data for a specific user and stall and updates the [cartItems] LiveData.
     *
     * @param userId The ID of the user whose cart data is to be fetched.
     * @param stallId The ID of the stall for which to fetch the cart data.
     */
    fun loadCartData(userId: String, stallId: String) {
        // Launch a coroutine to fetch cart data from the repository
        viewModelScope.launch {
            val cartData = cartRepository.loadCartData(userId, stallId)
            // Update the LiveData with the fetched cart items
            _cartItems.value = cartData
        }
    }

    /**
     * Adds an item to the cart for a specific user and stall and refreshes the cart data.
     *
     * @param userId The ID of the user to which the item will be added.
     * @param stallId The ID of the stall from which the item will be added.
     * @param menuItem The menu item to be added to the cart.
     * @param quantity The quantity of the menu item to be added.
     */
    fun addItemToCart(userId: String, stallId: String, menuItem: MyMenuItem, quantity: Int) {
        // Launch a coroutine to add the item to the cart
        viewModelScope.launch {
            cartRepository.addItemToCart(userId, stallId, menuItem, quantity)
            // Refresh cart data after adding the item
            loadCartData(userId, stallId)
        }
    }
}




