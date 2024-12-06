package com.example.foodie.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodie.adapters.OrderHistoryAdapter
import com.example.foodie.databinding.FragmentOrderHistoryBinding
import com.example.foodie.models.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class OrderHistoryFragment: Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private val orderList = mutableListOf<Order>()

    /**
     * onCreateView is called to inflate the fragment's view and set up initial configurations.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        // Set up RecyclerView to display order history
        setupRecyclerView()

        // Load orders from Firestore
        loadOrders()

        // Open drawer when hamburger icon is clicked
        binding.include.hamburgerIcon.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        return binding.root
    }

    /**
     * onViewCreated is called after the view has been created, and it's used to configure UI elements.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the arrow_text to "ORDER HISTORY"
        binding.include.arrowText.text = "ORDER HISTORY"

        // Set up arrow click listener to open the drawer
        binding.include.arrow.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }
    }

    /**
     * Configures the RecyclerView with an adapter and layout manager.
     */
    private fun setupRecyclerView() {
        orderHistoryAdapter = OrderHistoryAdapter(orderList) // Create the adapter with the order list
        binding.recyclerOrderHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())  // Use a vertical layout manager
            adapter = orderHistoryAdapter // Set the adapter for the RecyclerView
        }
    }

    /**
     * Loads the orders from Firestore for the currently authenticated user.
     * The orders are filtered by userId, and the list is updated when data is fetched.
     */
    private fun loadOrders() {
        // Get the current user's ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Fetch the user's orders from Firestore
        firestore.collection("orders")
            .whereEqualTo("userId", userId)  // Filter orders by userId
            .get()
            .addOnSuccessListener { snapshot ->
                orderList.clear() // Clear the previous order list
                // Iterate through the snapshot and add each order to the list
                for(document in snapshot) {
                    val order = document.toObject(Order::class.java)
                    orderList.add(order)
                }
                // Notify the adapter that the data has changed and it needs to update
                orderHistoryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Log any error that occurs while fetching the orders
                    Log.e("OrderHistoryFragment", "Error loading orders", exception)
            }

    }


}