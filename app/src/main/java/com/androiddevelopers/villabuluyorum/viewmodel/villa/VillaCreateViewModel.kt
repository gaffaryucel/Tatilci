package com.androiddevelopers.villabuluyorum.viewmodel.villa

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.androiddevelopers.villabuluyorum.model.VillaModel
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VillaCreateViewModel
@Inject
constructor(
    private val firebaseAuth: FirebaseAuth,
    private val repo : FirebaseRepoInterFace
) : ViewModel() {


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
}
