package com.example.foodie.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.foodie.models.Stalls
import com.example.foodie.repositories.StallsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import junit.framework.TestCase.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class StallViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var stallsRepository: StallsRepository

    @Mock
    private lateinit var stallsObserver: Observer<List<Stalls>>

    private lateinit var viewModel: StallViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Set Main dispatcher to use the UnconfinedTestDispatcher
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)

        // Initialize mocks and the ViewModel
        MockitoAnnotations.openMocks(this)
        viewModel = StallViewModel(stallsRepository)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original Main dispatcher
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `loadStalls updates LiveData with stalls`() = runTest {
        // Arrange
        val mockStalls = listOf(
            Stalls(
                id = "1",
                name = "Stall 1",
                imageUrl = "https://example.com/stall1.jpg",
                operatingHours = "9 AM - 5 PM",
                operatingDays = "Mon-Fri",
                closedDays = "Sat-Sun",
                isOutdoor = true
            ),
            Stalls(
                id = "2",
                name = "Stall 2",
                imageUrl = "https://example.com/stall2.jpg",
                operatingHours = "10 AM - 6 PM",
                operatingDays = "Mon-Sun",
                closedDays = "",
                isOutdoor = false
            )
        )
        Mockito.`when`(stallsRepository.fetchStalls()).thenReturn(mockStalls)

        // Act
        viewModel.stalls.observeForever(stallsObserver)
        viewModel.loadStalls()
        advanceUntilIdle() // Ensure all coroutines are completed

        // Assert
        Mockito.verify(stallsObserver).onChanged(mockStalls)
        assertEquals(mockStalls, viewModel.stalls.value)

        // Clean up observer
        viewModel.stalls.removeObserver(stallsObserver)
    }
}
