import UIKit
import Firebase
import FirebaseAnalytics
import FirebaseCore
import FirebaseAuth
import FirebaseStorage
import FirebaseFirestore
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        KMPViewControllerWrapper()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        return true
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}



