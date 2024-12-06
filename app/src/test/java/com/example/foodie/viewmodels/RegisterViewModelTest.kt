package com.example.foodie.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.foodie.repositories.UsersRepository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // To test LiveData synchronously

    private lateinit var registerViewModel: RegisterViewModel
    private val repository: UsersRepository = mockk()
    private val registrationStatusObserver: Observer<Boolean> = mockk(relaxed = true)
    private val errorMessageObserver: Observer<String> = mockk(relaxed = true)

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(repository)
        registerViewModel.registrationStatus.observeForever(registrationStatusObserver)
        registerViewModel.errorMessage.observeForever(errorMessageObserver)
    }

    @Test
    fun `registerCustomer should post success when repository registerUser succeeds`() {
        // Arrange
        val fullName = "John Doe"
        val email = "test@example.com"
        val password = "password123"
        val mockTask = mockk<Task<Void>>()

        every { repository.registerUser(email, password, fullName) } returns mockTask
        every { mockTask.addOnSuccessListener(any()) } answers {
            firstArg<OnSuccessListener<Void>>().onSuccess(null) // Simulate success
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        // Act
        registerViewModel.registerCustomer(fullName, email, password)

        // Assert
        verify { registrationStatusObserver.onChanged(true) }
        verify(exactly = 0) { errorMessageObserver.onChanged(any()) } // No error
    }

    @Test
    fun `registerCustomer should post failure and error message when repository registerUser fails`() {
        // Arrange
        val fullName = "John Doe"
        val email = "test@example.com"
        val password = "password123"
        val mockTask = mockk<Task<Void>>()
        val exception = Exception("Registration failed")

        every { repository.registerUser(email, password, fullName) } returns mockTask
        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } answers {
            firstArg<OnFailureListener>().onFailure(exception) // Simulate failure
            mockTask
        }

        // Act
        registerViewModel.registerCustomer(fullName, email, password)

        // Assert
        verify { registrationStatusObserver.onChanged(false) }
        verify { errorMessageObserver.onChanged("Registration failed") }
    }
}
