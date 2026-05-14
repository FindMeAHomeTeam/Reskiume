package com.findmeahometeam.reskiume.ui.core.navigation

import kotlinx.serialization.Serializable

enum class Routes(val route: String) {
    HOME_SCREEN("homeScreen"),
    CHECK_ALL_FOSTER_HOMES("checkAllFosterHomes"),
    CHECK_ALL_RESCUE_EVENTS("checkAllRescueEvents"),
    CHECK_ALL_MY_CHATS("checkAllMyChats"),
    PROFILE("profile"),
    CREATE_ACCOUNT("createAccount"),
    LOGIN_ACCOUNT("loginAccount"),
    MODIFY_ACCOUNT("modifyAccount"),
    DELETE_ACCOUNT("deleteAccount"),
    CREATE_NON_HUMAN_ANIMAL("createNonHumanAnimal"),
    CHECK_ALL_ADVICE("checkAllAdvice")

}

@Serializable
class CheckFosterHome(
    val fosterHomeId: String,
    val ownerId: String,
    val chatId: String = ""
)

@Serializable
class CheckAllMyFosterHomes(val myUid: String)

@Serializable
class ModifyFosterHome(val fosterHomeId: String)

@Serializable
class CreateFosterHome(val ownerId: String)

@Serializable
class CheckRescueEvent(
    val rescueEventId: String,
    val creatorId: String,
)

@Serializable
class CheckAllMyRescueEvents(val myUid: String)

@Serializable
class ModifyRescueEvent(val rescueEventId: String)

@Serializable
class CreateRescueEvent(val creatorId: String)

@Serializable
class CheckReviews(val uid: String)

@Serializable
class CheckAllMyNonHumanAnimals(val caregiverId: String)

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

@Serializable
class CheckChat(
    val chatId: String,
    val lastTimestamp: Long
)

@Serializable
class CheckAdvice(
    val title: String,
    val description: String,
    val image: String,
    val authorUid: String? = null,
    val authorName: String? = null,
    val authorImage: String? = null
)
