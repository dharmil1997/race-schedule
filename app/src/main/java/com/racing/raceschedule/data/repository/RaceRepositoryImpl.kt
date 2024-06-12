package com.racing.raceschedule.data.repository

import com.racing.raceschedule.data.remote.api.RacingService
import com.racing.raceschedule.domain.model.Race
import com.racing.raceschedule.domain.repository.RaceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class RaceRepositoryImpl(private val service: RacingService) : RaceRepository {
    // Implementation of the RaceRepository to fetch races from the service
    override suspend fun getNextRaces(): Flow<List<Race>> = flow {
        while (true) {
            // Fetch the next races from the API
            val response = service.getNextRaces()
            val raceSummaries = response.data.raceSummaries

            // Map the API response to a list of Race objects
            val races = raceSummaries.values.map {
                Race(
                    raceId = it.raceId,
                    meetingName = it.meetingName,
                    raceNumber = it.raceNumber,
                    advertisedStart = it.advertisedStart.seconds,
                    categoryId = it.categoryId
                )
            }
            emit(races) // Emit the list of races
            delay(TimeUnit.MINUTES.toMillis(1)) // Refresh every minute
        }
    }
}