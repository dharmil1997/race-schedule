package com.racing.raceschedule.domain.repository

import com.racing.raceschedule.domain.model.Race
import kotlinx.coroutines.flow.Flow

// Repository interface for fetching races
interface RaceRepository {
    suspend fun getNextRaces(): Flow<List<Race>>
}