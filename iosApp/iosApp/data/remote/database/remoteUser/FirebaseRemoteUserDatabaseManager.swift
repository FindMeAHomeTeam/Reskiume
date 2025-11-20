import Foundation
import ComposeApp

@MainActor
final class FirebaseRemoteUserDatabaseManager {
    static let shared = FirebaseRemoteUserDatabaseManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseRemoteUserDatabaseManager")

    private init() {}
    
    private var realtimeDatabaseRemoteUserRepositoryIosHelper: RealtimeDatabaseRemoteUserRepositoryIosHelper?
    private var realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate?
    private var realtimeDatabaseRemoteUserRepositoryForIosDelegate: RealtimeDatabaseRemoteUserRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        realtimeDatabaseRemoteUserRepositoryIosHelper = RealtimeDatabaseRemoteUserRepositoryIosHelper()
        
        realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate =
        realtimeDatabaseRemoteUserRepositoryIosHelper!.realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate
        
        realtimeDatabaseRemoteUserRepositoryForIosDelegate =
        RealtimeDatabaseRemoteUserRepositoryForIosDelegateImpl(
            realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate: realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate!,
            log: realtimeDatabaseRemoteUserRepositoryIosHelper!.log
        )
        
        realtimeDatabaseRemoteUserRepositoryIosHelper!.realtimeDatabaseRemoteUserRepositoryForIosDelegateWrapper
            .updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate_: realtimeDatabaseRemoteUserRepositoryForIosDelegate)
    }
}
