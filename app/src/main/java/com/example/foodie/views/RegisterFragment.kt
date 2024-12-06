package com.example.foodie.views

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodie.R
import com.example.foodie.databinding.FragmentRegisterBinding
import com.example.foodie.viewmodels.RegisterViewModel
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.foodie.models.Customer
import com.example.foodie.repositories.UsersRepository
import com.example.foodie.viewmodels.RegisterViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null // Reference to the binding object for the fragment
    private val binding get() = _binding!! // Getter for binding, ensures null safety
    private val registerViewModel: RegisterViewModel by viewModels { // ViewModel for registration
        RegisterViewModelFactory(UsersRepository()) // Factory to create ViewModel
    }


    private var isPasswordVisible = false // Flag to track password visibility

    /**
     * onCreateView is called to inflate the fragment's layout and set up UI elements.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        // Hide hamburger icon in the action bar
        binding.include2.hamburgerIcon.visibility = View.GONE

        // Observe registration status to show a success message and navigate
        registerViewModel.registrationStatus.observe(viewLifecycleOwner, Observer { isRegistered ->
            if(isRegistered){
                Toast.makeText(requireContext(), "Registration Successful!", Toast.LENGTH_SHORT).show()
            }
        })

        // Observe any error messages and display them as Toast
        registerViewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            if(!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })

        // Set up the password visibility toggle for password input field
        binding.etPassword.setOnTouchListener { _, event->
            // Check if the user tapped on the "eye" icon to toggle password visibility
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etPassword.right - binding.etPassword.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility() // Toggle password visibility when icon is clicked
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Navigate to LoginFragment when the "Sign In" button is clicked
        binding.buttonSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Handle the registration button click, validating inputs before attempting registration
        binding.buttonRegister2.setOnClickListener {
            val fullName = binding.etFullname.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etPasswordConfirm.text.toString()

            // Validate input lengths
            if (email.length > 320) {
                Toast.makeText(requireContext(), "Email should not exceed 320 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length > 14) {
                Toast.makeText(requireContext(), "Password should not exceed 14 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate that passwords match
            if (password == confirmPassword) {
                registerViewModel.registerCustomer(fullName, email, password) // Attempt to register user
            } else {
                // Show a message if passwords do not match
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    /**
     * Toggles the visibility of the password (show/hide).
     */
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            hidePassword()
        } else {
            showPassword()
        }
        isPasswordVisible = !isPasswordVisible

        // Ensure the cursor stays at the end of the input text after toggling
        binding.etPassword.setSelection(binding.etPassword.text.length)
        binding.etPasswordConfirm.setSelection(binding.etPasswordConfirm.text.length)
    }

    /**
     * Shows the password in plain text (visibility mode).
     */
    private fun showPassword() {
        binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        binding.etPasswordConfirm.transformationMethod = HideReturnsTransformationMethod.getInstance()

        // Change the eye icon to indicate password is visible
        binding.etPassword.setCompoundDrawablesWithIntrinsicBounds(
            null, null, resources.getDrawable(R.drawable.view, null), null
        )
        binding.etPasswordConfirm.setCompoundDrawablesWithIntrinsicBounds(
            null, null, resources.getDrawable(R.drawable.view, null), null
        )
    }

    /**
     * Hides the password (password transformation method).
     */
    private fun hidePassword() {
        binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.etPasswordConfirm.transformationMethod = PasswordTransformationMethod.getInstance()

        // Change the eye icon to indicate password is hidden
        binding.etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, resources.getDrawable(R.drawable.hide, null), null
        )
        binding.etPasswordConfirm.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, resources.getDrawable(R.drawable.hide, null), null
        )
    }

    /**
     * Clean up and release resources when the fragment's view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

