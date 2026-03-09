import ComposeApp

struct RemoteNeedToCoverForRescueEventDTO: Codable {
    let needToCoverId: Int64?
    let rescueNeed: RescueNeedDTO?
    let rescueEventId: String?

    func toKotlin() -> RemoteNeedToCoverForRescueEvent {
        RemoteNeedToCoverForRescueEvent(
            needToCoverId: KotlinLong(longLong: Int64(needToCoverId ?? 0)),
            rescueNeed: (rescueNeed ?? RescueNeedDTO.unselected).toKotlin(),
            rescueEventId: rescueEventId ?? ""
        )
    }
}
