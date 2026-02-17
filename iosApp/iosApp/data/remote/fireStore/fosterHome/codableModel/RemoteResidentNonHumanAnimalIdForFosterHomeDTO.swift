import ComposeApp

struct RemoteResidentNonHumanAnimalIdForFosterHomeDTO: Codable {
    let nonHumanAnimalId: String?
    let caregiverId: String?
    let fosterHomeId: String?
    
    func toKotlin() -> RemoteResidentNonHumanAnimalIdForFosterHome {
        RemoteResidentNonHumanAnimalIdForFosterHome(
            nonHumanAnimalId: nonHumanAnimalId ?? "",
            caregiverId: caregiverId ?? "",
            fosterHomeId: fosterHomeId ?? ""
        )
    }
}
