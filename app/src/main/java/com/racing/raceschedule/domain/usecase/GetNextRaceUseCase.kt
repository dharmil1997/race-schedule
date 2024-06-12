package com.racing.raceschedule.domain.usecase

import com.racing.raceschedule.domain.model.Race
import com.racing.raceschedule.domain.repository.RaceRepository
import kotlinx.coroutines.flow.Flow

class GetNextRacesUseCase(private val repository: RaceRepository) {
    suspend operator fun invoke(): Flow<List<Race>> = repository.getNextRaces()
}