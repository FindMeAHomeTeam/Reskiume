import ComposeApp
import FirebaseCore
import FirebaseFirestore
import KMPNativeCoroutinesAsync


class FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl: FireStoreRemoteFosterHomeRepositoryForIosDelegate {

    private var firebaseFirestore: Firestore?
    
    private var fosterHomeUidTaskHandle: Task<(), Never>?
    
    private var log: Log
            
    init (
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate,
        log: Log
    ) {
        self.log = log
        firebaseFirestore = Firestore.firestore()
        
        fosterHomeUidTaskHandle = Task {
            do {
                let emittedValues = asyncSequence(for: fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.queryFosterHomeFlow)
                for try await queryFosterHome in emittedValues {
                    
                    let fosterHomeId: String = queryFosterHome.id ?? ""
                    let ownerId: String = queryFosterHome.ownerId ?? ""
                    let activistLongitude: Double = Double(truncating: queryFosterHome.activistLongitude ?? 0.0)
                    let activistLatitude: Double = Double(truncating: queryFosterHome.activistLatitude ?? 0.0)
                    let rangeLongitude: Double = Double(truncating: queryFosterHome.rangeLongitude ?? 0.0)
                    let rangeLatitude: Double = Double(truncating: queryFosterHome.rangeLatitude ?? 0.0)
                    let country: String = queryFosterHome.country ?? ""
                    let city: String = queryFosterHome.city ?? ""
                    
                    let isFetchAllMyFosterHomes = ownerId != "" && fosterHomeId == ""
                    let isFetchFosterHome = ownerId != "" && fosterHomeId != ""
                    let isFetchAllFosterHomesByCountryAndCity = country != "" && city != ""
                    let isFetchAllFosterHomesByLocation = activistLongitude != 0.0 && activistLatitude != 0.0 && rangeLongitude != 0.0 && rangeLatitude != 0.0
                                            
                    if isFetchAllMyFosterHomes {
                        
                        try await fetchAllMyFosterHomes(
                            ownerId: ownerId,
                            fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
                        )
                    } else if isFetchFosterHome {
                        
                        try await fetchFosterHome(
                            fosterHomeId: fosterHomeId,
                            ownerId: ownerId,
                            fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
                        )
                    } else if isFetchAllFosterHomesByCountryAndCity {
                        
                        try await fetchAllFosterHomesByCountryAndCity(
                            country: country,
                            city: city,
                            fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
                        )
                    } else if isFetchAllFosterHomesByLocation {
                        
                        try await fetchAllFosterHomesByLocation(
                            activistLongitude: activistLongitude,
                            activistLatitude: activistLatitude,
                            rangeLongitude: rangeLongitude,
                            rangeLatitude: rangeLatitude,
                            fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
                        )
                    }
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    deinit {
        fosterHomeUidTaskHandle?.cancel()
    }
    
    private func fetchAllMyFosterHomes(
        ownerId: String,
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
    ) async throws {
        
        let querySnapshotForAllMyFosterHomes = try await firebaseFirestore!
            .collection(Section.fosterHomes.path)
            .whereField("ownerId", isEqualTo: ownerId)
            .getDocuments()
        
        var remoteFosterHomes: [RemoteFosterHome] = []
        
        for fosterHomeQueryDocumentSnapshot in querySnapshotForAllMyFosterHomes.documents {
            do {
                let fosterHomeDTO = try await fosterHomeQueryDocumentSnapshot.reference.getDocument(as: RemoteFosterHomeDTO.self)
                remoteFosterHomes.append(fosterHomeDTO.toKotlin())
            } catch {
                log.e(
                    tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl",
                    message: "Error retrieving the remote foster homes for the owner id \(ownerId): \(String(describing: error))",
                    throwable: nil
                )
                fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.updateRemoteFosterHomeListFlow(delegate: [])
            }
        }
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.updateRemoteFosterHomeListFlow(delegate: remoteFosterHomes)
    }
    
    private func fetchFosterHome(
        fosterHomeId: String,
        ownerId: String,
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
    ) async throws {
        
        var remoteFosterHomes: [RemoteFosterHome] = []
        
        do {
            let fosterHomeDTO = try await firebaseFirestore!
                .collection(Section.fosterHomes.path)
                .document(fosterHomeId)
                .getDocument(as: RemoteFosterHomeDTO.self)
            remoteFosterHomes.append(fosterHomeDTO.toKotlin())
        } catch {
            log.e(
                tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl",
                message: "Error retrieving the remote foster home \(fosterHomeId) for the owner id \(ownerId): \(String(describing: error))",
                throwable: nil
            )
            fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.updateRemoteFosterHomeListFlow(delegate: [])
        }
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.updateRemoteFosterHomeListFlow(delegate: remoteFosterHomes)
    }
    
    private func fetchAllFosterHomesByCountryAndCity(
        country: String,
        city: String,
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
    ) async throws {
        
        let querySnapshotForAllMyFosterHomes = try await firebaseFirestore!
            .collection(Section.fosterHomes.path)
            .whereField("country", isEqualTo: country)
            .whereField("city", isEqualTo: city)
            .whereField("available", isEqualTo: true)
            .getDocuments()
        
        var remoteFosterHomes: [RemoteFosterHome] = []
        
        for fosterHomeQueryDocumentSnapshot in querySnapshotForAllMyFosterHomes.documents {
            do {
                let fosterHomeDTO = try await fosterHomeQueryDocumentSnapshot.reference.getDocument(as: RemoteFosterHomeDTO.self)
                remoteFosterHomes.append(fosterHomeDTO.toKotlin())
            } catch {
                log.e(
                    tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl",
                    message: "Error retrieving the remote foster homes by country and city (\(country), \(city)): \(String(describing: error))",
                    throwable: nil
                )
                fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.updateRemoteFosterHomeListFlow(delegate: [])
            }
        }
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.updateRemoteFosterHomeListFlow(delegate: remoteFosterHomes)
    }
    
    private func fetchAllFosterHomesByLocation(
        activistLongitude: Double,
        activistLatitude: Double,
        rangeLongitude: Double,
        rangeLatitude: Double,
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate: FireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate
    ) async throws {
        
        let querySnapshotForAllMyFosterHomes = try await firebaseFirestore!
            .collection(Section.fosterHomes.path)
            .whereField("longitude", isGreaterThanOrEqualTo: activistLongitude - rangeLongitude)
            .whereField("longitude", isLessThanOrEqualTo: activistLongitude + rangeLongitude)
            .whereField("latitude", isGreaterThanOrEqualTo: activistLatitude - rangeLatitude)
            .whereField("latitude", isLessThanOrEqualTo: activistLatitude + rangeLatitude)
            .whereField("available", isEqualTo: true)
            .getDocuments()
        
        var remoteFosterHomes: [RemoteFosterHome] = []
        
        for fosterHomeQueryDocumentSnapshot in querySnapshotForAllMyFosterHomes.documents {
            do {
                let fosterHomeDTO = try await fosterHomeQueryDocumentSnapshot.reference.getDocument(as: RemoteFosterHomeDTO.self)
                remoteFosterHomes.append(fosterHomeDTO.toKotlin())
            } catch {
                log.e(
                    tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl",
                    message: "Error retrieving the remote foster homes by location: \(String(describing: error))",
                    throwable: nil
                )
                fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.updateRemoteFosterHomeListFlow(delegate: [])
            }
        }
        fireStoreRemoteFosterHomeFlowsRepositoryForIosDelegate.updateRemoteFosterHomeListFlow(delegate: remoteFosterHomes)
    }
    
    private func remoteAcceptedNonHumanAnimalTypeForFosterHomeTodictArray(from items: [RemoteAcceptedNonHumanAnimalTypeForFosterHome]?) -> [[String: Any]] {
        (items ?? []).map {
            [
                "acceptedNonHumanAnimalTypeId": $0.acceptedNonHumanAnimalTypeId ?? 0,
                "fosterHomeId": $0.fosterHomeId!,
                "acceptedNonHumanAnimalType": $0.acceptedNonHumanAnimalType?.name ?? NonHumanAnimalType.unselected
            ]
        }
    }

    private func remoteAcceptedNonHumanAnimalGenderForFosterHomeTodictArray(from items: [RemoteAcceptedNonHumanAnimalGenderForFosterHome]?) -> [[String: Any]] {
        (items ?? []).map {
            [
                "acceptedNonHumanAnimalGenderId": $0.acceptedNonHumanAnimalGenderId ?? 0,
                "fosterHomeId": $0.fosterHomeId!,
                "acceptedNonHumanAnimalGender": $0.acceptedNonHumanAnimalGender?.name ?? Gender.unselected
            ]
        }
    }
    
    private func remoteResidentNonHumanAnimalIdForFosterHomeTodictArray(from items: [RemoteResidentNonHumanAnimalIdForFosterHome]?) -> [[String: Any]] {
        (items ?? []).map {
            [
                "residentNonHumanAnimalId": $0.residentNonHumanAnimalId ?? "",
                "caregiverId": $0.caregiverId ?? "",
                "fosterHomeId": $0.fosterHomeId!
            ]
        }
    }
    
    private func getDictionaryFromRemoteFosterHome(remoteFosterHome: RemoteFosterHome) -> Dictionary<String, Any> {
        return [
            "id": remoteFosterHome.id!,
            "ownerId": remoteFosterHome.ownerId!,
            "title": remoteFosterHome.title!,
            "description": remoteFosterHome.description_!,
            "conditions": remoteFosterHome.conditions!,
            "imageUrl": remoteFosterHome.imageUrl!,
            "allAcceptedNonHumanAnimalTypes": remoteAcceptedNonHumanAnimalTypeForFosterHomeTodictArray(from: remoteFosterHome.allAcceptedNonHumanAnimalTypes!),
            "allAcceptedNonHumanAnimalGenders": remoteAcceptedNonHumanAnimalGenderForFosterHomeTodictArray(from: remoteFosterHome.allAcceptedNonHumanAnimalGenders!),
            "allResidentNonHumanAnimalIds": remoteResidentNonHumanAnimalIdForFosterHomeTodictArray(from: remoteFosterHome.allResidentNonHumanAnimalIds!),
            "longitude": remoteFosterHome.longitude!,
            "latitude": remoteFosterHome.latitude!,
            "country": remoteFosterHome.country!,
            "city": remoteFosterHome.city!,
            "available": remoteFosterHome.available!
        ]
    }
    
    func insertRemoteFosterHome(remoteFosterHome: RemoteFosterHome, onInsertRemoteFosterHome: @escaping (DatabaseResult) -> Void) async {
        do {
            try await firebaseFirestore!
                .collection(Section.fosterHomes.path)
                .document(remoteFosterHome.id!)
                .setData(getDictionaryFromRemoteFosterHome(remoteFosterHome: remoteFosterHome))
            onInsertRemoteFosterHome(DatabaseResult.Success())
        } catch {
            log.e(
                tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl",
                message: "Error inserting the remote foster home \(String(describing: remoteFosterHome.ownerId))",
                throwable: nil
            )
            onInsertRemoteFosterHome(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func modifyRemoteFosterHome(remoteFosterHome: RemoteFosterHome, onModifyRemoteFosterHome: @escaping (DatabaseResult) -> Void) async throws {
        
        let remoteNonHumanValues = getDictionaryFromRemoteFosterHome(remoteFosterHome: remoteFosterHome)

        do {
            try await firebaseFirestore!
                .collection(Section.fosterHomes.path)
                .document(remoteFosterHome.id!)
                .updateData(remoteNonHumanValues)
            
            onModifyRemoteFosterHome(DatabaseResult.Success())
        } catch {
            log.e(tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl", message: "Error updating the remote foster home \(String(describing: remoteFosterHome.id))", throwable: nil)
            onModifyRemoteFosterHome(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func deleteRemoteFosterHome(id: String, ownerId: String, onDeleteRemoteFosterHome: @escaping (DatabaseResult) -> Void) async throws {
        do {
            try await firebaseFirestore!
                .collection(Section.fosterHomes.path)
                .document(id)
                .delete()
            
            onDeleteRemoteFosterHome(DatabaseResult.Success())
        } catch {
            log.e(tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl", message: "Error deleting the remote foster home \(id) from the owner \(ownerId): \(String(describing: error))", throwable: nil)
            onDeleteRemoteFosterHome(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func deleteAllMyRemoteFosterHomes(ownerId: String, onDeleteAllMyRemoteFosterHomes: @escaping (DatabaseResult) -> Void) async throws {
        do {
            let querySnapshotForAllMyFosterHomes = try await firebaseFirestore!
                .collection(Section.fosterHomes.path)
                .whereField("ownerId", isEqualTo: ownerId)
                .getDocuments()
            
            var isError: Bool = false
                            
            for fosterHomeQueryDocumentSnapshot in querySnapshotForAllMyFosterHomes.documents {
                do {
                    try await fosterHomeQueryDocumentSnapshot.reference.delete()
                } catch {
                    log.e(tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl", message: "Error deleting all remote foster homes from the owner \(ownerId): \(String(describing: error))", throwable: nil)
                    onDeleteAllMyRemoteFosterHomes(DatabaseResult.Error(message: String(describing: error)))
                    isError = true
                }
            }
            if isError == false {
                onDeleteAllMyRemoteFosterHomes(DatabaseResult.Success())
            }
        } catch {
            log.e(tag: "FireStoreRemoteFosterHomeRepositoryForIosDelegateImpl", message: "Error deleting all remote foster homes from the owner \(ownerId): \(String(describing: error))", throwable: nil)
            onDeleteAllMyRemoteFosterHomes(DatabaseResult.Error(message: String(describing: error)))
        }
    }
}
