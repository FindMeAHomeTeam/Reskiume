import ComposeApp

enum GenderDTO: String, Codable {
    case unselected
    case male
    case female
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let status = try? container.decode(String.self)
        switch status {
        case "UNSELECTED": self = .unselected
        case "MALE": self = .male
        case "FEMALE": self = .female
        default:
            self = .unselected
        }
    }

    func toKotlin() -> Gender {
        switch self {
        case .unselected: return .unselected
        case .male: return .male
        case .female: return .female
        }
    }
}
