package com.androiddevelopers.villabuluyorum.viewmodel.villa

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VillaCreateViewModel
@Inject
constructor(

) : ViewModel() {
    /*

        private val userId = firebaseAuth.currentUser?.uid.toString()

        private val firestoreDB = FirebaseFirestore.getInstance()
        private val storageRef = FirebaseStorage.getInstance().reference

        fun addVillaToFirestore(home: VillaModel): Task<Void> {
            return firestoreDB.collection("villas").document().set(home)
        }

        fun uploadImageToStorage(imageUri: Uri): Task<Uri> {
            val imageName = "image_${System.currentTimeMillis()}.jpg"
            val imageRef: StorageReference = storageRef.child("images/$imageName")

            return imageRef.putFile(imageUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    imageRef.downloadUrl
                }
        }

        fun saveVillaWithImage(home: VillaModel, imageUri: Uri): Task<Void> {
            return uploadImageToStorage(imageUri).onSuccessTask { downloadUri ->
                home.imageUrl = downloadUri.toString()
                addVillaToFirestore(home)
            }
        }
     */
}
