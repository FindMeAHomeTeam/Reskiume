package com.findmeahometeam.reskiume.data.remote.response.rescueEvent

// This data class is used to query rescue events in iOS
data class QueryRescueEvent(
    val id: String? = null,
    val creatorId: String? = null,
    val activistLongitude: Double? = null,
    val activistLatitude: Double? = null,
    val rangeLongitude: Double? = null,
    val rangeLatitude: Double? = null,
    val country: String? = null,
    val city: String? = null,
)
