import ComposeApp

struct RemoteResidentNonHumanAnimalIdForFosterHomeDTO: Codable {
    let residentNonHumanAnimalId: String?
    let caregiverId: String?
    let fosterHomeId: String?
    
    func toKotlin() -> RemoteResidentNonHumanAnimalIdForFosterHome {
        RemoteResidentNonHumanAnimalIdForFosterHome(
            residentNonHumanAnimalId: residentNonHumanAnimalId ?? "",
            caregiverId: caregiverId ?? "",
            fosterHomeId: fosterHomeId ?? ""
        )
    }
}
