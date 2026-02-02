import ComposeApp

struct RemoteAcceptedNonHumanAnimalForFosterHomeDTO: Codable {
    let acceptedNonHumanAnimalId: Int32?
    let fosterHomeId: String?
    let acceptedNonHumanAnimalType: NonHumanAnimalTypeDTO?
    let acceptedNonHumanAnimalGender: GenderDTO?

    func toKotlin() -> RemoteAcceptedNonHumanAnimalForFosterHome {
        RemoteAcceptedNonHumanAnimalForFosterHome(
            acceptedNonHumanAnimalId: KotlinInt(int: acceptedNonHumanAnimalId ?? 0),
            fosterHomeId: fosterHomeId ?? "",
            acceptedNonHumanAnimalType: (acceptedNonHumanAnimalType ?? NonHumanAnimalTypeDTO.unselected).toKotlin(),
            acceptedNonHumanAnimalGender: (acceptedNonHumanAnimalGender ?? GenderDTO.unselected).toKotlin()
        )
    }
}
