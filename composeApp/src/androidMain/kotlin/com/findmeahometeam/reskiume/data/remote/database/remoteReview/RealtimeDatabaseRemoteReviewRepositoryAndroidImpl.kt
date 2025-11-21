package com.findmeahometeam.reskiume.data.remote.database.remoteReview

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.RemoteReview
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.database.remoteReview.RealtimeDatabaseRemoteReviewRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealtimeDatabaseRemoteReviewRepositoryAndroidImpl(
    private val log: Log
) : RealtimeDatabaseRemoteReviewRepository {

    private val databaseRef: DatabaseReference =
        Firebase.database.also { it.setPersistenceEnabled(true) }.reference

    override suspend fun insertRemoteReview(
        remoteReview: RemoteReview,
        onInsertRemoteReview: (result: DatabaseResult) -> Unit
    ) {
        if (remoteReview.timestamp != null
            && remoteReview.timestamp > 0L
            && remoteReview.reviewedUid != null
        ) {
            databaseRef.child(Section.REVIEWS.path).child(remoteReview.reviewedUid)
                .setValue(remoteReview.toMap())
                .addOnSuccessListener {
                    onInsertRemoteReview(DatabaseResult.Success)
                }.addOnFailureListener { e ->
                    log.e(
                        "RealtimeDatabaseRemoteReviewRepositoryAndroidImpl",
                        "insertRemoteReview: Error inserting the remote review ${remoteReview.timestamp}: ${e.message}"
                    )
                    onInsertRemoteReview(DatabaseResult.Error(e.message ?: ""))
                }
        } else {
            onInsertRemoteReview(DatabaseResult.Error()).also {
                log.e(
                    "RealtimeDatabaseRemoteReviewRepositoryAndroidImpl",
                    "insertRemoteReview: Error inserting a remote review"
                )
            }
        }
    }

    override fun getRemoteReviews(reviewedUid: String): Flow<List<RemoteReview>> =
        callbackFlow {
            val reviewListener: ValueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val remoteReview: List<RemoteReview>? =
                        dataSnapshot.getValue<List<RemoteReview>>()
                    trySend(remoteReview ?: emptyList())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    log.e(
                        "RealtimeDatabaseRemoteReviewRepositoryAndroidImpl",
                        "getRemoteReviews:onCancelled ${databaseError.toException()}"
                    )
                }
            }
            databaseRef.child(Section.REVIEWS.path).child(reviewedUid)
                .addListenerForSingleValueEvent(reviewListener)
            awaitClose {
                databaseRef.child(Section.REVIEWS.path).child(reviewedUid)
                    .removeEventListener(reviewListener)
            }
        }

    override fun deleteRemoteReviews(
        reviewedUid: String,
        onDeletedRemoteReviews: (result: DatabaseResult) -> Unit
    ) {
        databaseRef.child(Section.REVIEWS.path).child(reviewedUid).removeValue { error, _ ->
            if (error == null) {
                onDeletedRemoteReviews(DatabaseResult.Success)
            } else {
                log.e(
                    "RealtimeDatabaseRemoteReviewRepositoryAndroidImpl",
                    "deleteRemoteReviews: Error deleting the reviews from the user $reviewedUid"
                )
                onDeletedRemoteReviews(DatabaseResult.Error())
            }
        }
    }
}
