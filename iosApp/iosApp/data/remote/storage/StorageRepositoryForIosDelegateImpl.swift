import ComposeApp
import FirebaseStorage

class StorageRepositoryForIosDelegateImpl: StorageRepository {
    
    private let storageRef: StorageReference = Storage.storage().reference()

    private func getStorageReference(imageType: Paths, userUid: String) -> StorageReference {
        return switch imageType {
            case Paths.users: storageRef.child(Paths.users.path).child(userUid).child("\(userUid).webp")
            default: storageRef
        }
    }
    
    func uploadImage(userUid: String, imageType: Paths, imageUri: String, onImageUploaded: @escaping (String) -> Void) {
        let imageRef: StorageReference = getStorageReference(imageType: imageType, userUid: userUid)
        
        let localFile = URL(string: imageUri)!
        
        imageRef.putFile(from: localFile, metadata: nil) { metadata, error in
            guard metadata != nil else {
                Log().e(tag: "StorageRepositoryForIosDelegateImpl", message: "Error uploading image: \(String(describing: error))", throwable: nil)
                onImageUploaded("")
                return
            }
            imageRef.downloadURL { (url, error) in
                guard let downloadURL = url else {
                    Log().e(tag: "StorageRepositoryForIosDelegateImpl", message: "Error downloading image: \(String(describing: error))", throwable: nil)
                    onImageUploaded("")
                    return
                }
                onImageUploaded(downloadURL.absoluteString)
            }
        }
    }
    
    func deleteLocalImage(userUid: String, currentImagePath: String, onImageDeleted: @escaping (KotlinBoolean) -> Void) {
        let fileName: String = String(currentImagePath.split(separator: "/").last ?? "")
        if let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {
            let absoluteFilePath = documentsDirectory.appendingPathComponent(fileName)
            try? FileManager.default.removeItem(at: absoluteFilePath)
            onImageDeleted(true)
        } else {
            Log().e(tag: "StorageRepositoryForIosDelegateImpl", message: "deleteLocalImage: Error deleting the user avatar", throwable: nil)
            onImageDeleted(false)
        }
    }
    
    func saveImage(userUid: String, imageType: Paths, onImageSaved: @escaping (String) -> Void) {
        let imageRef: StorageReference = getStorageReference(imageType: imageType, userUid: userUid)
        
        let fileName: String = switch imageType {
            case Paths.users: "\(userUid).webp"
            default: "\(userUid).webp"
        }
        if let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {
            let absoluteFilePath = documentsDirectory.appendingPathComponent(fileName)
            try? FileManager.default.removeItem(at: absoluteFilePath)
            
            // Download to the local filesystem
            imageRef.write(toFile: absoluteFilePath) { url, error in
                if error != nil {
                    onImageSaved("")
                    Log().e(tag: "StorageRepositoryForIosDelegateImpl", message: "Error saving the user avatar \(String(describing: error!.localizedDescription))", throwable: nil)
                } else {
                    onImageSaved(url?.absoluteString ?? "")
                }
            }
        }
    }
    
    func deleteRemoteImage(userUid: String, imageType: Paths, onImageDeleted: @escaping (KotlinBoolean) -> Void) async {
        let imageRef: StorageReference = getStorageReference(imageType: imageType, userUid: userUid)
        do {
            try await imageRef.delete()
            onImageDeleted(true)
        } catch {
            Log().e(
                tag: "StorageRepositoryForIosDelegateImpl",
                message: "Error deleting the user avatar for user \(userUid): \(String(describing: error.localizedDescription))",
                throwable: nil
            )
            onImageDeleted(false)
        }
    }
}
