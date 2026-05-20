import ComposeApp
import FirebaseCore
import FirebaseFirestore
import FirebaseFunctions
import KMPNativeCoroutinesAsync


class FireStoreRemoteChatRepositoryForIosDelegateImpl: FireStoreRemoteChatRepositoryForIosDelegate {

    private var firebaseFirestore: Firestore?

    private var chatUidTaskHandle: Task<(), Never>?

    private var chatListener: ListenerRegistration?

    private var messagesListener: ListenerRegistration?

    private var allMyChatsListener: ListenerRegistration?

    private var log: Log

    init(
        fireStoreRemoteChatFlowsRepositoryForIosDelegate: FireStoreRemoteChatFlowsRepositoryForIosDelegate,
        log: Log
    ) {
        self.log = log
        firebaseFirestore = Firestore.firestore()

        chatUidTaskHandle = Task {
            do {
                let emittedValues = asyncSequence(for: fireStoreRemoteChatFlowsRepositoryForIosDelegate.queryChatFlow)
                for try await queryChat in emittedValues {

                    let chatId: String = queryChat.id ?? ""
                    let uid: String = queryChat.uid ?? ""
                    let lastChatTimestamp: CLong = CLong(Int64(truncating: queryChat.lastChatTimestamp ?? -1))
                    let lastMessageTimestamp: CLong = CLong(Int64(truncating: queryChat.lastMessageTimestamp ?? -1))

                    let isGetRemoteChat = chatId != "" && lastMessageTimestamp == -1
                    let isGetRemoteChatMessages = chatId != "" && lastMessageTimestamp != -1
                    let isGetAllMyRemoteChats = uid != "" && lastChatTimestamp != -1

                    if isGetRemoteChat {

                        try await getRemoteChat(
                            chatId: chatId,
                            fireStoreRemoteChatFlowsRepositoryForIosDelegate: fireStoreRemoteChatFlowsRepositoryForIosDelegate
                        )
                    } else if isGetRemoteChatMessages {

                        try await getRemoteChatMessages(
                            chatId: chatId,
                            lastMessageTimestamp: lastMessageTimestamp,
                            fireStoreRemoteChatFlowsRepositoryForIosDelegate: fireStoreRemoteChatFlowsRepositoryForIosDelegate
                        )
                    } else if isGetAllMyRemoteChats {

                        try await getAllMyRemoteChats(
                            uid: uid,
                            lastChatTimestamp: lastChatTimestamp,
                            fireStoreRemoteChatFlowsRepositoryForIosDelegate: fireStoreRemoteChatFlowsRepositoryForIosDelegate
                        )
                    }
                }
            } catch {
                log.e(
                    tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                    message: "Failed with error: \(String(describing: error))",
                    throwable: nil
                )
            }
        }
    }

    deinit {
        chatUidTaskHandle?.cancel()
        chatListener?.remove()
        messagesListener?.remove()
        allMyChatsListener?.remove()
    }

    private func getRemoteChat(
        chatId: String,
        fireStoreRemoteChatFlowsRepositoryForIosDelegate: FireStoreRemoteChatFlowsRepositoryForIosDelegate
    ) async throws {

        chatListener?.remove()
        chatListener = firebaseFirestore!
            .collection(Section.chats.path)
            .document(chatId)
            .addSnapshotListener { documentSnapshot, error in

                if documentSnapshot != nil && documentSnapshot?.exists == true {

                    guard let document = documentSnapshot else {
                        self.log.e(
                            tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                            message: "getRemoteChat: Error fetching document: \(String(describing: error))",
                            throwable: nil
                        )
                        return
                    }
                    guard let remoteChatDTO = try? document.data(as: RemoteChatDTO.self) else {
                        self.log.d(
                            tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                            message: "getRemoteChat: Document data was empty"
                        )
                        return
                    }
                    self.log.d(
                        tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                        message: "getRemoteChat: Successfully retrieved the remote chat \(chatId)"
                    )
                    fireStoreRemoteChatFlowsRepositoryForIosDelegate.updateRemoteChatListFlow(delegate: [remoteChatDTO.toKotlin()])
                } else {
                    self.log.e(
                        tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                        message: "getRemoteChat: Document does not exist or error fetching document: \(String(describing: error))",
                        throwable: nil
                    )
                    fireStoreRemoteChatFlowsRepositoryForIosDelegate.updateRemoteChatListFlow(delegate: [])
                }
            }
    }

