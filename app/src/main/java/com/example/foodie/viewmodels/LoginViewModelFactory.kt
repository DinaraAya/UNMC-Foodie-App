package com.example.foodie.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodie.repositories.UsersRepository

/**
 * Factory class responsible for creating instances of the [LoginViewModel].
 * This is required because [LoginViewModel] has a non-default constructor that requires a [UsersRepository].
 */
class LoginViewModelFactory(private val repository: UsersRepository) : ViewModelProvider.Factory {
    /**
     * Creates an instance of the ViewModel class.
     * This method checks if the ViewModel class is of type [LoginViewModel] and provides the necessary dependencies.
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return An instance of [LoginViewModel].
     * @throws IllegalArgumentException If the ViewModel class is unknown (not of type [LoginViewModel]).
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel class is of type LoginViewModel
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Create and return a LoginViewModel instance, passing the repository as a constructor argument
            return LoginViewModel(repository) as T
        }
        // If the ViewModel class is not LoginViewModel, throw an exception
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
