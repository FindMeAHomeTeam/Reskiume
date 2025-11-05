import Foundation
import FirebaseAuth
import ComposeApp
import KMPNativeCoroutinesAsync

class AuthRepositoryForIosDelegateImpl: AuthRepositoryForIosDelegate {
    
    private let auth = Auth.auth()
    
    private var listenerHandle: AuthStateDidChangeListenerHandle? = nil
    
    var authUser: AuthUser?
    
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
    
    private func reauthenticateUser(password: String, onReauthenticate: @escaping (FirebaseAuth.User?) -> Void) async throws {
        guard let user: FirebaseAuth.User = auth.currentUser, let email = user.email else {
            Log().e(tag: "AuthRepositoryForIosDelegateImpl", message: "reauthenticateUser: Not signed in or user has no email", throwable: nil)
            onReauthenticate(nil)
            return
        }
        
        do {
            let credential = EmailAuthProvider.credential(withEmail: email, password: password)
            try await user.reauthenticate(with: credential)
            onReauthenticate(user)
        } catch {
            Log().e(tag: "AuthRepositoryForIosDelegateImpl", message: "reauthenticateUser: \(error.localizedDescription)", throwable: nil)
            onReauthenticate(nil)
        }
    }
    
    func deleteUser(password: String, onDeleteUser: @escaping (String) -> Void) async throws {
        do {
            try await reauthenticateUser (password: password) { user in
                guard let user = user else {
                    onDeleteUser("AuthRepositoryForIosDelegateImpl - deleteUser: Error reauthenticating user")
                    return
                }
                user.delete { error in
                    if let error = error {
                        Log().e(tag: "AuthRepositoryForIosDelegateImpl", message: "deleteUser: \(error.localizedDescription)", throwable: nil)
                        onDeleteUser(error.localizedDescription)
                    } else {
                        onDeleteUser("")
                    }
                }
            }
        } catch {
            Log().e(tag: "AuthRepositoryForIosDelegateImpl", message: "deleteUser: \(error.localizedDescription)", throwable: nil)
            onDeleteUser(error.localizedDescription)
        }
    }
    
    func updateUserEmail(password: String, newEmail: String, onUpdatedUserEmail: @escaping (String) -> Void) async throws {
        do {
            try await reauthenticateUser (password: password) { user in
                guard let user = user else {
                    onUpdatedUserEmail("AuthRepositoryForIosDelegateImpl - updateUserEmail: Error reauthenticating user")
                    return
                }
                user.sendEmailVerification(beforeUpdatingEmail: newEmail) { error in
                    if let error = error {
                        Log().e(tag: "AuthRepositoryForIosDelegateImpl", message: "updateUserEmail: \(error.localizedDescription)", throwable: nil)
                        onUpdatedUserEmail(error.localizedDescription)
                    } else {
                        onUpdatedUserEmail("")
                    }
                }
            }
        } catch {
            Log().e(tag: "AuthRepositoryForIosDelegateImpl", message: "updateUserEmail: \(error.localizedDescription)", throwable: nil)
            onUpdatedUserEmail(error.localizedDescription)
        }
    }
    
    func updateUserPassword(currentPassword: String, newPassword: String, onUpdatedUserPassword: @escaping (String) -> Void) async throws {
        do {
            try await reauthenticateUser (password: currentPassword) { user in
                guard let user = user else {
                    onUpdatedUserPassword("AuthRepositoryForIosDelegateImpl - updateUserPassword: Error reauthenticating user")
                    return
                }
                user.updatePassword(to: newPassword) { error in
                    if let error = error {
                        Log().e(tag: "AuthRepositoryForIosDelegateImpl", message: "updateUserPassword: \(error.localizedDescription)", throwable: nil)
                        onUpdatedUserPassword(error.localizedDescription)
                    } else {
                        onUpdatedUserPassword("")
                    }
                }
            }
        } catch {
            Log().e(tag: "AuthRepositoryForIosDelegateImpl", message: "updateUserPassword: \(error.localizedDescription)", throwable: nil)
            onUpdatedUserPassword(error.localizedDescription)
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
