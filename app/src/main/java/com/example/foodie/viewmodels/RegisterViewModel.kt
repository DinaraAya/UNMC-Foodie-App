package com.example.foodie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodie.repositories.UsersRepository

/**
 * ViewModel class responsible for handling the user registration logic.
 * It communicates with the [UsersRepository] to register a new customer and provides feedback on the registration status.
 */
class RegisterViewModel(private val repository: UsersRepository) : ViewModel() {

    // LiveData to hold the registration status (true if successful, false if failed)
    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> get() = _registrationStatus

    // LiveData to hold the error message in case of a registration failure
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    /**
     * Registers a new customer with the provided full name, email, and password.
     * Updates the [registrationStatus] LiveData based on the outcome of the registration process.
     * If registration fails, the [errorMessage] LiveData will be updated with the error message.
     *
     * @param fullName The full name of the customer.
     * @param email The email address of the customer.
     * @param password The password chosen by the customer.
     */
    fun registerCustomer(fullName: String, email: String, password: String) {
        // Call the registerUser method from UsersRepository to create a new user
        repository.registerUser(email, password, fullName)
            .addOnSuccessListener {
                // Registration successful, update the registration status
                _registrationStatus.postValue(true)
            }
            .addOnFailureListener { e ->
                // Registration failed, update the registration status and error message
                _registrationStatus.postValue(false)
                _errorMessage.postValue(e.message)
            }
    }
}
