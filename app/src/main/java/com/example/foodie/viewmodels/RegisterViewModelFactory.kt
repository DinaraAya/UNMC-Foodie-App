package com.example.foodie.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodie.repositories.UsersRepository

/**
 * Factory class for creating an instance of [RegisterViewModel].
 * This is used for dependency injection to provide the [UsersRepository] into the [RegisterViewModel].
 */
class RegisterViewModelFactory(private val repository: UsersRepository) : ViewModelProvider.Factory {
    /**
     * Creates an instance of [RegisterViewModel].
     * It provides the necessary [UsersRepository] dependency to the ViewModel.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return The newly created instance of [RegisterViewModel].
     * @throws IllegalArgumentException If the provided class is not assignable from [RegisterViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is RegisterViewModel
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            // Return the RegisterViewModel with the repository injected
            return RegisterViewModel(repository) as T
        }
        // Throw an exception if the ViewModel class is unknown
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
