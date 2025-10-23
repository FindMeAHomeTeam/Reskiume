import Foundation
import FirebaseAuth
import ComposeApp
import KMPNativeCoroutinesAsync

class AuthRepositoryForIosDelegateImpl: AuthRepositoryForIosDelegate, ObservableObject {
    
    private let auth = Auth.auth()
    
    private var listenerHandle: AuthStateDidChangeListenerHandle? = nil
    
    @Published var authUser: AuthUser?
    
    init (authUserRepositoryForIosDelegate: AuthUserRepositoryForIosDelegate) {
        listenerHandle = auth.addStateDidChangeListener { [weak self] _, user in
            guard let self = self else { return }
            
            if let firebaseUser = user {
                self.authUser = AuthUser(
                    uid: firebaseUser.uid,
                    name: firebaseUser.displayName,
                    email: firebaseUser.email,
                    photoUrl: firebaseUser.photoURL?.absoluteString
                )
            } else {
                self.authUser = nil
            }
            authUserRepositoryForIosDelegate.updateAuthUserDelegate(delegate: self.authUser)
        }
    }
    
    deinit {
        if let handle = listenerHandle {
            auth.removeStateDidChangeListener(handle)
        }
    }

    func createUserWithEmailAndPassword(email: String, password: String) async throws -> any AuthResult {
        do {
            let result = try await auth.createUser(withEmail: email, password: password)
            let user = result.user
            let authUser = AuthUser(uid: user.uid, name: user.displayName, email: user.email, photoUrl: user.photoURL?.absoluteString)
            return AuthResultSuccess(user: authUser)
        } catch {
            return AuthResultError(message: error.localizedDescription, cause: nil)
        }
    }
    
    func signInWithEmailAndPassword(email: String, password: String) async throws -> any AuthResult {
        do {
            let result = try await auth.signIn(withEmail: email, password: password)
            let user = result.user
            let authUser = AuthUser(uid: user.uid, name: user.displayName, email: user.email, photoUrl: user.photoURL?.absoluteString)
            return AuthResultSuccess(user: authUser)
        } catch {
            return AuthResultError(message: error.localizedDescription, cause: KotlinThrowable?.none)
        }
    }
    
    func deleteUser(password: String, onDeleteUser: @escaping (String, String) -> Void) async throws {
        guard let user = auth.currentUser, let email = user.email else {
            onDeleteUser("", "Not signed in or user has no email")
            return
        }
        
        do {
            let credential = EmailAuthProvider.credential(withEmail: email, password: password)
            try await user.reauthenticate(with: credential)
            try await user.delete()
            onDeleteUser(user.uid, "")
        } catch {
            onDeleteUser("", error.localizedDescription)
        }
    }
    
    func signOut() -> Bool {
        do {
            try auth.signOut()
            return true
        } catch {
            print("Error signing out: \(error.localizedDescription)")
            return false
        }
    }
}
