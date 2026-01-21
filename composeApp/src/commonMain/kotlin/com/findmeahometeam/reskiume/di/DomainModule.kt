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
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.DeleteFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByCountryAndCityFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllFosterHomesByLocationFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetFosterHomeFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertAcceptedNonHumanAnimalGenderForFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertAcceptedNonHumanAnimalTypeForFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.InsertResidentNonHumanAnimalForFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyAcceptedNonHumanAnimalGenderForFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyAcceptedNonHumanAnimalTypeForFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyFosterHomeInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.ModifyResidentNonHumanAnimalForFosterHomeInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
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
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetNonHumanAnimalFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.DeleteReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.GetReviewsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.review.InsertReviewInRemoteRepository
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

    // fosterHome
    factoryOf(::DeleteAllMyFosterHomesFromLocalRepository)
    factoryOf(::DeleteAllMyFosterHomesFromRemoteRepository)
    factoryOf(::DeleteFosterHomeFromLocalRepository)
    factoryOf(::DeleteFosterHomeFromRemoteRepository)
    factoryOf(::GetAllMyFosterHomesFromLocalRepository)
    factoryOf(::GetAllMyFosterHomesFromRemoteRepository)
    factoryOf(::GetAllFosterHomesByCountryAndCityFromLocalRepository)
    factoryOf(::GetAllFosterHomesByCountryAndCityFromRemoteRepository)
    factoryOf(::GetAllFosterHomesByLocationFromLocalRepository)
    factoryOf(::GetAllFosterHomesByLocationFromRemoteRepository)
    factoryOf(::GetFosterHomeFromLocalRepository)
    factoryOf(::GetFosterHomeFromRemoteRepository)
    factoryOf(::InsertFosterHomeInLocalRepository)
    factoryOf(::InsertResidentNonHumanAnimalForFosterHomeInLocalRepository)
    factoryOf(::InsertAcceptedNonHumanAnimalTypeForFosterHomeInLocalRepository)
    factoryOf(::InsertAcceptedNonHumanAnimalGenderForFosterHomeInLocalRepository)
    factoryOf(::InsertFosterHomeInRemoteRepository)
    factoryOf(::ModifyFosterHomeInLocalRepository)
    factoryOf(::ModifyResidentNonHumanAnimalForFosterHomeInLocalRepository)
    factoryOf(::ModifyAcceptedNonHumanAnimalTypeForFosterHomeInLocalRepository)
    factoryOf(::ModifyAcceptedNonHumanAnimalGenderForFosterHomeInLocalRepository)
    factoryOf(::ModifyFosterHomeInRemoteRepository)

    // image
    factoryOf(::DeleteImageFromLocalDataSource)
    factoryOf(::DeleteImageFromRemoteDataSource)
    factoryOf(::DownloadImageToLocalDataSource)
    factoryOf(::UploadImageToRemoteDataSource)
    factoryOf(::GetCompleteImagePathFromLocalDataSource)

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
    factoryOf(::GetAllNonHumanAnimalsFromLocalRepository)
    factoryOf(::GetAllNonHumanAnimalsFromRemoteRepository)
    factoryOf(::GetNonHumanAnimalFromLocalRepository)
    factoryOf(::GetNonHumanAnimalFromRemoteRepository)
    factoryOf(::InsertNonHumanAnimalInLocalRepository)
    factoryOf(::InsertNonHumanAnimalInRemoteRepository)
    factoryOf(::ModifyNonHumanAnimalInLocalRepository)
    factoryOf(::ModifyNonHumanAnimalInRemoteRepository)

    // review
    factoryOf(::InsertReviewInLocalRepository)
    factoryOf(::DeleteReviewsFromLocalRepository)
    factoryOf(::GetReviewsFromLocalRepository)
    factoryOf(::InsertReviewInRemoteRepository)
    factoryOf(::DeleteReviewsFromRemoteRepository)
    factoryOf(::GetReviewsFromRemoteRepository)

    // user
    factoryOf(::DeleteUserFromRemoteDataSource)
    factoryOf(::DeleteUsersFromLocalDataSource)
    factoryOf(::GetUserFromLocalDataSource)
    factoryOf(::GetUserFromRemoteDataSource)
    factoryOf(::InsertUserInLocalDataSource)
    factoryOf(::InsertUserInRemoteDataSource)
    factoryOf(::ModifyUserInLocalDataSource)
    factoryOf(::ModifyUserInRemoteDataSource)
}
