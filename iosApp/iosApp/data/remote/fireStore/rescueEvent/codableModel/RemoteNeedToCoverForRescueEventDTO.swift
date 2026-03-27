import ComposeApp

struct RemoteNeedToCoverForRescueEventDTO: Codable {
    let needToCoverId: String?
    let rescueNeed: RescueNeedDTO?
    let rescueEventId: String?

    func toKotlin() -> RemoteNeedToCoverForRescueEvent {
        RemoteNeedToCoverForRescueEvent(
            needToCoverId: needToCoverId ?? "",
            rescueNeed: (rescueNeed ?? RescueNeedDTO.unselected).toKotlin(),
            rescueEventId: rescueEventId ?? ""
        )
    }
}
