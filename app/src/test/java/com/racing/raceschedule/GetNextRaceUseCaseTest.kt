package com.racing.raceschedule

import com.racing.raceschedule.domain.model.Race
import com.racing.raceschedule.domain.repository.RaceRepository
import com.racing.raceschedule.domain.usecase.GetNextRacesUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetNextRacesUseCaseTest {

    private lateinit var raceRepository: RaceRepository
    private lateinit var getNextRacesUseCase: GetNextRacesUseCase

    @Before
    fun setup() {
        raceRepository = mock()
        getNextRacesUseCase = GetNextRacesUseCase(raceRepository)
    }

    @Test
    fun `getNextRacesUseCase returns races from repository`() = runTest {
        // Given
        val races = listOf(
            Race("raceId1", "Meeting1", 1, System.currentTimeMillis() / 1000, "HORSE"),
            Race("raceId2", "Meeting2", 2, System.currentTimeMillis() / 1000, "GREYHOUND")
        )
        stubGetNextRaces(races)

        // When
        val result = getNextRacesUseCase()

        // Then
        assertEquals(races, result.first())
    }

    private suspend fun stubGetNextRaces(races: List<Race>) {
        whenever(raceRepository.getNextRaces()).thenReturn(flow { emit(races) })
    }
}