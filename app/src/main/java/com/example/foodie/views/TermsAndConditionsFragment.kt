package com.example.foodie.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.example.foodie.R
import com.example.foodie.databinding.FragmentTermsAndConditionsBinding


class TermsAndConditionsFragment : Fragment() {
    private var _binding: FragmentTermsAndConditionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)

        // Get the TextView and set its text
        val termsTextView = binding.tvTermsAndConditions
        termsTextView.text = HtmlCompat.fromHtml(getString(R.string.tc_content), HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Open drawer when hamburger icon is clicked
        binding.include.hamburgerIcon.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up arrow click listener to open the drawer
        binding.include.arrow.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up binding reference
    }
}
