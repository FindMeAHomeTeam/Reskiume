package com.findmeahometeam.reskiume

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.model.User

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
