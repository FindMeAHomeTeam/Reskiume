import Foundation
import ComposeApp

@MainActor
final class FirebaseDatabaseManager {
    static let shared = FirebaseDatabaseManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseDatabaseManager")

    private init() {}
    
    private var realtimeDatabaseRepositoryIosHelper: RealtimeDatabaseRepositoryIosHelper?
    private var realtimeDatabaseRemoteUserRepositoryForIosDelegate: RealtimeDatabaseRemoteUserRepositoryForIosDelegate?
    private var realtimeDatabaseRepositoryForIosDelegate: RealtimeDatabaseRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        realtimeDatabaseRepositoryIosHelper = RealtimeDatabaseRepositoryIosHelper()
        
        realtimeDatabaseRemoteUserRepositoryForIosDelegate =
        realtimeDatabaseRepositoryIosHelper!.realtimeDatabaseRemoteUserRepositoryForIosDelegate
        
        realtimeDatabaseRepositoryForIosDelegate =
        RealtimeDatabaseRepositoryForIosDelegateImpl(
            realtimeDatabaseRemoteUserRepositoryForIosDelegate: realtimeDatabaseRemoteUserRepositoryForIosDelegate!,
            log: realtimeDatabaseRepositoryIosHelper!.log
        )
        
        realtimeDatabaseRepositoryIosHelper!.realtimeDatabaseRepositoryForIosDelegateWrapper
            .updateRealtimeDatabaseRepositoryForIosDelegate(delegate: realtimeDatabaseRepositoryForIosDelegate)
    }
}
