import ComposeApp
import FirebaseDatabaseInternal
import FirebaseCore
import KMPNativeCoroutinesAsync


class RealtimeDatabaseRepositoryForIosDelegateImpl: RealtimeDatabaseRepositoryForIosDelegate {
    
    private var databaseReference: DatabaseReference?
    
    private let userUidTaskHandle: Task<(), Never>?
    
    private var log: Log
            
    init (
        realtimeDatabaseRemoteUserRepositoryForIosDelegate: RealtimeDatabaseRemoteUserRepositoryForIosDelegate,
        log: Log
    ) {
        self.log = log
        let database: Database! = Database.database()
        database.isPersistenceEnabled = true
        databaseReference = database.reference()
        
        userUidTaskHandle = Task {
            do {
                let emittedValues = asyncSequence(for: realtimeDatabaseRemoteUserRepositoryForIosDelegate.userUidFlow)
                for try await userUid in emittedValues {
                    
                    if userUid != "" {
                        database.reference().child(Paths.users.path).child(userUid).observeSingleEvent (of: .value, with: { snapshot in
                            let nSDictionary: NSDictionary? = snapshot.value as? NSDictionary
                            
                            let availableAny = nSDictionary?["available"]

                            // Support NSNumber, DarwinBoolean, or Bool
                            let availableBool: Bool? =
                                (availableAny as? NSNumber)?.boolValue ??
                                (availableAny as? DarwinBoolean)?.boolValue ??
                                (availableAny as? Bool)
                            
                            let remoteUser: RemoteUser = RemoteUser(
                                uid: nSDictionary?["uid"] as? String ?? "",
                                username: nSDictionary?["username"] as? String ?? "",
                                description: nSDictionary?["description"] as? String ?? "",
                                image: nSDictionary?["image"] as? String ?? "",
                                available: KotlinBoolean(value: availableBool ?? false)
                            )
                            realtimeDatabaseRemoteUserRepositoryForIosDelegate.updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: remoteUser)
                            
                        }) { error in
                            log.e(tag: "RealtimeDatabaseRepositoryIos", message: "Error retrieving the remote user id \(userUid): \(error.localizedDescription)", throwable: nil)
                            realtimeDatabaseRemoteUserRepositoryForIosDelegate.updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: nil)
                        }
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
    
    private func getNSDictionaryFromRemoteUser(remoteUser: RemoteUser) -> NSDictionary {
        return [
            "uid": remoteUser.uid!,
            "username": remoteUser.username!,
            "description": remoteUser.description_!,
            "image": remoteUser.image!,
            "available": remoteUser.available?.boolValue == true
        ]
    }
    
    func insertRemoteUser(remoteUser: RemoteUser, onInsertRemoteUser: @escaping (DatabaseResult) -> Void) async {
        do {
            try await databaseReference!.child(Paths.users.path).child(remoteUser.uid!).setValue(getNSDictionaryFromRemoteUser(remoteUser: remoteUser))
            onInsertRemoteUser(DatabaseResult.Success())
        } catch {
            log.e(tag: "RealtimeDatabaseRepositoryIos", message: "Error inserting the remote user \(String(describing: remoteUser.uid))", throwable: nil)
            onInsertRemoteUser(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func updateRemoteUser(remoteUser: RemoteUser, onUpdateRemoteUser: @escaping (DatabaseResult) -> Void) async {
        let remoteUserValues = getNSDictionaryFromRemoteUser(remoteUser: remoteUser)
        let childUpdates = ["/\(Paths.users.path)/\(remoteUser.uid!)": remoteUserValues]

        do {
            try await databaseReference?.updateChildValues(childUpdates)
            onUpdateRemoteUser(DatabaseResult.Success())
        } catch {
            log.e(tag: "RealtimeDatabaseRepositoryIos", message: "Error updating the remote user \(String(describing: remoteUser.uid))", throwable: nil)
            onUpdateRemoteUser(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func deleteRemoteUser(uid: String, onDeleteRemoteUser: @escaping (DatabaseResult) -> Void) {
        databaseReference!.child(Paths.users.path).child(uid).removeValue { error, _ in
            if (error == nil) {
                onDeleteRemoteUser(DatabaseResult.Success())
            } else {
                self.log.e(tag: "RealtimeDatabaseRepositoryIos", message: "Error deleting the remote user \(uid): \(String(describing: error))", throwable: nil)
                onDeleteRemoteUser(DatabaseResult.Error(message: String(describing: error)))
            }
        }
    }
}
