import Foundation
import ComposeApp

@MainActor
final class FirebaseAuthManager {
    static let shared = FirebaseAuthManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseAuthManager")

    private init() {}
    
    private var authRepositoryIosHelper: AuthRepositoryIosHelper?
    private var authUserRepositoryForIosDelegate: AuthUserRepositoryForIosDelegate?
    private var authRepositoryForIosDelegate: AuthRepositoryForIosDelegate?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        authRepositoryIosHelper = AuthRepositoryIosHelper()
        
        authUserRepositoryForIosDelegate =
        authRepositoryIosHelper!.authUserRepositoryForIosDelegate
        
        authRepositoryForIosDelegate =
        AuthRepositoryForIosDelegateImpl(authUserRepositoryForIosDelegate: authUserRepositoryForIosDelegate!)
        
        authRepositoryIosHelper!.authRepositoryForIosDelegateWrapper.updateAuthRepositoryForIosDelegate(delegate: authRepositoryForIosDelegate)
    }
}