    private func getRemoteChatMessages(
        chatId: String,
        lastMessageTimestamp: CLong,
        fireStoreRemoteChatFlowsRepositoryForIosDelegate: FireStoreRemoteChatFlowsRepositoryForIosDelegate
    ) async throws {

        var remoteChatMessages: [RemoteChatMessage] = []

        messagesListener?.remove()
        messagesListener = firebaseFirestore!
            .collection(Section.chats.path)
            .document(chatId)
            .collection(Section.messages.path)
            .whereField("timestamp", isGreaterThanOrEqualTo: lastMessageTimestamp)
            .order(by: "timestamp")
            .addSnapshotListener { documentSnapshot, error in

                remoteChatMessages.removeAll()

                guard let documentChanges = documentSnapshot?.documentChanges else {
                    self.log.e(
                        tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                        message: "getRemoteChatMessages: Error fetching documents: \(String(describing: error))",
                        throwable: nil
                    )
                    return
                }
                for documentChange in documentChanges {

                    if let remoteChatMessageDTO = try? documentChange.document.data(as: RemoteChatMessageDTO.self) {
                        remoteChatMessages.append(remoteChatMessageDTO.toKotlin())
                    } else {
                        self.log.e(
                            tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                            message: "getRemoteChatMessages: Failed to decode RemoteChatMessageDTO",
                            throwable: nil
                        )
                    }
                }
                fireStoreRemoteChatFlowsRepositoryForIosDelegate.updateRemoteChatMessageListFlow(delegate: remoteChatMessages)
            }
    }

    private func getAllMyRemoteChats(
        uid: String,
        lastChatTimestamp: CLong,
        fireStoreRemoteChatFlowsRepositoryForIosDelegate: FireStoreRemoteChatFlowsRepositoryForIosDelegate
    ) async throws {

        var remoteChats: [RemoteChat] = []

        allMyChatsListener?.remove()
        allMyChatsListener = firebaseFirestore!
            .collection(Section.chats.path)
            .whereFilter(Filter.andFilter([
                                              Filter.orFilter([
                                                                  Filter.whereField("chatHolderId", isEqualTo: uid),
                                                                  Filter.whereField("allActivistsInfo", arrayContains: uid)
                                                              ]),
                                              Filter.whereField("timestamp", isGreaterOrEqualTo: lastChatTimestamp)
                                          ])
            )
            .order(by: "timestamp")
            .addSnapshotListener { documentSnapshot, error in

                remoteChats.removeAll()

                guard let documents = documentSnapshot?.documents else {
                    self.log.e(
                        tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                        message: "getAllMyRemoteChats: Error fetching documents: \(String(describing: error))",
                        throwable: nil
                    )
                    return
                }
                for document in documents {
                    if let remoteChatDTO = try? document.data(as: RemoteChatDTO.self) {
                        remoteChats.append(remoteChatDTO.toKotlin())
                    } else {
                        self.log.e(
                            tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                            message: "getAllMyRemoteChats: Failed to decode RemoteChatDTO",
                            throwable: nil
                        )
                    }
                }
                fireStoreRemoteChatFlowsRepositoryForIosDelegate.updateRemoteChatListFlow(delegate: remoteChats)
            }
    }

    private func remoteNonHumanAnimalInfoToDictArray(from items: [RemoteNonHumanAnimalInfo]?) -> [[String: Any]] {
        (items ?? []).map {
            [
                "nonHumanAnimalId": $0.nonHumanAnimalId ?? "",
                "chatId": $0.chatId ?? "",
                "caregiverId": $0.caregiverId!
            ]
        }
    }

