package com.example.foodie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodie.models.Stalls
import com.example.foodie.repositories.StallsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the stalls data.
 * It fetches the list of stalls from the [StallsRepository] and exposes it to the UI.
 */
@HiltViewModel
class StallViewModel @Inject constructor(
    private val stallsRepository: StallsRepository
) : ViewModel() {

    // LiveData to hold the list of stalls
    private val _stalls = MutableLiveData<List<Stalls>>()
    val stalls: LiveData<List<Stalls>> get() = _stalls

    // Initialize and load the list of stalls when the ViewModel is created
    init {
        loadStalls()
    }

    /**
     * Fetches the list of stalls from the [StallsRepository] and updates the [LiveData] with the result.
     */
    fun loadStalls() {
        // Launch a coroutine to fetch the stalls data
        viewModelScope.launch {
            // Get the list of stalls from the repository
            val stallList = stallsRepository.fetchStalls()
            // Update the LiveData with the fetched list of stalls
            _stalls.value = stallList
        }
    }
}

