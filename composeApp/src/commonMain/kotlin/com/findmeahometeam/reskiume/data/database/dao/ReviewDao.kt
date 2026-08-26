package com.findmeahometeam.reskiume.data.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy.Companion.REPLACE
import androidx.room3.Query
import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertLocalReview(review: ReviewEntity): Long

    @Query("SELECT * FROM ReviewEntity WHERE reviewedUid = :reviewedUserUid")
    fun getLocalReviews(reviewedUserUid: String): Flow<List<ReviewEntity>>

    @Query("DELETE FROM ReviewEntity WHERE reviewedUid = :reviewedUserUid OR savedBy = :reviewedUserUid OR savedBy = ' ' OR savedBy = '' ")
    suspend fun deleteLocalReviews(reviewedUserUid: String): Int
}
