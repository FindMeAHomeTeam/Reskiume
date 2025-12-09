package com.findmeahometeam.reskiume

import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.model.AgeCategory
import com.findmeahometeam.reskiume.domain.model.LocalCache
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.Review
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.ui.profile.checkReviews.UiReview

// Mocked user data for testing

val user = User(
    uid = "userUid123",
    savedBy = "userUid123",
    username = "Juan Antonio",
    description = "Hello, this is Juan Antonio's profile.",
    email = "juan@email.com",
    image = "image_uri_juan_antonio.jpg",
    isAvailable = true
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
    id = 0,
    uid = user.uid,
    savedBy = user.uid,
    section = Section.REVIEWS,
    timestamp = 1625328000000L
)


// Mocked review data for testing

val author = User(
    uid = "PatryUid123",
    savedBy = user.uid,
    username = "Patry",
    description = "Hello, this is Patry's profile.",
    email = "patry@email.com",
    image = "image_uri_patry.jpg",
    isAvailable = true
)

val review = Review(
    id = "1625241600000 + ${author.uid}",
    savedBy = user.uid,
    timestamp = 1625241600000L,
    authorUid = author.uid,
    reviewedUid = user.uid,
    description = "Great experience working with Juan!",
    rating = 4.5f
)

val uiReview = UiReview(
    date = "+53471-11-09",
    authorUid = author.uid,
    authorName = author.username,
    authorUri = author.image,
    description = review.description,
    rating = review.rating
)

// Mocked nonHumanAnimal data for testing

val nonHumanAnimal = NonHumanAnimal(
    id = 1,
    caregiverId = user.uid,
    savedBy = user.uid,
    name = "Lucky",
    ageCategory = AgeCategory.ADULT,
    description = "Lucky is wonderfully peaceful and calm",
    imageUrl = "image_uri_lucky.jpg",
    nonHumanAnimalType = NonHumanAnimalType.DOG,
)

