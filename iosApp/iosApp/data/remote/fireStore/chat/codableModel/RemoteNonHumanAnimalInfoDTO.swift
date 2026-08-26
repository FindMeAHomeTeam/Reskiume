import Shared

struct RemoteNonHumanAnimalInfoDTO: Codable {
    let nonHumanAnimalId: String?
    let chatId: String?
    let caregiverId: String?

    func toKotlin() -> RemoteNonHumanAnimalInfo {

        return RemoteNonHumanAnimalInfo(
            nonHumanAnimalId: nonHumanAnimalId ?? "",
            chatId: chatId ?? "",
            caregiverId: caregiverId ?? ""
        )
    }
}
