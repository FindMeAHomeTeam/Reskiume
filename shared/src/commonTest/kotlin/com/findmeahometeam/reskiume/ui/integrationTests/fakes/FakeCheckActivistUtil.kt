package com.findmeahometeam.reskiume.ui.integrationTests.fakes

import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.ui.profile.checkReviews.CheckActivistUtil
import com.findmeahometeam.reskiume.user

class FakeCheckActivistUtil(
    private val activistToCheck: User? = user
): CheckActivistUtil {

    override suspend fun getUser(
        activistUid: String,
        myUserUid: String
    ): User? {
        return if (activistUid == activistToCheck?.uid) {
            user
        } else {
            null
        }
    }
}
