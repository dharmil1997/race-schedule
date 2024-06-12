package com.racing.raceschedule

import com.racing.raceschedule.data.remote.api.RacingService
import com.racing.raceschedule.data.remote.dto.AdvertisedStart
import com.racing.raceschedule.data.remote.dto.RaceData
import com.racing.raceschedule.data.remote.dto.RaceResponse
import com.racing.raceschedule.data.remote.dto.RaceSummary
import com.racing.raceschedule.data.repository.RaceRepositoryImpl
import com.racing.raceschedule.domain.model.Race
import com.racing.raceschedule.domain.repository.RaceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RaceRepositoryTest {

    private lateinit var raceRepository: RaceRepository
    private lateinit var racingService: RacingService

    @Before
    fun setup() {
        // Mock the RacingService
        racingService = mock()
        raceRepository = RaceRepositoryImpl(racingService)
    }

    @Test
    fun `raceRepository fetches next races from service`() = runTest {
        // Given
        val races = listOf(
            Race("raceId1", "Meeting1", 1, System.currentTimeMillis() / 1000, "HORSE"),
            Race("raceId2", "Meeting2", 2, System.currentTimeMillis() / 1000, "GREYHOUND")
        )
        stubGetNextRacesFromService(races)

        // When
        val result = raceRepository.getNextRaces()

        // Then
        assertEquals(races, result.first())
    }

    private suspend fun stubGetNextRacesFromService(races: List<Race>) {
        // Stubbing the behavior of getNextRaces() in the mock racingService
        val raceSummaries = races.map {
            RaceSummary(
                raceId = it.raceId,
                meetingName = it.meetingName,
                raceNumber = it.raceNumber,
                advertisedStart = AdvertisedStart(it.advertisedStart),
                categoryId = it.categoryId
            )
        }.associateBy { it.raceId }
        val raceData = RaceData(raceSummaries)
        val raceResponse = RaceResponse(raceData)
        whenever(racingService.getNextRaces()).thenReturn(raceResponse)
    }
}