    private func remoteBlockedUserInfoToDictArray(from items: [RemoteBlockedUserInfo]?) -> [[String: Any]] {
        (items ?? []).map {
            [
                "id": $0.id ?? "",
                "chatId": $0.chatId ?? "",
                "uid": $0.uid!
            ]
        }
    }

    private func getDictionaryFromRemoteChat(remoteChat: RemoteChat) -> Dictionary<String, Any> {
        return [
            "id": remoteChat.id!,
            "fosterHomeId": remoteChat.fosterHomeId!,
            "rescueEventId": remoteChat.rescueEventId!,
            "chatHolderId": remoteChat.chatHolderId!,
            "allNonHumanAnimalsInfo": remoteNonHumanAnimalInfoToDictArray(from: remoteChat.allNonHumanAnimalsInfo!),
            "allActivistsInfo": remoteChat.allActivistsInfo!,
            "allBlockedUsersInfo": remoteBlockedUserInfoToDictArray(from: remoteChat.allBlockedUsersInfo!),
            "acceptedFoster": remoteChat.acceptedFoster!,
            "finished": remoteChat.finished!,
            "addReview": remoteChat.addReview!,
            "timestamp": remoteChat.timestamp!
        ]
    }

    private func getDictionaryFromRemoteChatMessage(remoteChatMessage: RemoteChatMessage) -> Dictionary<String, Any> {
        return [
            "id": remoteChatMessage.id!,
            "chatId": remoteChatMessage.chatId!,
            "message": remoteChatMessage.message!,
            "senderId": remoteChatMessage.senderId!,
            "timestamp": remoteChatMessage.timestamp!
        ]
    }

    func insertRemoteChat(remoteChat: RemoteChat, onInsertRemoteChat: @escaping (DatabaseResult) -> Void) async {
        do {
            try await firebaseFirestore!
                .collection(Section.chats.path)
                .document(remoteChat.id!)
                .setData(getDictionaryFromRemoteChat(remoteChat: remoteChat))
            log.d(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "insertRemoteChat: Successfully inserted the remote chat \(String(describing: remoteChat.id))"
            )
            onInsertRemoteChat(DatabaseResult.Success())
        } catch {
            log.e(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "insertRemoteChat: Error inserting the remote chat \(String(describing: remoteChat.id))",
                throwable: nil
            )
            onInsertRemoteChat(DatabaseResult.Error(message: String(describing: error)))
        }
    }

    func insertRemoteChatMessage(remoteChatMessage: RemoteChatMessage, onInsertRemoteChatMessage: @escaping (DatabaseResult) -> Void) async {
        do {
            try await firebaseFirestore!
                .collection(Section.chats.path)
                .document(remoteChatMessage.chatId!)
                .collection(Section.messages.path)
                .document(remoteChatMessage.id!)
                .setData(getDictionaryFromRemoteChatMessage(remoteChatMessage: remoteChatMessage))
            log.d(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "insertRemoteChatMessage: Successfully inserted the remote chat message \(String(describing: remoteChatMessage.id))"
            )
            onInsertRemoteChatMessage(DatabaseResult.Success())
        } catch {
            log.e(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "insertRemoteChatMessage: Error inserting the remote chat message \(String(describing: remoteChatMessage.id))",
                throwable: nil
            )
            onInsertRemoteChatMessage(DatabaseResult.Error(message: String(describing: error)))
        }
    }

    func modifyRemoteChat(remoteChat: RemoteChat, onModifyRemoteChat: @escaping (DatabaseResult) -> Void) async throws {

        let remoteChatValues = getDictionaryFromRemoteChat(remoteChat: remoteChat)

        do {
            try await firebaseFirestore!
                .collection(Section.chats.path)
                .document(remoteChat.id!)
                .updateData(remoteChatValues)

            log.d(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "modifyRemoteChat: Successfully modified the remote chat \(String(describing: remoteChat.id))"
            )
            onModifyRemoteChat(DatabaseResult.Success())
        } catch {
            log.e(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "modifyRemoteChat: Error updating the remote chat \(String(describing: remoteChat.id))",
                throwable: nil
            )
            onModifyRemoteChat(DatabaseResult.Error(message: String(describing: error)))
        }
    }

