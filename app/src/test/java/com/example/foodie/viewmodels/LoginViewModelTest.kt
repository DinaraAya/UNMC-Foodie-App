package com.example.foodie.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.foodie.repositories.UsersRepository
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // To test LiveData synchronously

    private lateinit var loginViewModel: LoginViewModel
    private val repository: UsersRepository = mockk()
    private val loginResultObserver: Observer<Boolean> = mockk(relaxed = true)
    private val errorMessageObserver: Observer<String?> = mockk(relaxed = true)

    @Before
    fun setup() {
        loginViewModel = LoginViewModel(repository)
        loginViewModel.loginResult.observeForever(loginResultObserver)
        loginViewModel.errorMessage.observeForever(errorMessageObserver)
    }

    @Test
    fun `loginUser should post success when repository login succeeds`() {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val mockTask = mockk<Task<AuthResult>>()

        every { repository.loginUser(email, password) } returns mockTask
        every { mockTask.addOnSuccessListener(any()) } answers {
            firstArg<OnSuccessListener<AuthResult>>().onSuccess(mockk()) // Simulate success
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        // Act
        loginViewModel.loginUser(email, password)

        // Assert
        verify { loginResultObserver.onChanged(true) }
        verify(exactly = 0) { errorMessageObserver.onChanged(any()) } // No error
    }

    @Test
    fun `loginUser should post failure and error message when repository login fails`() {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val mockTask = mockk<Task<AuthResult>>()
        val exception = Exception("Login failed")

        every { repository.loginUser(email, password) } returns mockTask
        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } answers {
            firstArg<OnFailureListener>().onFailure(exception) // Simulate failure
            mockTask
        }

        // Act
        loginViewModel.loginUser(email, password)

        // Assert
        verify { loginResultObserver.onChanged(false) }
        verify { errorMessageObserver.onChanged("Login failed") }
    }
}

