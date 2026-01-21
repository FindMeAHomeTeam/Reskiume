import ComposeApp

struct RemoteAcceptedNonHumanAnimalGenderForFosterHomeDTO: Codable {
    let acceptedNonHumanAnimalGenderId: Int32?
    let fosterHomeId: String?
    let acceptedNonHumanAnimalGender: GenderDTO?

    func toKotlin() -> RemoteAcceptedNonHumanAnimalGenderForFosterHome {
        RemoteAcceptedNonHumanAnimalGenderForFosterHome(
            acceptedNonHumanAnimalGenderId: KotlinInt(int: acceptedNonHumanAnimalGenderId ?? 0),
            fosterHomeId: fosterHomeId ?? "",
            acceptedNonHumanAnimalGender: (acceptedNonHumanAnimalGender ?? GenderDTO.unselected).toKotlin()
        )
    }
}
