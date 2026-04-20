package com.findmeahometeam.reskiume.data.remote.fireStore.chat

import com.findmeahometeam.reskiume.data.remote.response.DatabaseResult
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChatMessage
import com.findmeahometeam.reskiume.data.util.Section
import com.findmeahometeam.reskiume.data.util.log.Log
import com.findmeahometeam.reskiume.domain.repository.remote.fireStore.chat.FireStoreRemoteChatRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FireStoreRemoteChatRepositoryAndroidImpl(
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseFunctions: FirebaseFunctions,
    private val log: Log
) : FireStoreRemoteChatRepository {

    override fun insertRemoteChat(
        remoteChat: RemoteChat,
        remoteChatMessages: List<RemoteChatMessage>
    ): Flow<DatabaseResult> = flow<DatabaseResult> {

        val batch = firebaseFirestore.batch()

        val chatRef = firebaseFirestore
            .collection(Section.CHATS.path)
            .document(remoteChat.id!!)

        batch.set(chatRef, remoteChat.toMap())

        remoteChatMessages.forEach { remoteChatMessage ->

            val messageRef = chatRef
                .collection(Section.MESSAGES.path)
                .document(remoteChatMessage.id!!)

            batch.set(messageRef, remoteChatMessage.toMap())
        }
        batch.commit().await()
        log.d(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "insertRemoteChat: Successfully inserted the remote chat ${remoteChat.id}"
        )
        emit(DatabaseResult.Success)

    }.catch { e ->
        log.e(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "insertRemoteChat: Error inserting the remote chat ${remoteChat.id}: ${e.message}"
        )
        emit(DatabaseResult.Error(e.message ?: ""))
    }

    override fun insertRemoteChatMessage(remoteChatMessage: RemoteChatMessage): Flow<DatabaseResult> = flow<DatabaseResult> {

        firebaseFirestore
            .collection(Section.CHATS.path)
            .document(remoteChatMessage.chatId!!)
            .collection(Section.MESSAGES.path)
            .document(remoteChatMessage.id!!)
            .set(remoteChatMessage.toMap())
            .await()

        log.d(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "insertRemoteChatMessage: Successfully inserted the remote chat message ${remoteChatMessage.id}"
        )
        emit(DatabaseResult.Success)

    }.catch { e ->
        log.e(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "insertRemoteChatMessage: Error inserting the remote chat message ${remoteChatMessage.id}: ${e.message}"
        )
        emit(DatabaseResult.Error(e.message ?: ""))
    }

    override fun modifyRemoteChat(remoteChat: RemoteChat): Flow<DatabaseResult> =
        flow<DatabaseResult> {

            val remoteChatValues: Map<String, Any?> = remoteChat.toMap()
            firebaseFirestore
                .collection(Section.CHATS.path)
                .document(remoteChat.id!!)
                .update(remoteChatValues)
                .await()

            log.d(
                "FireStoreRemoteChatRepositoryAndroidImpl",
                "modifyRemoteChat: Successfully modified the remote chat ${remoteChat.id}"
            )
            emit(DatabaseResult.Success)

        }.catch { e ->
            log.e(
                "FireStoreRemoteChatRepositoryAndroidImpl",
                "modifyRemoteChat: Error modifying the remote chat ${remoteChat.id}: ${e.message}"
            )
            emit(DatabaseResult.Error(e.message ?: ""))
        }

    override fun deleteRemoteChat(
        uid: String,
        remoteChatId: String
    ): Flow<DatabaseResult> = flow<DatabaseResult> {

        val httpCallableReference = firebaseFunctions.getHttpsCallable("deleteRemoteChat")
        val path = firebaseFirestore
            .collection(Section.CHATS.path)
            .document(remoteChatId).path

        httpCallableReference.call(
            hashMapOf(
                "uid" to uid,
                "path" to path
            )
        ).await()
        log.d(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "deleteRemoteChat: Successfully deleted the remote chat $remoteChatId"
        )
        emit(DatabaseResult.Success)
    }.catch { e ->
        log.e(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "deleteRemoteChat: Error deleting the remote chat $remoteChatId: ${e.message}"
        )
        emit(DatabaseResult.Error(e.message ?: ""))
    }

    override fun deleteAllMyRemoteChats(uid: String): Flow<DatabaseResult> = flow<DatabaseResult> {

        val httpCallableReference =
            firebaseFunctions.getHttpsCallable("deleteAllRemoteChatsFromUser")

        val querySnapshot = firebaseFirestore
            .collection(Section.CHATS.path)
            .whereEqualTo("chatHolderId", uid)
            .get()
            .await()

        val paths: List<String> =
            querySnapshot.documents.mapNotNull { documentSnapshot: DocumentSnapshot ->
                documentSnapshot.reference.path
            }

        httpCallableReference.call(
            hashMapOf(
                "uid" to uid,
                "paths" to paths
            )
        ).await()
        log.d(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "deleteRemoteChat: Successfully deleted the remote chats from the user $uid"
        )
        emit(DatabaseResult.Success)
    }.catch { e ->
        log.e(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "deleteRemoteChat: Error deleting the remote chats from the user $uid: ${e.message}"
        )
        emit(DatabaseResult.Error(e.message ?: ""))
    }

    override fun getRemoteChat(id: String): Flow<RemoteChat?> = callbackFlow {

        val listener = firebaseFirestore
            .collection(Section.CHATS.path)
            .document(id)
            .addSnapshotListener { value, error ->

                if (error == null) {
                    val result: RemoteChat? = value?.toObject(RemoteChat::class.java)
                    log.d(
                        "FireStoreRemoteChatRepositoryAndroidImpl",
                        "getRemoteChat: Successfully retrieved the remote chat $id"
                    )
                    trySend(result)
                } else {
                    log.e(
                        "FireStoreRemoteChatRepositoryAndroidImpl",
                        "getRemoteChat: Error retrieving the remote chat $id: ${error.message}"
                    )
                    trySend(null)
                    close(error)
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    override fun getRemoteChatMessages(
        chatId: String,
        lastTimestamp: Long
    ): Flow<List<RemoteChatMessage>> = callbackFlow {

        val listener = firebaseFirestore
            .collection(Section.CHATS.path)
            .document(chatId)
            .collection(Section.MESSAGES.path)
            .whereGreaterThan("timestamp", lastTimestamp)
            .orderBy("timestamp")
            .addSnapshotListener { querySnapshot, error ->

                if (error == null) {

                    val list: List<RemoteChatMessage> =
                        querySnapshot?.documentChanges?.mapNotNull {
                            it.document.toObject(RemoteChatMessage::class.java)
                        } ?: emptyList()

                    log.d(
                        "FireStoreRemoteChatRepositoryAndroidImpl",
                        "getRemoteChatMessages: Successfully retrieved remote chat messages for the chat $chatId"
                    )
                    trySend(list)
                } else {
                    log.e(
                        "FireStoreRemoteChatRepositoryAndroidImpl",
                        "getRemoteChatMessages: Error retrieving the remote chat messages for the chat $chatId: ${error.message}"
                    )
                    trySend(emptyList())
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    override fun getAllMyRemoteChats(uid: String): Flow<List<RemoteChat>> = flow {

        val querySnapshot = firebaseFirestore
            .collection(Section.CHATS.path)
            .where(
                Filter.or(
                    Filter.equalTo("chatHolderId", uid),
                    Filter.arrayContains("allActivistsInfo", uid)
                )
            )
            .get()
            .await()

        val result: List<RemoteChat> =
            querySnapshot.documents.mapNotNull { documentSnapshot: DocumentSnapshot ->
                documentSnapshot.toObject(RemoteChat::class.java)
            }
        log.d(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "getAllMyRemoteChats: Successfully retrieved all remote chats where the user $uid participates"
        )
        emit(result)

    }.catch { e ->
        log.e(
            "FireStoreRemoteChatRepositoryAndroidImpl",
            "getAllMyRemoteChats: Error retrieving all remote chats where the user $uid participates: ${e.message}"
        )
        emit(emptyList())
    }
}
