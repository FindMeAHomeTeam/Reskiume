import UIKit
import Firebase
import FirebaseAnalytics
import FirebaseCore
import FirebaseAuth
import FirebaseStorage
import FirebaseFirestore
import FirebaseMessaging
import UserNotifications
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        KMPViewControllerWrapper()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {

    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {

        FirebaseApp.configure()

        UNUserNotificationCenter.current().delegate = self
        application.registerForRemoteNotifications()
        Messaging.messaging().delegate = self
        return true
    }

    // Pass the APNs token to Firebase
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        Messaging.messaging().apnsToken = deviceToken
    }

    // Handle FCM token refresh
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("Firebase registration token: \(String(describing: fcmToken))")
    }

    // Handle push notifications in the foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification) async -> UNNotificationPresentationOptions {
        let userInfo = notification.request.content.userInfo
        print("Foreground notification: \(userInfo)")

        // Check the data payload dictionary
        let notificationType = userInfo["notificationType"] as? String ?? ""

        if notificationType == "" {
            print("notificationType is empty, skipping notification")
            return []
        }
        let messagingServiceViewModel = MessagingServiceViewModel()
        do {
            var activistId = ""
            try await messagingServiceViewModel.retrieveUserId { id in
                
                activistId = id
            }
            
            if activistId == "" {
                print("No activist ID found, skipping notification")
                return []
            }
            
            if notificationType == "rescueEvent" {
                
                let creatorId = userInfo["creatorId"] as? String
                
                if creatorId == activistId {
                    print("Creator ID match with activist ID, skipping notification")
                    return []
                }
            }
            return [.list, .banner, .sound]
        } catch {
            print("Error retrieving user ID: \(error)")
            return []
        }
    }

    // Handle push notification clicks
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse) async {
        let userInfo = response.notification.request.content.userInfo
        print("Notification clicked: \(userInfo)")

        if let deeplink = userInfo["deeplink"] as? String,
           let url = URL(string: deeplink) {

            await UIApplication.shared.open(url)
        }
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}



