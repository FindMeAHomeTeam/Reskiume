import ComposeApp
import FirebaseCore
import FirebaseAnalytics

class AnalyticsForIosImpl: AnalyticsForIos {
    func logEvent(message: String, params: [String : Any]?) {
        Analytics.logEvent(message, parameters: params)
    }
}
