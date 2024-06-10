package com.izmirsoftware.tatilci.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.UserModel
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.toUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private var _authState = MutableLiveData<Resource<Boolean>>()
    val authState: LiveData<Resource<Boolean>>
        get() = _authState

    private var userToken = MutableLiveData<Resource<String>>()

    private var _registrationError = MutableLiveData<Resource<Boolean>>()
    val registrationError: LiveData<Resource<Boolean>>
        get() = _registrationError

    private var _isVerificationEmailSent = MutableLiveData<Resource<Boolean>>()
    val isVerificationEmailSent: LiveData<Resource<Boolean>>
        get() = _isVerificationEmailSent

    init {
        getToken()
    }

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String
    ) = viewModelScope.launch {
        _authState.value = Resource.loading(null)
        if (isInputValid(email, password, confirmPassword)) {
            firebaseRepo.register(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid ?: ""
                        createUser(
                            userId = userId,
                            email = email
                        )
                        _authState.value = Resource.success(null)
                        verify()
                    } else {
                        _authState.value = Resource.error(
                            task.exception?.localizedMessage ?: "error : try again",
                            null
                        )
                    }
                }
        } else {
            _authState.value = Resource.error("password mismatch", null)
        }
    }

    private fun createUser(
        userId: String,
        email: String,
        google: Boolean? = false
    ) = viewModelScope.launch {
        val tempUsername = email.substringBefore("@")
        println("createUser")
        val user = makeUser(
            userId,
            tempUsername,
            email,
            userToken.value?.data.toString()
        )

        firebaseRepo.addUserToFirestore(user)
            .addOnSuccessListener {
                if (google == false) {
                    verify()
                } else {
                    println("else success")
                    _authState.value = Resource.success(true)
                }
            }.addOnFailureListener { e ->
                _authState.value = Resource.error(e.localizedMessage ?: "error : try again later", null)
            }
    }

    private fun verify() = viewModelScope.launch {
        val currentUser = firebaseAuth.currentUser
        currentUser?.sendEmailVerification()?.addOnSuccessListener {
            _isVerificationEmailSent.value = Resource.success()
            firebaseAuth.signOut()
        }
    }

    private fun makeUser(
        userId: String,
        userName: String,
        email: String,
        token: String,
    ): UserModel {
        println("make user")
        return UserModel(
            userId = userId,
            username = userName,
            email = email,
            token = token
        )
    }

    private fun isPasswordConfirmed(password: String, confirmPassword: String): Boolean {
        return if (password == confirmPassword) {
            true
        } else {
            _registrationError.value = Resource.error("şifreler uyuşmuyor", false)
            false
        }
    }

    private fun isInputValid(email: String, password: String, confirmPassword: String): Boolean {
        return if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            val errorMessage = "Lütfen tüm alanları doldurun."
            _registrationError.value = Resource.error(errorMessage, true)
            false
        } else {
            isPasswordConfirmed(password, confirmPassword)
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                userToken.value = Resource.error("", null)
                return@addOnCompleteListener
            }
            val token = it.result //this is the token retrieved
            userToken.value = Resource.success(token)
        }
    }

    fun signInWithGoogle(idToken: String?) {
        println("signInWithGoogle")
        _authState.value = Resource.loading(null)
        val cridential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(cridential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.user
                if (user != null && user.email != null) {
                    println("user != null")
                    checkIsUserExist(
                        user.uid,
                        user.email!!
                    )
                }
            } else {
                _authState.value = Resource.error("Hata : Tekrar deneyin", null)
            }
        }
    }
    private fun checkIsUserExist(userId: String,email : String) = viewModelScope.launch {
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { document ->
                val user = document.toUserModel()
                if (user?.userId != null) {
                    println("User exists: ${user.userId}")
                    _authState.value = Resource.success(true)
                } else {
                    println("User does not exist, creating new user")
                    createUser(
                        userId = userId,
                        email = email,
                        google = true
                    )
                }
            }
            .addOnFailureListener { exception ->
                println("Failed to get user data, creating new user: ${exception.message}")
                createUser(
                    userId = userId,
                    email = email,
                    google = true
                )
            }
    }


}