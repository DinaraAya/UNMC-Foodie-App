package com.example.foodie.views

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.foodie.R
import com.example.foodie.databinding.FragmentLoginBinding
import com.example.foodie.repositories.UsersRepository
import com.example.foodie.viewmodels.LoginViewModel
import com.example.foodie.viewmodels.LoginViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // ViewModel for managing login data and handling login operations
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(UsersRepository())
    }

    // Flag to track password visibility state
    private var isPasswordVisible = false

    /**
     * onCreateView is called when the fragment's view is created.
     * Here we set up the ViewModel, handle button clicks, and the password visibility toggle.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.include.hamburgerIcon.visibility = View.GONE // Hide hamburger icon for this fragment

        // Observing the login result from the ViewModel to navigate or show error
        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if(isSuccess) {
                navigateToHomePage() // Navigate to home page on successful login
            }else {
                // Show error message if login fails
                var error = loginViewModel.errorMessage.value ?: "Login Failed"
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        })

        // Login button click listener
        binding.loginButton.setOnClickListener {
            val email = binding.etEmailLogin.text.toString().trim()
            val password = binding.etPasswordLogin.text.toString().trim()

            // Check if email and password fields are filled
            if(email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.loginUser(email, password) // Attempt login using the ViewModel
            } else {
                // Show error message if any field is empty
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Register button click listener to navigate to the registration fragment
        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Handling touch event for the password field to toggle visibility
        binding.etPasswordLogin.setOnTouchListener { _, event->
            if (event.action == MotionEvent.ACTION_UP) {
                // Check if the touch was on the eye icon to toggle password visibility
                if (event.rawX >= (binding.etPasswordLogin.right - binding.etPasswordLogin.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility() // Toggle the password visibility
                    return@setOnTouchListener true
                }
            }
            false
        }

        return binding.root
    }

    /**
     * Toggles password visibility between plain text and masked (dots).
     * Updates the visibility state and adjusts the cursor position.
     */
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            hidePassword()
        } else {
            showPassword()
        }
        isPasswordVisible = !isPasswordVisible

        // Keep the cursor at the end of the text after changing the visibility
        binding.etPasswordLogin.setSelection(binding.etPasswordLogin.text.length)
    }

    /**
     * Navigates to the home page fragment after a successful login.
     */
    private fun navigateToHomePage() {
        findNavController().navigate(R.id.action_loginFragment_to_homePageFragment)
    }

    /**
     * Shows the password in plain text (disables password masking).
     */
    private fun showPassword() {
        binding.etPasswordLogin.transformationMethod = HideReturnsTransformationMethod.getInstance()
        binding.etPasswordLogin.setCompoundDrawablesWithIntrinsicBounds(
            null, null, resources.getDrawable(R.drawable.view, null), null
        )
    }

    /**
     * Hides the password (masks it with dots).
     */
    private fun hidePassword() {
        binding.etPasswordLogin.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.etPasswordLogin.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null, null, resources.getDrawable(R.drawable.hide, null), null
        )
    }

    /**
     * Cleanup method to prevent memory leaks when the fragment view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}