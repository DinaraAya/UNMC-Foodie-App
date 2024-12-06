package com.example.foodie.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodie.adapters.TrackOrderAdapter
import com.example.foodie.databinding.FragmentTrackOrderBinding
import com.example.foodie.viewmodels.TrackOrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackOrderFragment : Fragment() {

    private var _binding: FragmentTrackOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrackOrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackOrderBinding.inflate(inflater, container, false)

        // Initialize RecyclerView
        val adapter = TrackOrderAdapter { orderNumber, status ->
            val action = TrackOrderFragmentDirections.actionTrackOrderFragmentToStatusFragment(orderNumber, status)
            findNavController().navigate(action)
        }
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOrders.adapter = adapter

        // Observe orders in the ViewModel and update the adapter
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            adapter.submitList(orders)
        }

        // Set up arrow click listener to open the drawer
        binding.include.arrow.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

