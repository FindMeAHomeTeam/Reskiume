import Foundation
import ComposeApp

@MainActor
final class FirebaseRemoteNonHumanAnimalDatabaseManager {
    static let shared = FirebaseRemoteNonHumanAnimalDatabaseManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseRemoteNonHumanAnimalDatabaseManager")

    private init() {}
    
    private var realtimeDatabaseRemoteNonHumanAnimalRepositoryIosHelper: RealtimeDatabaseRemoteNonHumanAnimalRepositoryIosHelper?
    private var realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate?
    private var realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate: RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        realtimeDatabaseRemoteNonHumanAnimalRepositoryIosHelper = RealtimeDatabaseRemoteNonHumanAnimalRepositoryIosHelper()
        
        realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate =
        realtimeDatabaseRemoteNonHumanAnimalRepositoryIosHelper!.realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate
        
        realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate =
        RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateImpl(
            realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate: realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate!,
            log: realtimeDatabaseRemoteNonHumanAnimalRepositoryIosHelper!.log
        )
        
        realtimeDatabaseRemoteNonHumanAnimalRepositoryIosHelper!.realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateWrapper
            .updateRealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate(delegate: realtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate)
    }
}
