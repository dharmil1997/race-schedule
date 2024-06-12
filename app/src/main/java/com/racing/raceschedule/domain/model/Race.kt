package com.racing.raceschedule.domain.model

// Data class representing a race
data class Race(
    val raceId: String,
    val meetingName: String,
    val raceNumber: Int,
    val advertisedStart: Long,
    val categoryId: String
)