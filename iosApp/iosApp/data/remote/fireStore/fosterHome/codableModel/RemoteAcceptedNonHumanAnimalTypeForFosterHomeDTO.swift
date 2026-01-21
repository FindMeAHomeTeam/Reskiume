import ComposeApp

struct RemoteAcceptedNonHumanAnimalTypeForFosterHomeDTO: Codable {
    let acceptedNonHumanAnimalTypeId: Int32?
    let fosterHomeId: String?
    let acceptedNonHumanAnimalType: NonHumanAnimalTypeDTO?

    func toKotlin() -> RemoteAcceptedNonHumanAnimalTypeForFosterHome {
        RemoteAcceptedNonHumanAnimalTypeForFosterHome(
            acceptedNonHumanAnimalTypeId: KotlinInt(int: acceptedNonHumanAnimalTypeId ?? 0),
            fosterHomeId: fosterHomeId ?? "",
            acceptedNonHumanAnimalType: (acceptedNonHumanAnimalType ?? NonHumanAnimalTypeDTO.unselected).toKotlin()
        )
    }
}
