import Foundation
import ComposeApp

@MainActor
final class FCMManager {
    static let shared = FCMManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FCMManager")

    private init() {}
    
    private var fCMSubscriberRepositoryIosHelper: FCMSubscriberRepositoryIosHelper?
    private var fCMSubscriberRepositoryForIosDelegate: FCMSubscriberRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        fCMSubscriberRepositoryIosHelper = FCMSubscriberRepositoryIosHelper()

        fCMSubscriberRepositoryForIosDelegate = FCMSubscriberRepositoryForIosDelegateImpl(log: fCMSubscriberRepositoryIosHelper!.log)
        
        fCMSubscriberRepositoryIosHelper!.fCMSubscriberRepositoryForIosDelegateWrapper
            .updateFCMSubscriberRepositoryForIosDelegate(delegate: fCMSubscriberRepositoryForIosDelegate)
    }
}
