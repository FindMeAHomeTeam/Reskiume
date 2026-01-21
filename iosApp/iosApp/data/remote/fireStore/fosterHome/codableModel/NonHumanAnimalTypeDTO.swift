import ComposeApp

enum NonHumanAnimalTypeDTO: String, Codable {
    case unselected
    case dog
    case cat
    case bird
    case rabbit
    case rodent
    case ferret
    case reptile
    case fish
    case equid
    case hog
    case ovine
    case bobine
    case other
    
    init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        let status = try? container.decode(String.self)
        switch status {
        case "UNSELECTED": self = .unselected
        case "DOG": self = .dog
        case "CAT": self = .cat
        case "BIRD": self = .bird
        case "RABBIT": self = .rabbit
        case "RODENT": self = .rodent
        case "FERRET": self = .ferret
        case "REPTILE": self = .reptile
        case "FISH": self = .fish
        case "EQUID": self = .equid
        case "HOG": self = .hog
        case "OVINE": self = .ovine
        case "BOBINE": self = .bobine
        case "OTHER": self = .other
        default:
            self = .unselected
        }
    }
    
    func toKotlin() -> NonHumanAnimalType {
        switch self {
        case .unselected: return .unselected
        case .dog: return .dog
        case .cat: return .cat
        case .bird: return .bird
        case .rabbit: return .rabbit
        case .rodent: return .rodent
        case .ferret: return .ferret
        case .reptile: return .reptile
        case .fish: return .fish
        case .equid: return .equid
        case .hog: return .hog
        case .ovine: return .ovine
        case .bobine: return .bobine
        case .other: return .other
        }
    }
}
