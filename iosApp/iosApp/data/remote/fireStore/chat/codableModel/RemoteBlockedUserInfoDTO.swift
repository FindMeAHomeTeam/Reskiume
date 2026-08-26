import Shared

struct RemoteBlockedUserInfoDTO: Codable {
    let id: String?
    let chatId: String?
    let uid: String?

    func toKotlin() -> RemoteBlockedUserInfo {

        return RemoteBlockedUserInfo(
            id: id ?? "",
            chatId: chatId ?? "",
            uid: uid ?? ""
        )
    }
}
