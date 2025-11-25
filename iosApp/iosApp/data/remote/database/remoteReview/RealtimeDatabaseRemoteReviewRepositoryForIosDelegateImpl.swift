import ComposeApp
import FirebaseDatabaseInternal
import FirebaseCore
import KMPNativeCoroutinesAsync


class RealtimeDatabaseRemoteReviewRepositoryForIosDelegateImpl: RealtimeDatabaseRemoteReviewRepositoryForIosDelegate {

    private var databaseReference: DatabaseReference?
    
    private let reviewUidTaskHandle: Task<(), Never>?
    
    private var log: Log
            
    init (
        realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate,
        log: Log
    ) {
        self.log = log
        let database: Database! = Database.database()
        database.isPersistenceEnabled = true
        databaseReference = database.reference()
        
        reviewUidTaskHandle = Task {
            do {
                let emittedValues = asyncSequence(for: realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate.reviewedUidFlow)
                for try await reviewedUid in emittedValues {
                    
                    if reviewedUid != "" {
                        database.reference().child(Section.reviews.path).child(reviewedUid).observeSingleEvent (of: .value, with: { snapshot in
                            var remoteReviews: [RemoteReview] = []

                            for review in snapshot.children {
                                guard let reviewSnapshot = review as? DataSnapshot,
                                      let reviewNsDictionary = reviewSnapshot.value as? [String: Any] else {
                                    continue
                                }
                                let remoteReview = RemoteReview(
                                    id: reviewNsDictionary["id"] as? String ?? "",
                                    timestamp: KotlinLong(longLong: Int64(reviewNsDictionary["timestamp"] as? CLong ?? 0)),
                                    authorUid: reviewNsDictionary["authorUid"] as? String ?? "",
                                    reviewedUid: reviewNsDictionary["reviewedUid"] as? String ?? "",
                                    description: reviewNsDictionary["description"] as? String ?? "",
                                    rating: KotlinFloat(float: reviewNsDictionary["rating"] as? Float ?? 0)
                                )
                                remoteReviews.append(remoteReview)
                            }
                            realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate.updateRealtimeDatabaseRemoteReviewsRepositoryForIosDelegate(delegate: remoteReviews)
                            
                        }) { error in
                            log.e(tag: "RealtimeDatabaseRemoteReviewRepositoryForIosDelegateImpl", message: "Error retrieving the remote review for user id \(reviewedUid): \(error.localizedDescription)", throwable: nil)
                            realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate.updateRealtimeDatabaseRemoteReviewsRepositoryForIosDelegate(delegate: [])
                        }
                    }
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    deinit {
        reviewUidTaskHandle?.cancel()
    }
    
    private func getNSDictionaryFromRemoteReview(remoteReview: RemoteReview) -> NSDictionary {
        return [
            "id": remoteReview.id!,
            "timestamp": remoteReview.timestamp!,
            "authorUid": remoteReview.authorUid!,
            "reviewedUid": remoteReview.reviewedUid!,
            "description": remoteReview.description_!,
            "rating": remoteReview.rating!
        ]
    }
    
    func insertRemoteReview(remoteReview: RemoteReview, onInsertRemoteReview: @escaping (DatabaseResult) -> Void) async {
        do {
            try await databaseReference!.child(Section.reviews.path).child(remoteReview.reviewedUid!).child(remoteReview.id!).setValue(getNSDictionaryFromRemoteReview(remoteReview: remoteReview))
            onInsertRemoteReview(DatabaseResult.Success())
        } catch {
            log.e(tag: "RealtimeDatabaseRemoteReviewRepositoryForIosDelegateImpl", message: "Error inserting the remote review \(String(describing: remoteReview.timestamp))", throwable: nil)
            onInsertRemoteReview(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func deleteRemoteReviews(reviewedUid: String, onDeletedRemoteReviews: @escaping (DatabaseResult) -> Void) {
        databaseReference!.child(Section.reviews.path).child(reviewedUid).removeValue { error, _ in
            if (error == nil) {
                onDeletedRemoteReviews(DatabaseResult.Success())
            } else {
                self.log.e(tag: "RealtimeDatabaseRemoteReviewRepositoryForIosDelegateImpl", message: "Error deleting the remote review from the user \(reviewedUid): \(String(describing: error))", throwable: nil)
                onDeletedRemoteReviews(DatabaseResult.Error(message: String(describing: error)))
            }
        }
    }
}
