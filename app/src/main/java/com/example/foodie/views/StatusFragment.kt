package com.example.foodie.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foodie.databinding.FragmentStatusBinding
import com.example.foodie.R

class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null // Reference to the binding object for the fragment
    private val binding get() = _binding!! // Getter for binding, ensures null safety

    /**
     * onCreateView is called to inflate the fragment's layout and set up UI elements.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * onViewCreated is called after the view is created, where we can retrieve arguments
     * and set up any specific logic or UI updates.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve orderNumber and status from the arguments passed to the fragment
        val orderNumber = arguments?.getInt("orderNumber") ?: 0 // Default to 0 if no order number is passed
        val status = arguments?.getString("status") ?: "Order Placed"  // Default to "Order Placed" if no status is passed

        // Display the order number on the screen
        binding.orderNumber.text = "$orderNumber"

        // Set the checkboxes based on the order status
        when (status) {
            "Order Placed" -> {
                binding.orderPlacedCheckBox.isChecked = true
                binding.orderPreparingCheckBox.isChecked = false
                binding.orderReadyCheckBox.isChecked = false
            }
            "In the Kitchen" -> {
                binding.orderPlacedCheckBox.isChecked = true
                binding.orderPreparingCheckBox.isChecked = true
                binding.orderReadyCheckBox.isChecked = false
            }
            "Order Ready" -> {
                binding.orderPlacedCheckBox.isChecked = true
                binding.orderPreparingCheckBox.isChecked = true
                binding.orderReadyCheckBox.isChecked = true
            }
        }

        // Set up the "Return Back" button to navigate the user to the previous screen
        binding.returnBackButton.setOnClickListener {
            val navController = findNavController()
            val previousDestination = navController.previousBackStackEntry?.destination?.id  // Get the previous destination ID

            // If the previous destination is "trackOrderFragment", go back to that fragment
            // Otherwise, navigate to the home fragment
            when (previousDestination) {
                R.id.trackOrderFragment -> navController.navigateUp() // Navigate back if coming from TrackOrderFragment
                else -> navController.navigate(R.id.action_statusFragment_to_homeFragment) // Navigate to HomeFragment otherwise
            }
        }
    }

    /**
     * onDestroyView is called when the view is destroyed, to clean up any references to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

