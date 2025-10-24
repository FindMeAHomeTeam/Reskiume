import ComposeApp
import FirebaseDatabaseInternal
import FirebaseCore
import KMPNativeCoroutinesAsync


class RealtimeDatabaseRepositoryForIosDelegateImpl: RealtimeDatabaseRepositoryForIosDelegate {
    
    private var databaseReference: DatabaseReference?
    
    private let userUidTaskHandle: Task<(), Never>?
            
    init (realtimeDatabaseRemoteUserRepositoryForIosDelegate: RealtimeDatabaseRemoteUserRepositoryForIosDelegate) {
        
        let database: Database! = Database.database()
        database.isPersistenceEnabled = true
        databaseReference = database.reference()
        
        userUidTaskHandle = Task {
            do {
                let emittedValues = asyncSequence(for: realtimeDatabaseRemoteUserRepositoryForIosDelegate.userUidStateFlow)
                for try await userUid in emittedValues {
                    
                    if userUid == "" { return }
                    
                    database.reference().child(Paths.users.path).child(userUid).observeSingleEvent (of: .value, with: { snapshot in
                        let nSDictionary: NSDictionary? = snapshot.value as? NSDictionary
                        let remoteUser: RemoteUser = RemoteUser(
                            uid: nSDictionary?["uid"] as? String ?? "",
                            username: nSDictionary?["username"] as? String ?? "",
                            description: nSDictionary?["description"] as? String ?? "",
                            email: nSDictionary?["email"] as? String ?? "",
                            image: nSDictionary?["image"] as? String ?? "",
                            isAvailable: nSDictionary?["isAvailable"] as? KotlinBoolean ?? false
                        )
                        realtimeDatabaseRemoteUserRepositoryForIosDelegate.updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: remoteUser)
                        
                    }) { error in
                        Log().e(tag: "RealtimeDatabaseRepositoryIos", message: "Error retrieving the remote user id \(userUid): \(error.localizedDescription)", throwable: nil)
                        realtimeDatabaseRemoteUserRepositoryForIosDelegate.updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: nil)
                    }
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    deinit {
        userUidTaskHandle?.cancel()
    }
    
    func insertRemoteUser(remoteUser: RemoteUser, onInsertRemoteUser: @escaping (DatabaseResult) -> Void) async {
        do {
            try await databaseReference!.child(Paths.users.path).child(remoteUser.uid!).setValue(remoteUser.toMap())
            onInsertRemoteUser(DatabaseResult.Success())
        } catch {
            Log().e(tag: "RealtimeDatabaseRepositoryIos", message: "Error inserting the remote user \(String(describing: remoteUser.uid))", throwable: nil)
            onInsertRemoteUser(DatabaseResult.Error(message: "Error inserting the remote user \(String(describing: remoteUser.uid))"))
        }
    }
    
    func updateRemoteUser(remoteUser: RemoteUser, onUpdateRemoteUser: @escaping (DatabaseResult) -> Void) async {
        guard let key: String = databaseReference!.child(Paths.users.path).childByAutoId().key else {
            Log().w(tag: "RealtimeDatabaseRepositoryForIosDelegateImpl", message: "Couldn't get push key for users")
            return
        }
        let remoteUserValues = remoteUser.toMap()
        let childUpdates = ["/\(Paths.users.path)/\(key)": remoteUserValues]

        do {
            try await databaseReference?.updateChildValues(childUpdates)
            onUpdateRemoteUser(DatabaseResult.Success())
        } catch {
            Log().e(tag: "RealtimeDatabaseRepositoryIos", message: "Error updating the remote user \(String(describing: remoteUser.uid))", throwable: nil)
            onUpdateRemoteUser(DatabaseResult.Error(message: "Error updating the remote user \(String(describing: remoteUser.uid))"))
        }
    }
    
    func deleteRemoteUser(uid: String, onDeleteRemoteUser: @escaping (DatabaseResult) -> Void) {
        databaseReference!.child(Paths.users.path).child(uid).removeValue { error, _ in
            if (error == nil) {
                onDeleteRemoteUser(DatabaseResult.Success())
            } else {
                Log().e(tag: "RealtimeDatabaseRepositoryIos", message: "Error deleting the remote user \(uid): \(String(describing: error))", throwable: nil)
                onDeleteRemoteUser(DatabaseResult.Error(message: "Error deleting the remote user \(uid): \(String(describing: error))"))
            }
        }
    }
}
