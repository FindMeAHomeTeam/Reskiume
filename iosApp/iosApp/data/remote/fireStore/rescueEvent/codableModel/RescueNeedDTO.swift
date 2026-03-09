import ComposeApp

enum RescueNeedDTO: String, Codable {
    case unselected
    case rescuers
    case fosterHome
        
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let status = try? container.decode(String.self)
        switch status {
        case "UNSELECTED": self = .unselected
        case "RESCUERS": self = .rescuers
        case "FOSTER_HOME": self = .fosterHome
        default:
            self = .unselected
        }
    }

    func toKotlin() -> RescueNeed {
        switch self {
        case .unselected: return .unselected
        case .rescuers: return .rescuers
        case .fosterHome: return .fosterHome
        }
    }
}
