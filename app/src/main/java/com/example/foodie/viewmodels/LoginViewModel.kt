package com.example.foodie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodie.repositories.UsersRepository

/**
 * ViewModel for managing the login process in the app.
 * Interacts with the [UsersRepository] to perform user login and handle the results.
 */
class LoginViewModel(private val repository: UsersRepository) : ViewModel() {

    // LiveData to hold the result of the login attempt (true for success, false for failure)
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    // LiveData to hold any error messages encountered during the login process
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Attempts to log in the user with the provided email and password.
     * This method interacts with the repository to authenticate the user and updates the LiveData with the result.
     *
     * @param email The email of the user trying to log in.
     * @param password The password of the user trying to log in.
     */
    fun loginUser(email: String, password: String) {
        // Call the repository to log in the user
        repository.loginUser(email, password)
            .addOnSuccessListener {
                // On successful login, update the login result to true
                _loginResult.postValue(true) // Login successful
            }
            .addOnFailureListener { e ->
                // On login failure, update the login result to false and set the error message
                _loginResult.postValue(false) // Login failed
                _errorMessage.postValue(e.message)
            }
    }
}