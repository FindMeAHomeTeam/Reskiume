package com.findmeahometeam.reskiume.ui.core.navigation

import kotlinx.serialization.Serializable

enum class Routes(val route: String) {
    HOME_SCREEN("homeScreen"),
    FOSTER_HOMES("fosterHomes"),
    RESCUE("rescue"),
    CHATS("chats"),
    PROFILE("profile"),
    CREATE_ACCOUNT("createAccount"),
    LOGIN_ACCOUNT("loginAccount"),
    MODIFY_ACCOUNT("modifyAccount"),
    DELETE_ACCOUNT("deleteAccount"),
    CREATE_NON_HUMAN_ANIMAL("createNonHumanAnimal")

}

@Serializable
class CheckReviews(val uid: String)

@Serializable
class CheckAllNonHumanAnimals(val caregiverId: String)

@Serializable
class CheckNonHumanAnimal(
    val nonHumanAnimalId: String,
    val caregiverId: String
)

@Serializable
class ModifyNonHumanAnimal(
    val nonHumanAnimalId: String,
    val caregiverId: String
)
