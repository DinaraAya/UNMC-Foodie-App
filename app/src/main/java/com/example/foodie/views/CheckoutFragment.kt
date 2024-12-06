package com.example.foodie.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodie.adapters.CheckoutAdapter
import com.example.foodie.databinding.FragmentCheckoutBinding
import com.example.foodie.models.CartItemData
import com.example.foodie.viewmodels.CheckoutViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth

/**
 * Fragment responsible for displaying the checkout process,
 * including cart items, total cost, and the ability to place an order.
 */
open class CheckoutFragment : Fragment() {

    // Binding to the fragment layout (FragmentCheckoutBinding)
    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    // ViewModel instance to manage checkout logic
    private val viewModel: CheckoutViewModel by viewModels()

    // Arguments passed to the fragment, containing cart data and stall info
    private val args: CheckoutFragmentArgs by navArgs()

    /**
     * Inflates the layout for the fragment and sets up the UI components,
     * such as the checkout title and the place order button.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        // Set the title of the checkout screen
        binding.include.arrowText.text = "CHECKOUT"

        // Setup the place order button functionality
        setupPlaceOrderButton()

        return binding.root
    }

    /**
     * Sets up the RecyclerView and observes changes in the ViewModel's LiveData
     * for cart items and order number.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up navigation to the previous screen when the back arrow is clicked
        binding.include.arrow.setOnClickListener {
            findNavController().navigateUp()
        }

        // Set up RecyclerView to display cart items
        val adapter = CheckoutAdapter()
        binding.itemViewCheckout.layoutManager = LinearLayoutManager(requireContext())
        binding.itemViewCheckout.adapter = adapter

        // Observe changes to cart items and update the RecyclerView when data changes
        viewModel.cartItems.observe(viewLifecycleOwner, Observer { cartData ->
            cartData?.let {
                adapter.submitList(it.map { entry -> entry.key to entry.value }) // Convert Map to List<Pair<String, CartItemData>>
            }
        })

        // Observe changes to the order number and navigate to the status screen when it's updated
        viewModel.orderNumber.observe(viewLifecycleOwner, Observer { orderNumber ->
            orderNumber?.let {
                // Navigate to the status fragment with the order number
                val action = CheckoutFragmentDirections.actionCheckoutFragmentToStatusFragment(it, "Order Placed")
                findNavController().navigate(action)

                // Clear the order number after navigation
                viewModel.clearOrderNumber()
            }
        })

        // Set the cart items in ViewModel if not already set
        if (viewModel.cartItems.value == null) {
            val cartDataMap = args.cartData.associateBy { it.itemName }
            viewModel.setCartItems(cartDataMap)
        }
    }

    /**
     * Sets up the place order button click listener, which triggers order placement and cart clearing.
     */
    private fun setupPlaceOrderButton() {
        binding.placeOrderButton.setOnClickListener {
            // Get user ID from Firebase Authentication (ensure the user is logged in)
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val stallId = args.stallId
            val stallName = args.stallName
            val totalItems = calculateTotalItems()
            val totalCost = calculateTotalCost()
            val items = args.cartData.associateBy { it.itemName }.toList()

            // Place the order by calling the ViewModel method
            viewModel.placeOrder(
                userId = userId,
                stallId = stallId,
                stallName = stallName,
                totalItems = totalItems,
                totalCost = totalCost,
                items = items,
                onFailure = { e ->
                    // Show an error message and log the error if the order placement fails
                    Toast.makeText(requireContext(), "Failure to place order", Toast.LENGTH_SHORT).show()
                    Log.e("CheckoutFragment", "Error placing order", e)
                },
                onSuccess = {
                    // Clear the cart after placing the order successfully
                    viewModel.clearCart(userId, stallId,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Cart cleared successfully!", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { e ->
                            // Show an error message and log the error if cart clearing fails
                            Toast.makeText(requireContext(), "Failed to clear cart", Toast.LENGTH_SHORT).show()
                            Log.e("CheckoutFragment", "Error clearing cart", e)
                        }
                    )
                }
            )
        }
    }

    /**
     * Calculates the total number of items in the cart by summing the quantities.
     */
    private fun calculateTotalItems(): Int {
        return args.cartData.sumOf { it.quantity }
    }

    /**
     * Calculates the total cost of the cart by summing the price of each item multiplied by its quantity.
     */
    private fun calculateTotalCost(): Double {
        return args.cartData.sumOf { it.price * it.quantity }
    }

    /**
     * Cleans up the binding when the view is destroyed to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






