package com.findmeahometeam.reskiume.di

import com.findmeahometeam.reskiume.domain.usecases.authUser.CreateUserWithEmailAndPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DeleteImageFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.DeleteUserFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetUserFromRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertUserInRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserEmailInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.ModifyUserInRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ModifyUserPasswordInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.DownloadImageToLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignInWithEmailAndPasswordFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.authUser.SignOutFromAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.DeleteAllMyChatsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.DeleteAllMyChatsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.DeleteMyChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.DeleteMyChatFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetAllMyChatsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetAllMyChatsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatMessageInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.InsertChatMessageInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.ModifyChatInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.ModifyChatInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteMyFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteMyFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.image.UploadImageToRemoteDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteAllCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.DeleteCacheFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.localCache.InsertCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.localCache.ModifyCacheInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.DeleteNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllMyNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteAllMyRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.DeleteMyRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllMyRescueEventsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetAllRescueEventsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.GetRescueEventFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.InsertRescueEventInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.rescueEvent.ModifyRescueEventInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.user.DeleteSubscriptionFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.GetAllUsersFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.user.InsertSubscriptionInLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.util.fcm.SubscribeToAllTopicsFromSubscriberRepository
import com.findmeahometeam.reskiume.domain.usecases.util.fcm.UnsubscribeFromAllTopicsFromSubscriberRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.ObserveIfLocationEnabledFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.GetLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.location.RequestEnableLocationFromLocationRepository
import com.findmeahometeam.reskiume.domain.usecases.util.translator.TranslateMessage
import com.plusmobileapps.konnectivity.Konnectivity
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {

    // Network connectivity
    single<Konnectivity> { Konnectivity() }

    // auth user
    factoryOf(::CreateUserWithEmailAndPasswordInAuthDataSource)
    factoryOf(::DeleteUserFromAuthDataSource)
    factoryOf(::ModifyUserEmailInAuthDataSource)
    factoryOf(::ModifyUserPasswordInAuthDataSource)
    factoryOf(::ObserveAuthStateInAuthDataSource)
    factoryOf(::SignInWithEmailAndPasswordFromAuthDataSource)
    factoryOf(::SignOutFromAuthDataSource)

    // chat
    factoryOf(::InsertChatInLocalRepository)
    factoryOf(::InsertChatInRemoteRepository)
    factoryOf(::InsertChatMessageInLocalRepository)
    factoryOf(::InsertChatMessageInRemoteRepository)
    factoryOf(::ModifyChatInLocalRepository)
    factoryOf(::ModifyChatInRemoteRepository)
    factoryOf(::DeleteMyChatFromLocalRepository)
    factoryOf(::DeleteMyChatFromRemoteRepository)
    factoryOf(::DeleteAllMyChatsFromLocalRepository)
    factoryOf(::DeleteAllMyChatsFromRemoteRepository)
    factoryOf(::GetChatFromLocalRepository)
    factoryOf(::GetChatFromRemoteRepository)
    factoryOf(::GetAllMyChatsFromLocalRepository)
    factoryOf(::GetAllMyChatsFromRemoteRepository)

    // fosterHome
    factoryOf(::DeleteAllMyFosterHomesFromLocalRepository)
    factoryOf(::DeleteAllMyFosterHomesFromRemoteRepository)
    factoryOf(::DeleteMyFosterHomeFromLocalRepository)
    factoryOf(::DeleteMyFosterHomeFromRemoteRepository)
    factoryOf(::GetAllMyFosterHomesFromLocalRepository)
    factoryOf(::GetAllFosterHomesFromLocalRepository)
    factoryOf(::GetAllMyFosterHomesFromRemoteRepository)
    factoryOf(::GetAllFosterHomesByCountryAndCityFromLocalRepository)
    factoryOf(::GetAllFosterHomesByCountryAndCityFromRemoteRepository)
    factoryOf(::GetAllFosterHomesByLocationFromLocalRepository)
    factoryOf(::GetAllFosterHomesByLocationFromRemoteRepository)
    factoryOf(::GetFosterHomeFromLocalRepository)
    factoryOf(::GetFosterHomeFromRemoteRepository)
    factoryOf(::InsertFosterHomeInLocalRepository)
    factoryOf(::InsertFosterHomeInRemoteRepository)
    factoryOf(::ModifyFosterHomeInLocalRepository)
    factoryOf(::ModifyFosterHomeInRemoteRepository)

    // image
    factoryOf(::DeleteImageFromLocalDataSource)
    factoryOf(::DeleteImageFromRemoteDataSource)
    factoryOf(::DownloadImageToLocalDataSource)
    factoryOf(::UploadImageToRemoteDataSource)
    factoryOf(::GetImagePathForFileNameFromLocalDataSource)

    // localCache
    factoryOf(::InsertCacheInLocalRepository)
    factoryOf(::ModifyCacheInLocalRepository)
    factoryOf(::GetDataByManagingObjectLocalCacheTimestamp)
    factoryOf(::DeleteCacheFromLocalRepository)
    factoryOf(::DeleteAllCacheFromLocalRepository)

    // nonHumanAnimal
    factoryOf(::DeleteAllNonHumanAnimalsFromLocalRepository)
    factoryOf(::DeleteAllNonHumanAnimalsFromRemoteRepository)
    factoryOf(::DeleteNonHumanAnimalFromLocalRepository)
    factoryOf(::DeleteNonHumanAnimalFromRemoteRepository)
    factoryOf(::GetAllMyNonHumanAnimalsFromLocalRepository)
    factoryOf(::GetAllNonHumanAnimalsFromLocalRepository)
    factoryOf(::GetAllNonHumanAnimalsFromRemoteRepository)
    factoryOf(::GetNonHumanAnimalFromLocalRepository)
    factoryOf(::GetNonHumanAnimalFromRemoteRepository)
    factoryOf(::InsertNonHumanAnimalInLocalRepository)
    factoryOf(::InsertNonHumanAnimalInRemoteRepository)
    factoryOf(::ModifyNonHumanAnimalInLocalRepository)
    factoryOf(::ModifyNonHumanAnimalInRemoteRepository)

    // rescueEvent
    factoryOf(::DeleteAllMyRescueEventsFromLocalRepository)
    factoryOf(::DeleteAllMyRescueEventsFromRemoteRepository)
    factoryOf(::DeleteMyRescueEventFromLocalRepository)
    factoryOf(::DeleteMyRescueEventFromRemoteRepository)
    factoryOf(::GetAllMyRescueEventsFromLocalRepository)
    factoryOf(::GetAllRescueEventsFromLocalRepository)
    factoryOf(::GetAllMyRescueEventsFromRemoteRepository)
    factoryOf(::GetAllRescueEventsByCountryAndCityFromLocalRepository)
    factoryOf(::GetAllRescueEventsByCountryAndCityFromRemoteRepository)
    factoryOf(::GetAllRescueEventsByLocationFromLocalRepository)
    factoryOf(::GetAllRescueEventsByLocationFromRemoteRepository)
    factoryOf(::GetRescueEventFromLocalRepository)
    factoryOf(::GetRescueEventFromRemoteRepository)
    factoryOf(::InsertRescueEventInLocalRepository)
    factoryOf(::InsertRescueEventInRemoteRepository)
    factoryOf(::ModifyRescueEventInLocalRepository)
    factoryOf(::ModifyRescueEventInRemoteRepository)

    // review
    factoryOf(::InsertReviewInLocalRepository)
    factoryOf(::DeleteReviewsFromLocalRepository)
    factoryOf(::GetReviewsFromLocalRepository)
    factoryOf(::InsertReviewInRemoteRepository)
    factoryOf(::DeleteReviewsFromRemoteRepository)
    factoryOf(::GetReviewsFromRemoteRepository)

    // user
    factoryOf(::DeleteSubscriptionFromLocalDataSource)
    factoryOf(::DeleteUserFromRemoteDataSource)
    factoryOf(::DeleteUsersFromLocalDataSource)
    factoryOf(::GetUserFromLocalDataSource)
    factoryOf(::GetAllUsersFromLocalDataSource)
    factoryOf(::GetUserFromRemoteDataSource)
    factoryOf(::InsertSubscriptionInLocalDataSource)
    factoryOf(::InsertUserInLocalDataSource)
    factoryOf(::InsertUserInRemoteDataSource)
    factoryOf(::ModifyUserInLocalDataSource)
    factoryOf(::ModifyUserInRemoteDataSource)

    // util translator
    factoryOf(::TranslateMessage)
    factoryOf(::ObserveIfLocationEnabledFromLocationRepository)
    factoryOf(::RequestEnableLocationFromLocationRepository)
    factoryOf(::GetLocationFromLocationRepository)

    // util
    factoryOf(::SubscribeToAllTopicsFromSubscriberRepository)
    factoryOf(::UnsubscribeFromAllTopicsFromSubscriberRepository)
}
