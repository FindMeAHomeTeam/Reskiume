package com.findmeahometeam.reskiume.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.findmeahometeam.reskiume.data.database.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert
    suspend fun insertUserReview(review: ReviewEntity): Long

    @Query("SELECT * FROM ReviewEntity WHERE reviewedUid = :reviewedUserUid")
    fun getUserReviews(reviewedUserUid: String): Flow<List<ReviewEntity>>

    @Query("DELETE FROM ReviewEntity WHERE reviewedUid = :reviewedUserUid")
    suspend fun deleteUserReviews(reviewedUserUid: String): Int
}
