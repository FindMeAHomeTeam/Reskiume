import ComposeApp

struct RemoteAcceptedNonHumanAnimalForFosterHomeDTO: Codable {
    let acceptedNonHumanAnimalId: String?
    let fosterHomeId: String?
    let acceptedNonHumanAnimalType: NonHumanAnimalTypeDTO?
    let acceptedNonHumanAnimalGender: GenderDTO?

    func toKotlin() -> RemoteAcceptedNonHumanAnimalForFosterHome {
        RemoteAcceptedNonHumanAnimalForFosterHome(
            acceptedNonHumanAnimalId: acceptedNonHumanAnimalId ?? "",
            fosterHomeId: fosterHomeId ?? "",
            acceptedNonHumanAnimalType: (acceptedNonHumanAnimalType ?? NonHumanAnimalTypeDTO.unselected).toKotlin(),
            acceptedNonHumanAnimalGender: (acceptedNonHumanAnimalGender ?? GenderDTO.unselected).toKotlin()
        )
    }
}
