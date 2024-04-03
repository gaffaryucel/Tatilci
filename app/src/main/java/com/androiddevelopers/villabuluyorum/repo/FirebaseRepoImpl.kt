package com.androiddevelopers.villabuluyorum.repo

import com.androiddevelopers.villabuluyorum.service.NotificationAPI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    storage: FirebaseStorage,
) : FirebaseRepoInterFace {

}