import ComposeApp

struct RemoteChatDTO: Codable {
    let id: String?
    let fosterHomeId: String?
    let rescueEventId: String?
    let chatHolderId: String?
    let allNonHumanAnimalsInfo: [RemoteNonHumanAnimalInfoDTO]?
    let allActivistsInfo: [String]?
    let allBlockedUsersInfo: [RemoteBlockedUserInfoDTO]?
    let acceptedFoster: Bool?
    let finished: Bool?
    let addReview: Bool?
    let timestamp: CLong?

    func toKotlin() -> RemoteChat {
        
        let allNonHumanAnimalsInfoKotlin: [RemoteNonHumanAnimalInfo] =
            (allNonHumanAnimalsInfo ?? []).map { $0.toKotlin() }
        
        let allBlockedUsersInfoKotlin: [RemoteBlockedUserInfo] =
            (allBlockedUsersInfo ?? []).map { $0.toKotlin() }

        return RemoteChat(
            id: id ?? "",
            fosterHomeId: fosterHomeId ?? "",
            rescueEventId: rescueEventId ?? "",
            chatHolderId: chatHolderId ?? "",
            allNonHumanAnimalsInfo: allNonHumanAnimalsInfoKotlin,
            allActivistsInfo: allActivistsInfo ?? [],
            allBlockedUsersInfo: allBlockedUsersInfoKotlin,
            acceptedFoster: KotlinBoolean(bool: acceptedFoster ?? false),
            finished: KotlinBoolean(bool: finished ?? false),
            addReview: KotlinBoolean(bool: addReview ?? false),
            timestamp: KotlinLong(longLong: Int64(timestamp ?? 0))
        )
    }
}