    func modifyOnlyActivistsInRemoteChat(chatId: String, activistId: String, shouldAdd: Bool, onModifyOnlyActivistsInRemoteChat: @escaping (DatabaseResult) -> Void) async {

        let chatRef = firebaseFirestore!
            .collection(Section.chats.path)
            .document(chatId)

        do {
            if (shouldAdd) {
                try await chatRef
                    .updateData([
                                    "allActivistsInfo": FieldValue.arrayUnion([activistId])
                                ])
            } else {
                try await chatRef
                    .updateData([
                                    "allActivistsInfo": FieldValue.arrayRemove([activistId])
                                ])
            }

            let action = if (shouldAdd) {
                "added"
            } else {
                "removed"
            }
            log.d(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "modifyOnlyActivistsInRemoteChat: Successfully \(String(describing: action)) the activist \(String(describing: activistId)) in the remote chat \(String(describing: chatId))"
            )
            onModifyOnlyActivistsInRemoteChat(DatabaseResult.Success())
        } catch {

            let action = if (shouldAdd) {
                "adding"
            } else {
                "removing"
            }
            log.e(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "modifyOnlyActivistsInRemoteChat: Error \(String(describing: action)) the activist \(String(describing: activistId)) in the remote chat \(String(describing: chatId)): \(String(describing: error))",
                throwable: nil
            )
            onModifyOnlyActivistsInRemoteChat(DatabaseResult.Error(message: String(describing: error)))
        }
    }

    func deleteRemoteChat(uid: String, remoteChatId: String, onDeleteRemoteChat: @escaping (DatabaseResult) -> Void) async throws {

        let path = firebaseFirestore!
            .collection(Section.chats.path)
            .document(remoteChatId)
            .path

        Functions.functions()
            .httpsCallable("deleteRemoteChat")
            .call(["uid": uid, "path": path]) { result, error in

                if error == nil {
                    self.log.d(
                        tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                        message: "deleteRemoteChat: Successfully deleted the remote chat \(remoteChatId)"
                    )
                    onDeleteRemoteChat(DatabaseResult.Success())
                } else {
                    self.log.e(
                        tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                        message: "deleteRemoteChat: Error deleting the remote chat \(remoteChatId): \(String(describing: error))",
                        throwable: nil
                    )
                    onDeleteRemoteChat(DatabaseResult.Error(message: String(describing: error)))
                }
            }

    }

    func deleteAllMyRemoteChats(uid: String, onDeleteAllMyRemoteChats: @escaping (DatabaseResult) -> Void) async throws {
        do {
            let querySnapshotForAllMyChats = try await firebaseFirestore!
                .collection(Section.chats.path)
                .whereField("chatHolderId", isEqualTo: uid)
                .getDocuments()

            var paths: [String] = []

            for chatQueryDocumentSnapshot in querySnapshotForAllMyChats.documents {
                paths.append(chatQueryDocumentSnapshot.reference.path)
            }

            Functions.functions()
                .httpsCallable("deleteAllRemoteChatsFromUser")
                .call(["uid": uid, "paths": paths]) { result, error in

                    if error == nil {
                        self.log.d(
                            tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                            message: "deleteAllMyRemoteChats: Successfully deleted the remote chats from the user \(uid)"
                        )
                        onDeleteAllMyRemoteChats(DatabaseResult.Success())
                    } else {
                        self.log.e(
                            tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                            message: "deleteAllMyRemoteChats: Error deleting the remote chats from the user \(uid): \(String(describing: error))",
                            throwable: nil
                        )
                        onDeleteAllMyRemoteChats(DatabaseResult.Error(message: String(describing: error)))
                    }
                }
        } catch {
            log.e(
                tag: "FireStoreRemoteChatRepositoryForIosDelegateImpl",
                message: "deleteAllMyRemoteChats: Error deleting the remote chats from the user \(uid): \(String(describing: error))",
                throwable: nil
            )
            onDeleteAllMyRemoteChats(DatabaseResult.Error(message: String(describing: error)))
        }
    }
}
