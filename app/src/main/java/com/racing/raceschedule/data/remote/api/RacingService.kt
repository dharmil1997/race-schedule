package com.racing.raceschedule.data.remote.api

import com.racing.raceschedule.data.remote.dto.RaceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RacingService {
    // API call to fetch the next races
    @GET("racing/")
    suspend fun getNextRaces(
        @Query("method") method: String = "nextraces",
        @Query("count") count: Int = 10
    ): RaceResponse
}