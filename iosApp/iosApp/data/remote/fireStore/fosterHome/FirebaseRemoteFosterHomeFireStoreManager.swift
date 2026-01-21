import Foundation
import ComposeApp

@MainActor
final class FirebaseRemoteFosterHomeFireStoreManager {
    static let shared = FirebaseRemoteFosterHomeFireStoreManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseRemoteFosterHomeFireStoreManager")

    private init() {}
    
    private var fireStoreRemoteFosterHomeRepositoryIosHelper: FireStoreRemoteFosterHomeRepositoryIosHelper?
    private var fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate?
    private var fireStoreRemoteFosterHomeRepositoryForIosDelegate: FireStoreRemoteFosterHomeRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        fireStoreRemoteFosterHomeRepositoryIosHelper = FireStoreRemoteFosterHomeRepositoryIosHelper()
        
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate =
        fireStoreRemoteFosterHomeRepositoryIosHelper!.fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
        
        fireStoreRemoteFosterHomeRepositoryForIosDelegate =
        FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl(
            fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate!,
            log: fireStoreRemoteFosterHomeRepositoryIosHelper!.log
        )
        
        fireStoreRemoteFosterHomeRepositoryIosHelper!.fireStoreRemoteFosterHomeRepositoryForIosDelegateWrapper
            .updateFireStoreRemoteFosterHomeRepositoryForIosDelegate(delegate: fireStoreRemoteFosterHomeRepositoryForIosDelegate)
    }
}
