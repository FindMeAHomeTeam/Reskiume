package com.findmeahometeam.reskiume.ui.profile.checkAllMyFosterHomes

import androidx.lifecycle.ViewModel
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.domain.model.fosterHome.FosterHome
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.fosterHome.GetAllMyFosterHomesFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetCompleteImagePathFromLocalDataSource
import com.findmeahometeam.reskiume.domain.usecases.localCache.GetDataByManagingObjectLocalCacheTimestamp
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAllMyFosterHomes
import com.findmeahometeam.reskiume.ui.core.navigation.SaveStateHandleProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CheckAllMyFosterHomesViewmodel(
    saveStateHandleProvider: SaveStateHandleProvider,
    private val getDataByManagingObjectLocalCacheTimestamp: GetDataByManagingObjectLocalCacheTimestamp,
    private val getAllMyFosterHomesFromRemoteRepository: GetAllMyFosterHomesFromRemoteRepository,
    private val checkAllMyFosterHomesUtil: CheckAllMyFosterHomesUtil,
    private val getAllMyFosterHomesFromLocalRepository: GetAllMyFosterHomesFromLocalRepository,
    private val getCompleteImagePathFromLocalDataSource: GetCompleteImagePathFromLocalDataSource
) : ViewModel() {

    private val myUid =
        saveStateHandleProvider.provideObjectRoute(CheckAllMyFosterHomes::class).myUid

    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchAllMyFosterHomes(): Flow<UiState<List<FosterHome>>> =
        flowOf(myUid)
            .flatMapConcat { myUid: String ->

                getDataByManagingObjectLocalCacheTimestamp(
                    cachedObjectId = myUid,
                    savedBy = myUid,
                    section = Section.FOSTER_HOMES,
                    onCompletionInsertCache = {
                        val allFosterHomesFlow: Flow<List<FosterHome>> = getAllMyFosterHomesFromRemoteRepository(
                            myUid
                        )
                        checkAllMyFosterHomesUtil.downloadImageAndManageFosterHomesInLocalRepositoryFromFlow(
                            allFosterHomesFlow,
                            myUid
                        )
                    },
                    onCompletionUpdateCache = {
                        val allFosterHomesFlow: Flow<List<FosterHome>> = getAllMyFosterHomesFromRemoteRepository(
                            myUid
                        )
                        checkAllMyFosterHomesUtil.downloadImageAndModifyFosterHomesInLocalRepositoryFromFlow(
                            allFosterHomesFlow,
                            myUid
                        )
                    },
                    onVerifyCacheIsRecent = {
                        getAllMyFosterHomesFromLocalRepository(myUid)
                    }
                ).map {
                    it.map { fosterHome ->
                        fosterHome.copy(
                            imageUrl = if (fosterHome.imageUrl.isEmpty()) {
                                fosterHome.imageUrl
                            } else {
                                getCompleteImagePathFromLocalDataSource(fosterHome.imageUrl)
                            }
                        )
                    }.sortedBy { fosterHome -> fosterHome.available }
                }
            }.toUiState()
}
