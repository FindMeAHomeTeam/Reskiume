import Foundation
import ComposeApp

@MainActor
final class FirebaseCrashlyticsManager {
    static let shared = FirebaseCrashlyticsManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseCrashlyticsManager")

    private init() {}
    
    private var crashlyticsForIosHelper: CrashlyticsForIosHelper?
    private var crashlyticsForIos: CrashlyticsForIos?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        crashlyticsForIosHelper = CrashlyticsForIosHelper()
        crashlyticsForIos = CrashlyticsForIosImpl()
        crashlyticsForIosHelper!.crashlyticsForIosWrapper.updateCrashlyticsForIosDelegate(delegate: crashlyticsForIos)
    }
}
