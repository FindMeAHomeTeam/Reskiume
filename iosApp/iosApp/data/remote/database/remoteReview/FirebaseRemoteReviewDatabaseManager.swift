import Foundation
import ComposeApp

@MainActor
final class FirebaseRemoteReviewDatabaseManager {
    static let shared = FirebaseRemoteReviewDatabaseManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseRemoteReviewDatabaseManager")

    private init() {}
    
    private var realtimeDatabaseRemoteReviewRepositoryIosHelper: RealtimeDatabaseRemoteReviewRepositoryIosHelper?
    private var realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate?
    private var realtimeDatabaseRemoteReviewRepositoryForIosDelegate: RealtimeDatabaseRemoteReviewRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        realtimeDatabaseRemoteReviewRepositoryIosHelper = RealtimeDatabaseRemoteReviewRepositoryIosHelper()
        
        realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate =
        realtimeDatabaseRemoteReviewRepositoryIosHelper!.realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate
        
        realtimeDatabaseRemoteReviewRepositoryForIosDelegate =
        RealtimeDatabaseRemoteReviewRepositoryForIosDelegateImpl(
            realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate: realtimeDatabaseRemoteReviewFlowsRepositoryForIosDelegate!,
            log: realtimeDatabaseRemoteReviewRepositoryIosHelper!.log
        )
        
        realtimeDatabaseRemoteReviewRepositoryIosHelper!.realtimeDatabaseRemoteReviewRepositoryForIosDelegateWrapper
            .updateRealtimeDatabaseRemoteReviewRepositoryForIosDelegate(delegate: realtimeDatabaseRemoteReviewRepositoryForIosDelegate)
    }
}
