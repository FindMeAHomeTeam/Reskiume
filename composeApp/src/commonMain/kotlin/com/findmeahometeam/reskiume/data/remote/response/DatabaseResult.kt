package com.findmeahometeam.reskiume.data.remote.response

sealed class DatabaseResult {
    data object Success : DatabaseResult()
    data class Error(val message: String = "") : DatabaseResult()
}
