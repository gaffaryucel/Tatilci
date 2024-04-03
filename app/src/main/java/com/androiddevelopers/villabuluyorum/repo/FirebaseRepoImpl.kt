package com.androiddevelopers.villabuluyorum.repo

import com.androiddevelopers.villabuluyorum.service.NotificationAPI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseDatabase,
    storage: FirebaseStorage,
    private val notificationAPI: NotificationAPI
) : FirebaseRepoInterFace {

    //StorageRef
    private val imagesParentRef = storage.reference.child("users")
    private val profilePhotoRef = storage.reference

    //RealtimeRef
    private val messagesReference = database.getReference("messages")


    //Auth
    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun forgotPassword(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }

    override fun register(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

}