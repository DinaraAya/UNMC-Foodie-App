package com.example.foodie.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.foodie.R
import com.example.foodie.databinding.FragmentFaqsBinding

/**
 * FaqsFragment displays a list of frequently asked questions (FAQs) in a TextView
 * and provides functionality to open a navigation drawer.
 */
class FaqsFragment : Fragment () {
    // Binding to the fragment layout (FragmentFaqsBinding)
    private var _binding: FragmentFaqsBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflates the layout for the fragment and sets up the FAQ content.
     * The FAQ content is loaded from a string resource with HTML formatting.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFaqsBinding.inflate(inflater, container, false)

        // Set the FAQ content with HTML formatting in the TextView
        val faqsTextView = binding.tvFaqs
        faqsTextView.text = HtmlCompat.fromHtml(getString(R.string.faq_content), HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Set up the hamburger icon click listener to open the navigation drawer
        binding.include.hamburgerIcon.setOnClickListener {
            // Open the drawer from the MainActivity
            (activity as MainActivity).openDrawer()
        }

        return binding.root
    }

    /**
     * Sets up additional interactions with UI components after the fragment view is created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the arrow icon click listener to open the drawer
        binding.include.arrow.setOnClickListener {
            // Open the drawer from the MainActivity
            (activity as? MainActivity)?.openDrawer()
        }
    }

    /**
     * Cleans up the binding reference when the view is destroyed to prevent memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding reference to avoid memory leaks
    }
}