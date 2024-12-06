package com.example.foodie.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.R
import com.example.foodie.adapters.PromoPagerAdapter
import com.example.foodie.adapters.StallAdapter
import com.example.foodie.databinding.FragmentHomePageBinding
import com.example.foodie.models.Stalls
import com.example.foodie.viewmodels.StallViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    private val stallViewModel: StallViewModel by viewModels()
    private val stalls = mutableListOf<Stalls>()
    private val displayedStalls = mutableListOf<Stalls>()
    private lateinit var stallAdapter: StallAdapter

    // Promo image URLs
    private val promoImageUrls = listOf(
        "https://hometaste.my/wp-content/uploads/2022/05/Hometaste-x-monthly-promo-blog-banner-website-01-compressed.jpg",
        "https://familymart.com.my/img/promotions/promotion-subbanner_241002-sayCheeseee.jpg",
        "https://assets.grab.com/wp-content/uploads/sites/8/2023/05/28171329/Final-KVOG-Durian24.jpg"
    )
    private lateinit var promoAdapter: PromoPagerAdapter

    // Handler and Runnable for changing the promo items
    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private val changeDelay = 3000L // Change every 3 seconds

    // Runnable to cycle through promo images
    private val changeRunnable = object : Runnable {
        override fun run() {
            currentIndex = (currentIndex + 1) % promoImageUrls.size
            binding.viewpagerPromos.setCurrentItem(currentIndex, true) // Change the current item
            handler.postDelayed(this, changeDelay) // Schedule next change
        }
    }

    /**
     * Inflates the layout for this fragment, sets up the RecyclerViews for stalls and promos,
     * and observes the stalls data from the ViewModel.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)

        // Set up hamburger icon to open the drawer
        binding.include.hamburgerIcon.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        setupRecyclerView()
        setupPromoRecyclerView()
        observeStalls()

        // Filter stalls by default (indoor)
        filterStalls(isOutdoor = false)
        updateButtonSelection(isOutdoor = false)

        setupButtonClickListeners()

        return binding.root
    }

    /**
     * Sets up the RecyclerView for stalls, including the StallAdapter and click listener
     * for navigating to the selected stall's menu.
     */
    private fun setupRecyclerView() {
        stallAdapter = StallAdapter(displayedStalls) { selectedStall ->
            Log.d("HomePageFragment", "Selected stall: ${selectedStall.name}, ID: ${selectedStall.id}")
            val action = HomePageFragmentDirections.actionHomePageFragmentToMenuFragment(selectedStall)
            findNavController().navigate(action)
        }

        binding.recyclerViewStalls.apply {
            adapter = stallAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    /**
     * Sets up the ViewPager2 for displaying promotional images.
     */
    private fun setupPromoRecyclerView() {
        promoAdapter = PromoPagerAdapter(promoImageUrls)

        binding.viewpagerPromos.apply {
            adapter = promoAdapter
            offscreenPageLimit = 1 // Keep the previous page in memory
        }

        // Attach TabLayoutMediator to connect ViewPager2 and TabLayout for dots/lines indicators
        TabLayoutMediator(binding.tabIndicator, binding.viewpagerPromos) { tab, position ->
            // Nothing is set in the tab as it's just an indicator
        }.attach()
    }

    /**
     * Observes the list of stalls from the ViewModel and updates the displayed stalls accordingly.
     */
    private fun observeStalls() {
        stallViewModel.stalls.observe(viewLifecycleOwner) { newStalls ->
            stalls.clear() // Clear current stalls
            stalls.addAll(newStalls) // Add new stalls to the list
            filterStalls(isOutdoor = false) // Update the list based on the current filter (indoor by default)
        }
    }

    /**
     * Sets up click listeners for the indoor and outdoor filter buttons.
     */
    private fun setupButtonClickListeners() {
        binding.indoorButton.setOnClickListener {
            filterStalls(isOutdoor = false)
            updateButtonSelection(isOutdoor = false)
        }

        binding.outdoorButton.setOnClickListener {
            filterStalls(isOutdoor = true)
            updateButtonSelection(isOutdoor = true)
        }
    }

    /**
     * Filters the stalls list based on the isOutdoor parameter (indoor vs. outdoor).
     */
    private fun filterStalls(isOutdoor: Boolean) {
        displayedStalls.clear() // Clear the current displayed stalls
        displayedStalls.addAll(stalls.filter { it.isOutdoor == isOutdoor }) // Add filtered stalls
        stallAdapter.notifyDataSetChanged() // Notify the adapter to update the list
    }

    /**
     * Updates the selection state of the indoor and outdoor buttons with appropriate colors.
     */
    private fun updateButtonSelection(isOutdoor: Boolean) {
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.button_selected_color)
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.light_blue)

        binding.indoorButton.setBackgroundColor(if (!isOutdoor) selectedColor else defaultColor)
        binding.outdoorButton.setBackgroundColor(if (isOutdoor) selectedColor else defaultColor)
    }

    /**
     * Starts the promo image change cycle when the fragment is resumed.
     */
    override fun onResume() {
        super.onResume()
        handler.postDelayed(changeRunnable, changeDelay) // Start the promo image change cycle
    }

    /**
     * Stops the promo image change cycle when the fragment is paused.
     */
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(changeRunnable) // Remove the callback to stop the cycle
    }

    /**
     * Cleans up the binding reference when the view is destroyed to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Nullify the binding to avoid memory leaks
    }
}

