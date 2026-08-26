package com.findmeahometeam.reskiume.data.remote.response.fosterHome

// This data class is used to query foster homes in iOS
data class QueryFosterHome(
    val id: String? = null,
    val ownerId: String? = null,
    val activistLongitude: Double? = null,
    val activistLatitude: Double? = null,
    val rangeLongitude: Double? = null,
    val rangeLatitude: Double? = null,
    val country: String? = null,
    val city: String? = null,
)
