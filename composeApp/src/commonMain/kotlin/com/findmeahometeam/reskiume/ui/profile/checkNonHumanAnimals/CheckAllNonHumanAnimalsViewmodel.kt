package com.findmeahometeam.reskiume.ui.profile.checkNonHumanAnimals

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.GetAllNonHumanAnimalsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.InsertNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.nonHumanAnimal.ModifyNonHumanAnimalInLocalRepository
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllNonHumanAnimals
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class CheckAllNonHumanAnimalsViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getAllNonHumanAnimalsFromRemoteRepository: GetAllNonHumanAnimalsFromRemoteRepository,
    private val insertNonHumanAnimalInLocalRepository: InsertNonHumanAnimalInLocalRepository,
    private val modifyNonHumanAnimalInLocalRepository: ModifyNonHumanAnimalInLocalRepository,
    private val getAllNonHumanAnimalsFromLocalRepository: GetAllNonHumanAnimalsFromLocalRepository,
    private val log: Log
) : ViewModel() {

    private val uid = saveStateHandleProvider.provideObjectRoute(CheckAllNonHumanAnimals::class).uid

    val nonHumanAnimalListFlow: Flow<List<NonHumanAnimal>> =
        observeAuthStateInAuthDataSource().flatMapConcat { authUser: AuthUser? ->

            getDataByManagingObjectLocalCacheTimestamp(
                uid = uid,
                savedBy = authUser?.uid ?: "",
                section = Section.NON_HUMAN_ANIMALS,
                onCompletionInsertCache = {
                    getAllNonHumanAnimalsFromRemoteRepository(uid).insertRemoteNonHumanAnimalsInLocalRepository()
                },
                onCompletionUpdateCache = {
                    getAllNonHumanAnimalsFromRemoteRepository(uid).modifyRemoteNonHumanAnimalsInLocalRepository()
                },
                onVerifyCacheIsRecent = {
                    getAllNonHumanAnimalsFromLocalRepository(uid)
                }
            )
        }

    private fun Flow<List<NonHumanAnimal>>.insertRemoteNonHumanAnimalsInLocalRepository(): Flow<List<NonHumanAnimal>> =
        this.map { list ->
            list.map { nonHumanAnimal ->
                insertNonHumanAnimalInLocalRepository(nonHumanAnimal) {
                    if (it > 0) {
                        log.d(
                            "CheckNonHumanAnimalsViewmodel",
                            "Non human animal ${nonHumanAnimal.id} added to local database"
                        )
                    } else {
                        log.e(
                            "CheckNonHumanAnimalsViewmodel",
                            "Error adding the non human animal ${nonHumanAnimal.id} to local database"
                        )
                    }
                }
                nonHumanAnimal
            }
        }

    private fun Flow<List<NonHumanAnimal>>.modifyRemoteNonHumanAnimalsInLocalRepository(): Flow<List<NonHumanAnimal>> =
        this.map { list ->
            list.map { nonHumanAnimal ->
                modifyNonHumanAnimalInLocalRepository(nonHumanAnimal) {
                    if (it > 0) {
                        log.d(
                            "CheckNonHumanAnimalsViewmodel",
                            "Non human animal ${nonHumanAnimal.id} modified in local database"
                        )
                    } else {
                        log.e(
                            "CheckNonHumanAnimalsViewmodel",
                            "Error modifying the non human animal ${nonHumanAnimal.id} in local database"
                        )
                    }
                }
                nonHumanAnimal
            }
        }
}