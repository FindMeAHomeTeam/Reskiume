import ComposeApp

struct RemoteNonHumanAnimalToRescueForRescueEventDTO: Codable {
    let nonHumanAnimalId: String?
    let caregiverId: String?
    let rescueEventId: String?

    func toKotlin() -> RemoteNonHumanAnimalToRescueForRescueEvent {
        RemoteNonHumanAnimalToRescueForRescueEvent(
            nonHumanAnimalId: nonHumanAnimalId ?? "",
            caregiverId: caregiverId ?? "",
            rescueEventId: rescueEventId ?? ""
        )
    }
}
