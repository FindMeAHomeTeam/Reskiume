import ComposeApp

struct RemoteFosterHomeDTO: Codable {
    let id: String?
    let ownerId: String?
    let title: String?
    let description: String?
    let conditions: String?
    let imageUrl: String?
    let allAcceptedNonHumanAnimals: [RemoteAcceptedNonHumanAnimalForFosterHomeDTO]?
    let allResidentNonHumanAnimalIds: [RemoteResidentNonHumanAnimalIdForFosterHomeDTO]?
    let longitude: Double?
    let latitude: Double?
    let country: String?
    let city: String?
    let available: Bool?

    func toKotlin() -> RemoteFosterHome {
        
        let nonHumanAnimalTypesKotlin: [RemoteAcceptedNonHumanAnimalForFosterHome] =
            (allAcceptedNonHumanAnimals ?? []).map { $0.toKotlin() }
        
        let residentsKotlin: [RemoteResidentNonHumanAnimalIdForFosterHome] =
            (allResidentNonHumanAnimalIds ?? []).map { $0.toKotlin() }

        return RemoteFosterHome(
            id: id ?? "",
            ownerId: ownerId ?? "",
            title: title ?? "",
            description: description ?? "",
            conditions: conditions ?? "",
            imageUrl: imageUrl ?? "",
            allAcceptedNonHumanAnimals: nonHumanAnimalTypesKotlin,
            allResidentNonHumanAnimalIds: residentsKotlin,
            longitude: KotlinDouble(double: longitude ?? 0.0),
            latitude: KotlinDouble(double: latitude ?? 0.0),
            country: country ?? "",
            city: city ?? "",
            available: KotlinBoolean(value: available ?? false)
        )
    }
}
