import ComposeApp
import Firebase

class CrashlyticsForIosImpl: CrashlyticsForIos {
    
    func log(message: String) {
        Crashlytics.crashlytics().log(message)
    }
    
    func logError(message: String) {
        let userInfo = [NSLocalizedDescriptionKey: NSLocalizedString(message, comment: "")]
        let error = NSError.init(domain: NSCocoaErrorDomain,
                                 code: -1001,
                                 userInfo: userInfo)
        Crashlytics.crashlytics().record(error: error)
    }
}
