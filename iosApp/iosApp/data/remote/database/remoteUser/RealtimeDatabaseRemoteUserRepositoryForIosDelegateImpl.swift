import ComposeApp
import FirebaseDatabaseInternal
import FirebaseCore
import KMPNativeCoroutinesAsync


class RealtimeDatabaseRemoteUserRepositoryForIosDelegateImpl: RealtimeDatabaseRemoteUserRepositoryForIosDelegate {
    
    private var databaseReference: DatabaseReference?
    
    private let userUidTaskHandle: Task<(), Never>?
    
    private var log: Log
            
    init (
        realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate,
        log: Log
    ) {
        self.log = log
        let database: Database! = Database.database()
        databaseReference = database.reference()
        
        userUidTaskHandle = Task {
            do {
                let emittedValues = asyncSequence(for: realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate.userUidFlow)
                for try await userUid in emittedValues {
                    
                    if userUid != "" {
                        database.reference().child(Section.users.path).child(userUid).observeSingleEvent (of: .value, with: { snapshot in
                            let nSDictionary: NSDictionary? = snapshot.value as? NSDictionary

                            let remoteUser: RemoteUser = RemoteUser(
                                uid: nSDictionary?["uid"] as? String ?? "",
                                username: nSDictionary?["username"] as? String ?? "",
                                description: nSDictionary?["description"] as? String ?? "",
                                image: nSDictionary?["image"] as? String ?? "",
                                countryForRescueEventNotifications: nSDictionary?["countryForRescueEventNotifications"] as? String ?? "",
                                cityForRescueEventNotifications: nSDictionary?["cityForRescueEventNotifications"] as? String ?? "",
                                fcmToken: nSDictionary?["fcmToken"] as? String ?? "",
                                subscriptions: nSDictionary?["subscriptions"] as? [RemoteSubscription] ?? []
                            )
                            realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate.updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: remoteUser)
                            
                        }) { error in
                            log.e(
                                tag: "RealtimeDatabaseRemoteUserRepositoryForIosDelegateImpl",
                                message: "Error retrieving the remote user id \(userUid): \(String(describing: error))",
                                throwable: nil
                            )
                            realtimeDatabaseRemoteUserFlowsRepositoryForIosDelegate.updateRealtimeDatabaseRemoteUserRepositoryForIosDelegate(delegate: nil)
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
            "countryForRescueEventNotifications": remoteUser.countryForRescueEventNotifications!,
            "cityForRescueEventNotifications": remoteUser.cityForRescueEventNotifications!,
            "fcmToken": remoteUser.fcmToken!,
            "subscriptions": remoteSubscriptionForUserTodictArray(from: remoteUser.subscriptions!)
        ]
    }
    
    private func remoteSubscriptionForUserTodictArray(from items: [RemoteSubscription]?) -> [[String: Any]] {
        (items ?? []).map {
            [
                "subscriptionId": $0.subscriptionId ?? "",
                "uid": $0.uid ?? "",
                "topic": $0.topic ?? ""
            ]
        }
    }
    
    func insertRemoteUser(remoteUser: RemoteUser, onInsertRemoteUser: @escaping (DatabaseResult) -> Void) async {
        do {
            try await databaseReference!.child(Section.users.path).child(remoteUser.uid!).setValue(getNSDictionaryFromRemoteUser(remoteUser: remoteUser))
            onInsertRemoteUser(DatabaseResult.Success())
        } catch {
            log.e(tag: "RealtimeDatabaseRemoteUserRepositoryForIosDelegateImpl", message: "Error inserting the remote user \(String(describing: remoteUser.uid))", throwable: nil)
            onInsertRemoteUser(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func updateRemoteUser(remoteUser: RemoteUser, onUpdateRemoteUser: @escaping (DatabaseResult) -> Void) async {
        let remoteUserValues = getNSDictionaryFromRemoteUser(remoteUser: remoteUser)
        let childUpdates = ["/\(Section.users.path)/\(remoteUser.uid!)": remoteUserValues]

        do {
            try await databaseReference?.updateChildValues(childUpdates)
            onUpdateRemoteUser(DatabaseResult.Success())
        } catch {
            log.e(tag: "RealtimeDatabaseRemoteUserRepositoryForIosDelegateImpl", message: "Error updating the remote user \(String(describing: remoteUser.uid))", throwable: nil)
            onUpdateRemoteUser(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func deleteRemoteUser(uid: String, onDeleteRemoteUser: @escaping (DatabaseResult) -> Void) {
        databaseReference!.child(Section.users.path).child(uid).removeValue { error, _ in
            if (error == nil) {
                onDeleteRemoteUser(DatabaseResult.Success())
            } else {
                self.log.e(tag: "RealtimeDatabaseRemoteUserRepositoryForIosDelegateImpl", message: "Error deleting the remote user \(uid): \(String(describing: error))", throwable: nil)
                onDeleteRemoteUser(DatabaseResult.Error(message: String(describing: error)))
            }
        }
    }
}
