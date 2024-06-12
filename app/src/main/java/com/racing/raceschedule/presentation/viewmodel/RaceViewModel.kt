package com.racing.raceschedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.racing.raceschedule.domain.model.Race
import com.racing.raceschedule.domain.usecase.GetNextRacesUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RaceViewModel(private val getNextRacesUseCase: GetNextRacesUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(RaceUiState(isLoading = true))
    val uiState: StateFlow<RaceUiState> = _uiState

    private val _filter = MutableStateFlow<String?>(null)

    init {
        fetchRaces() // Fetch races initially
        startCountdownTimer() // Start the countdown timer
    }

    private fun fetchRaces() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                // Fetch and filter races based on the selected category
                getNextRacesUseCase().combine(_filter) { races, filter ->
                    val currentTime = System.currentTimeMillis() / 1000
                    races.filter { race ->
                        (filter == null || race.categoryId == filter) && (race.advertisedStart + 60 > currentTime)
                    }.sortedBy { it.advertisedStart }
                }.collect { filteredRaces ->
                    _uiState.value = RaceUiState(races = filteredRaces.take(5))
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun filterRaces(categoryId: String?) {
        _filter.value = categoryId
        fetchRaces()
    }

    private fun startCountdownTimer() {
        viewModelScope.launch {
            while (true) {
                val currentTime = System.currentTimeMillis() / 1000
                // Update the UI state by removing races that have started more than 60 seconds ago
                _uiState.value = _uiState.value.copy(
                    races = _uiState.value.races.filter { race ->
                        race.advertisedStart + 60 > currentTime
                    }
                )
                delay(1000) // Update every second
            }
        }
    }
}

data class RaceUiState(
    val races: List<Race> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)