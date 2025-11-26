package com.findmeahometeam.reskiume

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.User

// Mocked user data for testing

val user = User(
    uid = "userUid123",
    username = "Juan Antonio",
    description = "Hello, this is Juan Antonio's profile.",
    email = "juan@email.com",
    image = "image_uri_juan_antonio.jpg",
    isAvailable = true,
    lastLogout = 1625155200000L
)

const val userPwd: String = "myPwd123"

const val wrongEmail = "incorrectEmail.com"

val authUser = AuthUser(
    uid = user.uid,
    name = user.username,
    email = user.email,
    photoUrl = user.image
)


// Mocked cache data for testing

val localCache = LocalCache(
    id = 1,
    uid = user.uid,
    section = Section.REVIEWS,
    timestamp = 1625328000000L
)


// Mocked review data for testing

val review = Review(
    id = "1625241600000authorUid456",
    timestamp = 1625241600000L,
    authorUid = "authorUid456",
    reviewedUid = user.uid,
    description = "Great experience working with Juan!",
    rating = 4.5f
)

