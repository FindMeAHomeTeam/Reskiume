import Foundation
import ComposeApp

@MainActor
final class FirebaseRemoteChatFireStoreManager {
    static let shared = FirebaseRemoteChatFireStoreManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseRemoteChatFireStoreManager")

    private init() {}
    
    private var fireStoreRemoteChatRepositoryIosHelper: FireStoreRemoteChatRepositoryIosHelper?
    private var fireStoreRemoteChatFlowsRepositoryForIosDelegate: FireStoreRemoteChatFlowsRepositoryForIosDelegate?
    private var fireStoreRemoteChatRepositoryForIosDelegate: FireStoreRemoteChatRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        fireStoreRemoteChatRepositoryIosHelper = FireStoreRemoteChatRepositoryIosHelper()
        
        fireStoreRemoteChatFlowsRepositoryForIosDelegate =
        fireStoreRemoteChatRepositoryIosHelper!.fireStoreRemoteChatFlowsRepositoryForIosDelegate
        
        fireStoreRemoteChatRepositoryForIosDelegate =
        FireStoreRemoteChatRepositoryForIosDelegateImpl(
            fireStoreRemoteChatFlowsRepositoryForIosDelegate: fireStoreRemoteChatFlowsRepositoryForIosDelegate!,
            log: fireStoreRemoteChatRepositoryIosHelper!.log
        )
        
        fireStoreRemoteChatRepositoryIosHelper!.fireStoreRemoteChatRepositoryForIosDelegateWrapper
            .updateFireStoreRemoteChatRepositoryForIosDelegate(delegate: fireStoreRemoteChatRepositoryForIosDelegate)
    }
}
