import ComposeApp
import FirebaseDatabaseInternal
import FirebaseCore
import KMPNativeCoroutinesAsync


class RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateImpl: RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegate {

    private var databaseReference: DatabaseReference?
    
    private let NonHumanAnimalUidTaskHandle: Task<(), Never>?
    
    private var log: Log
            
    init (
        realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate: RealtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate,
        log: Log
    ) {
        self.log = log
        let database: Database! = Database.database()
        databaseReference = database.reference()
        
        NonHumanAnimalUidTaskHandle = Task {
            do {
                let emittedValues = asyncSequence(for: realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate.nonHumanAnimalIdAndCaregiverIdPairFlow)
                for try await nonHumanAnimalIdAndCaregiverIdPair in emittedValues {
                    
                    let nonHumanAnimalId: String = nonHumanAnimalIdAndCaregiverIdPair.first as? String ?? ""
                    let caregiverId: String = nonHumanAnimalIdAndCaregiverIdPair.second as? String ?? ""
                    
                    if caregiverId != "" {
                        let childPath = (nonHumanAnimalId == "") ? "\(caregiverId)" : "\(caregiverId)\\\(nonHumanAnimalId)"
                        
                        database.reference()
                            .child(Section.nonHumanAnimals.path)
                            .child(childPath)
                            .observeSingleEvent (of: .value, with: { snapshot in
                            var remoteNonHumanAnimals: [RemoteNonHumanAnimal] = []

                            for nonHumanAnimal in snapshot.children {
                                guard let nonHumanAnimalSnapshot = nonHumanAnimal as? DataSnapshot,
                                      let nonHumanAnimalNsDictionary = nonHumanAnimalSnapshot.value as? [String: Any] else {
                                    continue
                                }
                                let remoteNonHumanAnimal = RemoteNonHumanAnimal(
                                    id: nonHumanAnimalNsDictionary["id"] as? String ?? "",
                                    caregiverId: nonHumanAnimalNsDictionary["caregiverId"] as? String ?? "",
                                    name: nonHumanAnimalNsDictionary["name"] as? String ?? "",
                                    ageCategory: AgeCategory.entries.first(where: { $0.name == (nonHumanAnimalNsDictionary["ageCategory"] as? String ?? AgeCategory.unselected.name) }) ?? AgeCategory.unselected,
                                    description: nonHumanAnimalNsDictionary["description"] as? String ?? "",
                                    imageUrl: nonHumanAnimalNsDictionary["imageUrl"] as? String ?? "",
                                    nonHumanAnimalType: NonHumanAnimalType.entries.first(where: { $0.name == (nonHumanAnimalNsDictionary["nonHumanAnimalType"] as? String ?? NonHumanAnimalType.unselected.name) }) ?? NonHumanAnimalType.unselected,
                                    gender: Gender.entries.first(where: { $0.name == (nonHumanAnimalNsDictionary["gender"] as? String ?? Gender.unselected.name) }) ?? Gender.unselected
                                )
                                remoteNonHumanAnimals.append(remoteNonHumanAnimal)
                            }
                            realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate.updateRemoteNonHumanAnimalListFlow(delegate: remoteNonHumanAnimals)
                            
                        }) { error in
                            log.e(
                                tag: "RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateImpl",
                                message: "Error retrieving the remote non human animal \(nonHumanAnimalId) for the caregiver id \(caregiverId): \(error.localizedDescription)",
                                throwable: nil
                            )
                            realtimeDatabaseRemoteNonHumanAnimalFlowsRepositoryForIosDelegate.updateRemoteNonHumanAnimalListFlow(delegate: [])
                        }
                    }
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
    }
    
    deinit {
        NonHumanAnimalUidTaskHandle?.cancel()
    }
    
    private func getNSDictionaryFromRemoteNonHumanAnimal(remoteNonHumanAnimal: RemoteNonHumanAnimal) -> NSDictionary {
        return [
            "id": remoteNonHumanAnimal.id!,
            "caregiverId": remoteNonHumanAnimal.caregiverId!,
            "name": remoteNonHumanAnimal.name!,
            "ageCategory": remoteNonHumanAnimal.ageCategory!,
            "description": remoteNonHumanAnimal.description_!,
            "imageUrl": remoteNonHumanAnimal.imageUrl!,
            "nonHumanAnimalType": remoteNonHumanAnimal.nonHumanAnimalType!,
            "gender": remoteNonHumanAnimal.gender!
        ]
    }
    
    func insertRemoteNonHumanAnimal(remoteNonHumanAnimal: RemoteNonHumanAnimal, onInsertRemoteNonHumanAnimal: @escaping (DatabaseResult) -> Void) async {
        do {
            try await databaseReference!
                .child(Section.nonHumanAnimals.path)
                .child(remoteNonHumanAnimal.caregiverId!)
                .child(remoteNonHumanAnimal.id!)
                .setValue(getNSDictionaryFromRemoteNonHumanAnimal(remoteNonHumanAnimal: remoteNonHumanAnimal))
            onInsertRemoteNonHumanAnimal(DatabaseResult.Success())
        } catch {
            log.e(
                tag: "RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateImpl",
                message: "Error inserting the remote non human animal \(String(describing: remoteNonHumanAnimal.caregiverId))",
                throwable: nil
            )
            onInsertRemoteNonHumanAnimal(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func modifyRemoteNonHumanAnimal(remoteNonHumanAnimal: RemoteNonHumanAnimal, onModifyRemoteNonHumanAnimal: @escaping (DatabaseResult) -> Void) async throws {
        
        let remoteNonHumanValues = getNSDictionaryFromRemoteNonHumanAnimal(remoteNonHumanAnimal: remoteNonHumanAnimal)
        let childUpdates = ["/\(Section.nonHumanAnimals.path)/\(remoteNonHumanAnimal.caregiverId!)/\(remoteNonHumanAnimal.id!)": remoteNonHumanValues]

        do {
            try await databaseReference?.updateChildValues(childUpdates)
            onModifyRemoteNonHumanAnimal(DatabaseResult.Success())
        } catch {
            log.e(tag: "RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateImpl", message: "Error updating the remote non human animal \(String(describing: remoteNonHumanAnimal.id))", throwable: nil)
            onModifyRemoteNonHumanAnimal(DatabaseResult.Error(message: String(describing: error)))
        }
    }
    
    func deleteRemoteNonHumanAnimal(id: String, caregiverId: String, onDeleteRemoteNonHumanAnimal: @escaping (DatabaseResult) -> Void) {
        databaseReference!
            .child(Section.nonHumanAnimals.path)
            .child(caregiverId)
            .child(id)
            .removeValue { error, _ in
            if (error == nil) {
                onDeleteRemoteNonHumanAnimal(DatabaseResult.Success())
            } else {
                self.log.e(
                    tag: "RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateImpl",
                    message: "Error deleting the remote non human animal \(id) from the caregiver \(caregiverId): \(String(describing: error))",
                    throwable: nil
                )
                onDeleteRemoteNonHumanAnimal(DatabaseResult.Error(message: String(describing: error)))
            }
        }
    }
    
    func deleteAllRemoteNonHumanAnimals(caregiverId: String, onDeleteAllRemoteNonHumanAnimals: @escaping (DatabaseResult) -> Void) {
        databaseReference!
            .child(Section.nonHumanAnimals.path)
            .child(caregiverId)
            .removeValue { error, _ in
            if (error == nil) {
                onDeleteAllRemoteNonHumanAnimals(DatabaseResult.Success())
            } else {
                self.log.e(
                    tag: "RealtimeDatabaseRemoteNonHumanAnimalRepositoryForIosDelegateImpl",
                    message: "Error deleting the remote non human animals from the caregiver \(caregiverId): \(String(describing: error))",
                    throwable: nil
                )
                onDeleteAllRemoteNonHumanAnimals(DatabaseResult.Error(message: String(describing: error)))
            }
        }
    }
}
