import ComposeApp
import FirebaseCore
import FirebaseFirestore
import KMPNativeCoroutinesAsync


class FireStoreRemoteRescueEventRepositoryForIosDelegateImpl: FireStoreRemoteRescueEventRepositoryForIosDelegate {

    private var firebaseFirestore: Firestore?

    private var rescueEventUidTaskHandle: Task<(), Never>?

    private var log: Log

    init(
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate,
        log: Log
    ) {
        self.log = log
        firebaseFirestore = Firestore.firestore()

        rescueEventUidTaskHandle = Task {
            do {
                let emittedValues = asyncSequence(for: fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.queryRescueEventFlow)
                for try await queryRescueEvent in emittedValues {

                    let rescueEventId: String = queryRescueEvent.id ?? ""
                    let creatorId: String = queryRescueEvent.creatorId ?? ""
                    let activistLongitude: Double = Double(truncating: queryRescueEvent.activistLongitude ?? 0.0)
                    let activistLatitude: Double = Double(truncating: queryRescueEvent.activistLatitude ?? 0.0)
                    let rangeLongitude: Double = Double(truncating: queryRescueEvent.rangeLongitude ?? 0.0)
                    let rangeLatitude: Double = Double(truncating: queryRescueEvent.rangeLatitude ?? 0.0)
                    let country: String = queryRescueEvent.country ?? ""
                    let city: String = queryRescueEvent.city ?? ""

                    let isFetchAllMyRescueEvents = creatorId != "" && rescueEventId == ""
                    let isFetchRescueEvent = rescueEventId != ""
                    let isFetchAllRescueEventsByCountryAndCity = country != "" && city != ""
                    let isFetchAllRescueEventsByLocation = activistLongitude != 0.0 && activistLatitude != 0.0 && rangeLongitude != 0.0 && rangeLatitude != 0.0

                    if isFetchAllMyRescueEvents {

                        try await fetchAllMyRescueEvents(
                            creatorId: creatorId,
                            fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
                        )
                    } else if isFetchRescueEvent {

                        try await fetchRescueEvent(
                            rescueEventId: rescueEventId,
                            fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
                        )
                    } else if isFetchAllRescueEventsByCountryAndCity {

                        try await fetchAllRescueEventsByCountryAndCity(
                            country: country,
                            city: city,
                            fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
                        )
                    } else if isFetchAllRescueEventsByLocation {

                        try await fetchAllRescueEventsByLocation(
                            activistLongitude: activistLongitude,
                            activistLatitude: activistLatitude,
                            rangeLongitude: rangeLongitude,
                            rangeLatitude: rangeLatitude,
                            fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
                        )
                    }
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }

    deinit {
        rescueEventUidTaskHandle?.cancel()
    }

    private func fetchAllMyRescueEvents(
        creatorId: String,
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
    ) async throws {

        let querySnapshotForAllMyRescueEvents = try await firebaseFirestore!
            .collection(Section.rescueEvents.path)
            .whereField("creatorId", isEqualTo: creatorId)
            .getDocuments()

        var remoteRescueEvents: [RemoteRescueEvent] = []

        for rescueEventQueryDocumentSnapshot in querySnapshotForAllMyRescueEvents.documents {
            do {
                let rescueEventDTO = try await rescueEventQueryDocumentSnapshot.reference.getDocument(as: RemoteRescueEventDTO.self)
                remoteRescueEvents.append(rescueEventDTO.toKotlin())
            } catch {
                log.e(
                    tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                    message: "fetchAllMyRescueEvents: Error retrieving the remote rescue events for the creator id \(creatorId): \(String(describing: error))",
                    throwable: nil
                )
                fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.updateRemoteRescueEventListFlow(delegate: [])
            }
        }
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.updateRemoteRescueEventListFlow(delegate: remoteRescueEvents)
    }

    private func fetchRescueEvent(
        rescueEventId: String,
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
    ) async throws {

        var remoteRescueEvents: [RemoteRescueEvent] = []

        do {
            let rescueEventDTO = try await firebaseFirestore!
                .collection(Section.rescueEvents.path)
                .document(rescueEventId)
                .getDocument(as: RemoteRescueEventDTO.self)
            remoteRescueEvents.append(rescueEventDTO.toKotlin())
        } catch {
            log.e(
                tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                message: "fetchRescueEvent: Error retrieving the remote rescue event \(rescueEventId): \(String(describing: error))",
                throwable: nil
            )
            fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.updateRemoteRescueEventListFlow(delegate: [])
        }
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.updateRemoteRescueEventListFlow(delegate: remoteRescueEvents)
    }

    private func fetchAllRescueEventsByCountryAndCity(
        country: String,
        city: String,
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
    ) async throws {

        let querySnapshotForAllMyRescueEvents = try await firebaseFirestore!
            .collection(Section.rescueEvents.path)
            .whereField("country", isEqualTo: country)
            .whereField("city", isEqualTo: city)
            .getDocuments()

        var remoteRescueEvents: [RemoteRescueEvent] = []

        for rescueEventQueryDocumentSnapshot in querySnapshotForAllMyRescueEvents.documents {
            do {
                let rescueEventDTO = try await rescueEventQueryDocumentSnapshot.reference.getDocument(as: RemoteRescueEventDTO.self)
                remoteRescueEvents.append(rescueEventDTO.toKotlin())
            } catch {
                log.e(
                    tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                    message: "fetchAllRescueEventsByCountryAndCity: Error retrieving the remote rescue events by country and city (\(country), \(city)): \(String(describing: error))",
                    throwable: nil
                )
                fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.updateRemoteRescueEventListFlow(delegate: [])
            }
        }
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.updateRemoteRescueEventListFlow(delegate: remoteRescueEvents)
    }

    private func fetchAllRescueEventsByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double,
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate: FireStoreRemoteRescueEventFlowsRepositoryForIosDelegate
    ) async throws {

        let querySnapshotForAllMyRescueEvents = try await firebaseFirestore!
            .collection(Section.rescueEvents.path)
            .whereField("longitude", isGreaterThanOrEqualTo: activistLongitude - rangeLongitude)
            .whereField("longitude", isLessThanOrEqualTo: activistLongitude + rangeLongitude)
            .whereField("latitude", isGreaterThanOrEqualTo: activistLatitude - rangeLatitude)
            .whereField("latitude", isLessThanOrEqualTo: activistLatitude + rangeLatitude)
            .getDocuments()

        var remoteRescueEvents: [RemoteRescueEvent] = []

        for rescueEventQueryDocumentSnapshot in querySnapshotForAllMyRescueEvents.documents {
            do {
                let rescueEventDTO = try await rescueEventQueryDocumentSnapshot.reference.getDocument(as: RemoteRescueEventDTO.self)
                remoteRescueEvents.append(rescueEventDTO.toKotlin())
            } catch {
                log.e(
                    tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                    message: "fetchAllRescueEventsByLocation: Error retrieving the remote rescue events by location: \(String(describing: error))",
                    throwable: nil
                )
                fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.updateRemoteRescueEventListFlow(delegate: [])
            }
        }
        fireStoreRemoteRescueEventFlowsRepositoryForIosDelegate.updateRemoteRescueEventListFlow(delegate: remoteRescueEvents)
    }

    private func remoteNeedToCoverForRescueEventToDictArray(from items: [RemoteNeedToCoverForRescueEvent]?) -> [[String: Any]] {
        (items ?? []).map {
            [
                "needToCoverId": $0.needToCoverId ?? "",
                "rescueNeed": $0.rescueNeed?.name ?? RescueNeed.unselected,
                "rescueEventId": $0.rescueEventId!
            ]
        }
    }

    private func remoteNonHumanAnimalToRescueForRescueEventToDictArray(from items: [RemoteNonHumanAnimalToRescueForRescueEvent]?) -> [[String: Any]] {
        (items ?? []).map {
            [
                "nonHumanAnimalId": $0.nonHumanAnimalId ?? "",
                "caregiverId": $0.caregiverId ?? "",
                "rescueEventId": $0.rescueEventId!
            ]
        }
    }

    private func getDictionaryFromRemoteRescueEvent(remoteRescueEvent: RemoteRescueEvent) -> Dictionary<String, Any> {
        return [
            "id": remoteRescueEvent.id!,
            "creatorId": remoteRescueEvent.creatorId!,
            "title": remoteRescueEvent.title!,
            "description": remoteRescueEvent.description_!,
            "imageUrl": remoteRescueEvent.imageUrl!,
            "allNeedsToCover": remoteNeedToCoverForRescueEventToDictArray(from: remoteRescueEvent.allNeedsToCover!),
            "allNonHumanAnimalsToRescue": remoteNonHumanAnimalToRescueForRescueEventToDictArray(from: remoteRescueEvent.allNonHumanAnimalsToRescue!),
            "longitude": remoteRescueEvent.longitude!,
            "latitude": remoteRescueEvent.latitude!,
            "country": remoteRescueEvent.country!,
            "city": remoteRescueEvent.city!
        ]
    }

    func insertRemoteRescueEvent(remoteRescueEvent: RemoteRescueEvent, onInsertRemoteRescueEvent: @escaping (DatabaseResult) -> Void) async {
        do {
            try await firebaseFirestore!
                .collection(Section.rescueEvents.path)
                .document(remoteRescueEvent.id!)
                .setData(getDictionaryFromRemoteRescueEvent(remoteRescueEvent: remoteRescueEvent))
            onInsertRemoteRescueEvent(DatabaseResult.Success())
        } catch {
            log.e(
                tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                message: "insertRemoteRescueEvent: Error inserting the remote rescue event \(String(describing: remoteRescueEvent.creatorId))",
                throwable: nil
            )
            onInsertRemoteRescueEvent(DatabaseResult.Error(message: String(describing: error)))
        }
    }

    func modifyRemoteRescueEvent(remoteRescueEvent: RemoteRescueEvent, onModifyRemoteRescueEvent: @escaping (DatabaseResult) -> Void) async throws {

        let remoteRescueEventValues = getDictionaryFromRemoteRescueEvent(remoteRescueEvent: remoteRescueEvent)

        do {
            try await firebaseFirestore!
                .collection(Section.rescueEvents.path)
                .document(remoteRescueEvent.id!)
                .updateData(remoteRescueEventValues)

            onModifyRemoteRescueEvent(DatabaseResult.Success())
        } catch {
            log.e(
                tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                message: "modifyRemoteRescueEvent: Error updating the remote rescue event \(String(describing: remoteRescueEvent.id))",
                throwable: nil
            )
            onModifyRemoteRescueEvent(DatabaseResult.Error(message: String(describing: error)))
        }
    }

    func deleteRemoteRescueEvent(id: String, onDeleteRemoteRescueEvent: @escaping (DatabaseResult) -> Void) async throws {
        do {
            try await firebaseFirestore!
                .collection(Section.rescueEvents.path)
                .document(id)
                .delete()

            onDeleteRemoteRescueEvent(DatabaseResult.Success())
        } catch {
            log.e(
                tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                message: "deleteRemoteRescueEvent: Error deleting the remote rescue event \(id): \(String(describing: error))",
                throwable: nil
            )
            onDeleteRemoteRescueEvent(DatabaseResult.Error(message: String(describing: error)))
        }
    }

    func deleteAllMyRemoteRescueEvents(creatorId: String, onDeleteAllMyRemoteRescueEvents: @escaping (DatabaseResult) -> Void) async throws {
        do {
            let querySnapshotForAllMyRescueEvents = try await firebaseFirestore!
                .collection(Section.rescueEvents.path)
                .whereField("creatorId", isEqualTo: creatorId)
                .getDocuments()

            var isError: Bool = false

            for rescueEventQueryDocumentSnapshot in querySnapshotForAllMyRescueEvents.documents {
                do {
                    try await rescueEventQueryDocumentSnapshot.reference.delete()
                } catch {
                    log.e(
                        tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                        message: "deleteAllMyRemoteRescueEvents: Error deleting all remote rescue events from the creator id \(creatorId): \(String(describing: error))",
                        throwable: nil
                    )
                    onDeleteAllMyRemoteRescueEvents(DatabaseResult.Error(message: String(describing: error)))
                    isError = true
                }
            }
            if isError == false {
                onDeleteAllMyRemoteRescueEvents(DatabaseResult.Success())
            }
        } catch {
            log.e(
                tag: "FireStoreRemoteRescueEventRepositoryForIosDelegateImpl",
                message: "deleteAllMyRemoteRescueEvents: Error deleting all remote rescue events from the creator id \(creatorId): \(String(describing: error))",
                throwable: nil
            )
            onDeleteAllMyRemoteRescueEvents(DatabaseResult.Error(message: String(describing: error)))
        }
    }
}
