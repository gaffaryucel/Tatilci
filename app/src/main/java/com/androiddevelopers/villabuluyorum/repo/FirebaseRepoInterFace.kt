package com.androiddevelopers.villabuluyorum.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

interface FirebaseRepoInterFace {
    // Auth
    fun login(email: String, password: String): Task<AuthResult>
    fun forgotPassword(email: String): Task<Void>
    fun register(email: String, password: String): Task<AuthResult>

}