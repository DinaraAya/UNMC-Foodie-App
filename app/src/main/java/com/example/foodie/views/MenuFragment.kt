package com.example.foodie.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodie.adapters.MenuAdapter
import com.example.foodie.databinding.FragmentMenuBinding
import com.example.foodie.models.CartItemData
import com.example.foodie.models.MenuCategory
import com.example.foodie.models.MyMenuItem
import com.example.foodie.models.Stalls
import com.example.foodie.viewmodels.MenuViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private val viewModel: MenuViewModel by viewModels()
    private lateinit var menuAdapter: MenuAdapter
    private var currentCartData = mutableMapOf<String, CartItemData>()

    /**
     * onCreateView is called to inflate the fragment's view and set up initial configurations.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)

        // Get selected stall data and authenticated user information
        val selectedStall = MenuFragmentArgs.fromBundle(requireArguments()).selectedStall
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: throw IllegalStateException("User not authenticated")

        // Fetch categories and cart data via ViewModel
        selectedStall?.let { stall ->
            viewModel.fetchMenuCategories(stall.id) // Fetch menu categories from ViewModel
            viewModel.loadCartData(userId, stall.id) // Load existing cart data for the user from Firestore
            binding.include.arrowText.text = stall.name // Display the stall name on the UI
        }

        // Set RecyclerView layout manager
        binding.recyclerViewMenu.layoutManager = LinearLayoutManager(requireContext())

        // Set RecyclerView layout manager
        setupObservers()
        setupListeners(selectedStall)

        return binding.root
    }

    /**
     * Sets up observers to listen for changes in menu categories and cart data.
     */
    private fun setupObservers() {
        // Observe changes in the menu categories and update the adapter
        viewModel.menuCategories.observe(viewLifecycleOwner) { categoriesWithItems ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@observe
            val stallId = MenuFragmentArgs.fromBundle(requireArguments()).selectedStall.id
            setupAdapter(categoriesWithItems, userId, stallId)
        }

        // Observe changes in the cart items
        viewModel.cartItems.observe(viewLifecycleOwner) { cartData ->
            currentCartData = cartData.toMutableMap() // Update the cart data
            updateCartSummary() // Recalculate and display the updated cart summary
        }
    }

    /**
     * Sets up the click listeners for UI components (e.g., arrow, cart).
     */
    private fun setupListeners(selectedStall: Stalls?) {
        // Navigate up when the arrow is clicked
        binding.include.arrow.setOnClickListener {
            findNavController().navigateUp()
        }

        // Navigate to checkout page when the cart layout is clicked
        binding.cartLayoutContainer.setOnClickListener {
            val cartDataArray = currentCartData.values.toTypedArray()
            val action = MenuFragmentDirections.actionMenuFragmentToCheckoutFragment(
                cartData = cartDataArray,
                stallId = selectedStall?.id ?: "",
                stallName = selectedStall?.name ?: ""
            )
            findNavController().navigate(action)
        }
    }

    /**
     * Sets up the adapter for the RecyclerView to display the menu items.
     */
    private fun setupAdapter(
        categoriesWithItems: List<Pair<MenuCategory, List<MyMenuItem>>>,
        userId: String,
        stallId: String
    ) {
        menuAdapter = MenuAdapter(categoriesWithItems, currentCartData.mapValues { it.value.quantity }) { menuItem, quantity ->
            // When an item is added/removed from the cart, update the cart and ViewModel
            viewModel.addItemToCart(userId, stallId, menuItem, quantity)
            currentCartData[menuItem.itemName] = CartItemData(menuItem.itemName, quantity, menuItem.itemPrice.toDoubleOrNull() ?: 0.0)
            if (quantity == 0) {
                currentCartData.remove(menuItem.itemName)
            }
            updateCartSummary() // Update the cart summary UI
        }
        binding.recyclerViewMenu.adapter = menuAdapter // Set the adapter to RecyclerView
    }

    /**
     * Updates the cart summary (total items and total cost).
     */
    private fun updateCartSummary() {
        var totalItems = 0
        var totalCost = 0.0

        // Calculate total items and total cost based on the current cart data
        currentCartData.forEach { (_, itemData) ->
            totalItems += itemData.quantity
            totalCost += itemData.price * itemData.quantity
        }

        // Update the UI elements based on the cart data
        if (totalItems > 0) {
            binding.tvTotalItems.text = "$totalItems Items"
            binding.tvTotalCost.text = "$${String.format("%.2f", totalCost)}"
            binding.cartLayoutContainer.visibility = View.VISIBLE // Show cart summary
        } else {
            binding.cartLayoutContainer.visibility = View.GONE // Hide cart summary if no items in the cart
        }
    }
}









