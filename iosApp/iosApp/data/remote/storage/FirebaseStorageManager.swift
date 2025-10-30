import Foundation
import ComposeApp

@MainActor
final class FirebaseStorageManager {
    static let shared = FirebaseStorageManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseStorageManager")

    private init() {}
    
    private var storageRepositoryForIosHelper: StorageRepositoryForIosHelper?
    private var storageRepository: StorageRepository?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        storageRepositoryForIosHelper = StorageRepositoryForIosHelper()
        storageRepository = StorageRepositoryForIosDelegateImpl()
        storageRepositoryForIosHelper!.storageRepositoryForIosDelegateWrapper.updateStorageRepositoryForIosDelegate(delegate: storageRepository)
    }
}
