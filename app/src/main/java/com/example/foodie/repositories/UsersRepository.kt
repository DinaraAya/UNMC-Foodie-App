package com.example.foodie.repositories

import com.example.foodie.models.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

/**
 * Repository for managing user-related operations, such as registration and login.
 * Interacts with Firebase Authentication and Firestore to handle user accounts.
 */
class UsersRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Registers a new user by creating a Firebase Authentication account and storing the user's details in Firestore.
     *
     * @param email The user's email address.
     * @param password The user's chosen password.
     * @param fullName The user's full name.
     * @return A [Task] representing the completion of the registration process.
     */
    fun registerUser(email: String, password: String, fullName: String): Task<Void> {
        // Start by creating a new user with the provided email and password
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
            .continueWithTask { authTask ->
                // If user registration fails, throw the exception
                if (!authTask.isSuccessful) {
                    throw authTask.exception ?: Exception("Registration failed")
                }
                // Extract the user ID from the result of the authentication task
                val userId = authTask.result?.user?.uid ?: throw Exception("User ID is null")

                // Create a Customer object with the provided full name and email
                val customer = Customer(fullName, email)

                // Return a Firestore task to store the customer's data in Firestore
                firestore.collection("customers").document(userId).set(customer)
            }
    }

    /**
     * Logs in an existing user using their email and password.
     *
     * @param email The user's email address.
     * @param password The user's password.
     * @return A [Task] representing the result of the sign-in operation, containing an [AuthResult] on success.
     */
    fun loginUser(email: String, password: String): Task<AuthResult> {
        // Use Firebase Authentication to sign in with the provided credentials
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }
}

