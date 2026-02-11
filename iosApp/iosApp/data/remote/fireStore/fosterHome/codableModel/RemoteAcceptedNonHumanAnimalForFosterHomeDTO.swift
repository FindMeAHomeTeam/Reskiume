import ComposeApp

struct RemoteAcceptedNonHumanAnimalForFosterHomeDTO: Codable {
    let acceptedNonHumanAnimalId: Int64?
    let fosterHomeId: String?
    let acceptedNonHumanAnimalType: NonHumanAnimalTypeDTO?
    let acceptedNonHumanAnimalGender: GenderDTO?

    func toKotlin() -> RemoteAcceptedNonHumanAnimalForFosterHome {
        RemoteAcceptedNonHumanAnimalForFosterHome(
            acceptedNonHumanAnimalId: KotlinLong(longLong: Int64(acceptedNonHumanAnimalId ?? 0)),
            fosterHomeId: fosterHomeId ?? "",
            acceptedNonHumanAnimalType: (acceptedNonHumanAnimalType ?? NonHumanAnimalTypeDTO.unselected).toKotlin(),
            acceptedNonHumanAnimalGender: (acceptedNonHumanAnimalGender ?? GenderDTO.unselected).toKotlin()
        )
    }
}
