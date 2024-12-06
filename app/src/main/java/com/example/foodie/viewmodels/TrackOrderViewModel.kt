package com.example.foodie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodie.models.Order
import com.example.foodie.repositories.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for tracking customer orders.
 * It fetches the list of pending orders from the [OrderRepository] and exposes it to the UI.
 */
@HiltViewModel
class TrackOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    // LiveData to hold the list of orders
    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    // Initialize and load the list of orders when the ViewModel is created
    init {
        fetchOrders()
    }

    /**
     * Fetches the list of pending orders from the [OrderRepository] and updates the [LiveData] with the result.
     */
    fun fetchOrders() {
        // Launch a coroutine to fetch the orders data
        viewModelScope.launch {
            // Get the list of pending orders from the repository
            val fetchedOrders = orderRepository.fetchPendingOrders()
            // Update the LiveData with the fetched list of orders
            _orders.value = fetchedOrders
        }
    }
}
