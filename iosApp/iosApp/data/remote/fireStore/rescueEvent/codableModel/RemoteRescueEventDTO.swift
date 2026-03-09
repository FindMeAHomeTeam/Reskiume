import ComposeApp

struct RemoteRescueEventDTO: Codable {
    let id: String?
    let creatorId: String?
    let title: String?
    let description: String?
    let imageUrl: String?
    let allNonHumanAnimalsToRescue: [RemoteNonHumanAnimalToRescueForRescueEventDTO]?
    let allNeedsToCover: [RemoteNeedToCoverForRescueEventDTO]?
    let longitude: Double?
    let latitude: Double?
    let country: String?
    let city: String?

    func toKotlin() -> RemoteRescueEvent {
        
        let allNonHumanAnimalsToRescueKotlin: [RemoteNonHumanAnimalToRescueForRescueEvent] =
            (allNonHumanAnimalsToRescue ?? []).map { $0.toKotlin() }
        
        let allNeedsToCoverKotlin: [RemoteNeedToCoverForRescueEvent] =
            (allNeedsToCover ?? []).map { $0.toKotlin() }

        return RemoteRescueEvent(
            id: id ?? "",
            creatorId: creatorId ?? "",
            title: title ?? "",
            description: description ?? "",
            imageUrl: imageUrl ?? "",
            allNonHumanAnimalsToRescue: allNonHumanAnimalsToRescueKotlin,
            allNeedsToCover: allNeedsToCoverKotlin,
            longitude: KotlinDouble(double: longitude ?? 0.0),
            latitude: KotlinDouble(double: latitude ?? 0.0),
            country: country ?? "",
            city: city ?? ""
        )
    }
}
