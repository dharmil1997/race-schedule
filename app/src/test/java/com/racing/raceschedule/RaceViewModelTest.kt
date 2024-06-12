package com.racing.raceschedule

import com.racing.raceschedule.domain.model.Race
import com.racing.raceschedule.domain.repository.RaceRepository
import com.racing.raceschedule.domain.usecase.GetNextRacesUseCase
import com.racing.raceschedule.presentation.FilterCategories
import com.racing.raceschedule.presentation.viewmodel.RaceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class RaceViewModelTest {

    private lateinit var viewModel: RaceViewModel
    private lateinit var getNextRacesUseCase: GetNextRacesUseCase
    private lateinit var raceRepository: RaceRepository

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        raceRepository = mock()
        getNextRacesUseCase = GetNextRacesUseCase(raceRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher after the test
    }

    @Test
    fun `fetchRaces updates races`() = runTest {
        // Given
        val races = listOf(
            Race("raceId1", "Meeting1", 1, System.currentTimeMillis() / 1000, FilterCategories.HORSE),
            Race("raceId2", "Meeting2", 2, System.currentTimeMillis() / 1000, FilterCategories.GREYHOUND)
        )
        stubFetchRaces(races)

        viewModel = RaceViewModel(getNextRacesUseCase) // init ViewModel

        // Then
        assertEquals(races, viewModel.uiState.value.races)
    }

    @Test
    fun `filterRaces updates races correctly`() = runTest {
        // Given
        val horseRace = Race("raceId1", "Meeting1", 1, System.currentTimeMillis() / 1000, FilterCategories.HORSE)
        val greyhoundRace = Race("raceId2", "Meeting2", 2, System.currentTimeMillis() / 1000, FilterCategories.GREYHOUND)
        val harnessRace = Race("raceId3", "Meeting3", 3, System.currentTimeMillis() / 1000, FilterCategories.HARNESS)
        val races = listOf(horseRace, greyhoundRace, harnessRace)
        stubFetchRaces(races)

        viewModel = RaceViewModel(getNextRacesUseCase) // init ViewModel

        // When & Then
        assertFilterRaces(FilterCategories.HORSE, listOf(horseRace))
        assertFilterRaces(FilterCategories.GREYHOUND, listOf(greyhoundRace))
        assertFilterRaces(FilterCategories.HARNESS, listOf(harnessRace))
        assertFilterRaces(null, races)
    }

    @Test
    fun `race gets removed from the list when 60 seconds past its start time`() = runTest {
        // Given
        val currentTime = System.currentTimeMillis() / 1000
        val pastStartTime = currentTime - 70 // Setting a past start time
        val futureStartTime = currentTime + 70 // Setting a future start time
        val races = listOf(
            Race("raceId1", "Meeting1", 1, pastStartTime, FilterCategories.HORSE),
            Race("raceId2", "Meeting2", 2, futureStartTime, FilterCategories.GREYHOUND)
        )
        stubFetchRaces(races)

        viewModel = RaceViewModel(getNextRacesUseCase) // init ViewModel

        // Wait for a moment to ensure the race item gets removed
        delay(2000) // Assuming the removal happens within 2 seconds

        // Then
        assertEquals(1, viewModel.uiState.value.races.size) // Only one race should be left
        assertEquals("raceId2", viewModel.uiState.value.races[0].raceId) // Ensure the correct race is left
    }

    @Test
    fun `fetchRaces sets error message when exception occurs`() = runTest {
        // Given
        val errorMessage = "An error occurred"
        stubFetchRacesError(errorMessage)

        viewModel = RaceViewModel(getNextRacesUseCase) // init ViewModel

        // Then
        assertEquals(errorMessage, viewModel.uiState.value.errorMessage)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    private suspend fun stubFetchRaces(races: List<Race>) {
        whenever(getNextRacesUseCase.invoke()).thenReturn(flow { emit(races) })
    }

    private suspend fun stubFetchRacesError(errorMessage: String) {
        whenever(getNextRacesUseCase.invoke()).thenThrow(RuntimeException(errorMessage))
    }

    private fun assertFilterRaces(category: String?, expectedRaces: List<Race>) {
        viewModel.filterRaces(category)
        assertEquals(expectedRaces, viewModel.uiState.value.races)
    }
}