import Foundation
import ComposeApp

@MainActor
final class FirebaseAnalyticsManager {
    static let shared = FirebaseAnalyticsManager()
    private var didStart = false
    private let lock = DispatchQueue(label: "com.findmeahometeam.reskiume.FirebaseAnalyticsManager")

    private init() {}
    
    private var analyticsForIosHelper: AnalyticsForIosHelper?
    private var analyticsForIos: AnalyticsForIos?

    func startIfNeeded() {
        
        guard !didStart else { return }
        didStart = true
        
        analyticsForIosHelper = AnalyticsForIosHelper()
        analyticsForIos = AnalyticsForIosImpl()
        analyticsForIosHelper!.analyticsForIosWrapper.updateAnalyticsForIosDelegate(delegate: analyticsForIos)
    }
}
