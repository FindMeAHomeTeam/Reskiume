import UIKit
import ComposeApp

class KMPViewControllerWrapper: UIViewController {
    private var didInitialize = false
    private let kmpViewController = MainViewControllerKt.MainViewController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Containment
        addChild(kmpViewController)
        view.addSubview(kmpViewController.view)
        kmpViewController.didMove(toParent: self)
        
        // Layout with constraints
        kmpViewController.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            kmpViewController.view.topAnchor.constraint(equalTo: view.topAnchor),
            kmpViewController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            kmpViewController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            kmpViewController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if !didInitialize {
            FirebaseAuthManager.shared.startIfNeeded()
            FirebaseDatabaseManager.shared.startIfNeeded()
            didInitialize = true
        }
    }
}
