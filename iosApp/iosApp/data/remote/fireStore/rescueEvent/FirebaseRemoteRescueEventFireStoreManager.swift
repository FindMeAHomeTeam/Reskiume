import Foundation
import ComposeApp

@MainActor
final class FirebaseRemoteRescueEventFireStoreManager {
    static let shared = FirebaseRemoteRescueEventFireStoreManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseRemoteRescueEventFireStoreManager")

    private init() {}
    
    private var fireStoreRemoteRescueEventRepositoryIosHelper: FireStoreRemoteRescueEventRepositoryIosHelper?
    private var fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate?
    private var fireStoreRemoteRescueEventRepositoryForIosDelegate: FireStoreRemoteRescueEventRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        fireStoreRemoteRescueEventRepositoryIosHelper = FireStoreRemoteRescueEventRepositoryIosHelper()
        
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate =
        fireStoreRemoteRescueEventRepositoryIosHelper!.fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
        
        fireStoreRemoteRescueEventRepositoryForIosDelegate =
        FireStoreRemoteRescueEventRepositoryForIosDelegateImpl(
            fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate!,
            log: fireStoreRemoteRescueEventRepositoryIosHelper!.log
        )
        
        fireStoreRemoteRescueEventRepositoryIosHelper!.fireStoreRemoteRescueEventRepositoryForIosDelegateWrapper
            .updateFireStoreRemoteRescueEventRepositoryForIosDelegate(delegate: fireStoreRemoteRescueEventRepositoryForIosDelegate)
    }
}
