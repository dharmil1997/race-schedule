package com.racing.raceschedule.data.remote.dto

import com.google.gson.annotations.SerializedName

// Data classes to parse the API response

data class RaceResponse(
    @SerializedName("data")
    val data: RaceData
)

data class RaceData(
    @SerializedName("race_summaries")
    val raceSummaries: Map<String, RaceSummary>
)

data class RaceSummary(
    @SerializedName("race_id")
    val raceId: String,
    @SerializedName("meeting_name")
    val meetingName: String,
    @SerializedName("race_number")
    val raceNumber: Int,
    @SerializedName("advertised_start")
    val advertisedStart: AdvertisedStart,
    @SerializedName("category_id")
    val categoryId: String
)

data class AdvertisedStart(
    @SerializedName("seconds")
    val seconds: Long
)