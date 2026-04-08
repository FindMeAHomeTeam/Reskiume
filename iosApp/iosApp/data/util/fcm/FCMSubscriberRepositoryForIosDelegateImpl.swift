import ComposeApp
import FirebaseMessaging

class FCMSubscriberRepositoryForIosDelegateImpl: FCMSubscriberRepositoryForIosDelegate {

    private var log: Log

    init(log: Log) {

        self.log = log
        logFCMToken()
    }

    func logFCMToken() {
        // Async version of generating the token
        Task {
            do {
                let token = try await Messaging.messaging().token()
                log.d(
                    tag: "FCMSubscriberRepositoryForIosImpl",
                    message: "FCM registration successful with token: \(token)"
                )
            } catch {
                log.e(
                    tag: "FCMSubscriberRepositoryForIosImpl",
                    message: "Fetching FCM registration token failed: \(error)",
                    throwable: nil
                )
            }
        }
    }

    func subscribeToAllTopics(allTopics: [Subscription]) async throws {
        do {
            for subscription in allTopics {
                
                try await Messaging.messaging().subscribe(toTopic: subscription.topic)
                log.d(
                    tag: "FCMSubscriberRepositoryForIosImpl",
                    message: "Subscribed to \(subscription.topic) topic"
                )
            }
        } catch {
            log.e(
                tag: "FCMSubscriberRepositoryForIosImpl",
                message: "Failed to subscribe to all topics: \(error)",
                throwable: nil
            )
            throw error
        }
    }
    
    func subscribeToTopic(topic: String) async throws {
        do {
            try await Messaging.messaging().subscribe(toTopic: topic)
            log.d(
                tag: "FCMSubscriberRepositoryForIosImpl",
                message: "Subscribed to \(topic) topic")
        } catch {
            log.e(
                tag: "FCMSubscriberRepositoryForIosImpl",
                message: "Failed to subscribe to topic: \(error)",
                throwable: nil
            )
            throw error
        }
    }
    
    func unsubscribeFromAllTopics(allTopics: [Subscription]) async throws {
        do {
            for subscription in allTopics {
                
                try await Messaging.messaging().unsubscribe(fromTopic: subscription.topic)
                log.d(
                    tag: "FCMSubscriberRepositoryForIosImpl",
                    message: "Unsubscribed from \(subscription.topic) topic"
                )
            }
        } catch {
            log.e(
                tag: "FCMSubscriberRepositoryForIosImpl",
                message: "Failed to unsubscribe from all topics: \(error)",
                throwable: nil
            )
            throw error
        }
    }

    
    func unsubscribeFromTopic(topic: String) async throws {
        do {
            try await Messaging.messaging().unsubscribe(fromTopic: topic)
            log.d(
                tag: "FCMSubscriberRepositoryForIosImpl",
                message: "Unsubscribed from \(topic) topic"
            )
        } catch {
            log.e(
                tag: "FCMSubscriberRepositoryForIosImpl",
                message: "Failed to unsubscribe: \(error)",
                throwable: nil
            )
            throw error
        }
    }
}
