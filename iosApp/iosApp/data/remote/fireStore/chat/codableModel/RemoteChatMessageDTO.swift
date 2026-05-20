import ComposeApp

struct RemoteChatMessageDTO: Codable {
    let id: String?
    let chatId: String?
    let message: String?
    let senderId: String?
    let timestamp: CLong?

    func toKotlin() -> RemoteChatMessage {

        return RemoteChatMessage(
            id: id ?? "",
            chatId: chatId ?? "",
            message: message ?? "",
            senderId: senderId ?? "",
            timestamp: KotlinLong(longLong: Int64(timestamp ?? 0))
        )
    }
}
