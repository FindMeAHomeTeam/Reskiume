package com.findmeahometeam.reskiume.ui.profile.checkReviews

import com.findmeahometeam.reskiume.domain.model.user.User

interface CheckActivistUtil {
    suspend fun getUser(
        activistUid: String,
        myUserUid: String
    ): User?
}
