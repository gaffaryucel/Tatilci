package com.izmirsoftware.tatilci.viewmodel.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.UserModel
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.toUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val _authState = MutableLiveData<Resource<Boolean>>()
    val authState: LiveData<Resource<Boolean>> get() = _authState
    private val _forgotPassword = MutableLiveData<Resource<Boolean>>()
    val forgotPassword: LiveData<Resource<Boolean>> get() = _forgotPassword
    private val _verificationEmailSent = MutableLiveData<Resource<Boolean>>()
    val verificationEmailSent: LiveData<Resource<Boolean>> get() = _verificationEmailSent

    private var userToken = MutableLiveData<Resource<String>>()

    fun getUser() = firebaseAuth.currentUser
    fun signOut() = firebaseAuth.signOut()

    init {
        getToken()
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = Resource.loading(true)
        firebaseRepo.login(email, password)
            .addOnCompleteListener {
                _authState.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _authState.value = Resource.success(true)
                    updateUserToken(it.result.user?.uid.toString())
                } else {
                    _authState.value =
                        it.exception?.localizedMessage?.let { message ->
                            Resource.error(message, false)
                        }
                }
            }
    }

    fun forgotPassword(email: String) = viewModelScope.launch {
        _forgotPassword.value = Resource.loading(true)
        firebaseRepo.forgotPassword(email)
            .addOnCompleteListener {
                _forgotPassword.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _forgotPassword.value = Resource.success(true)
                } else {
                    _forgotPassword.value =
                        it.exception?.localizedMessage?.let { message ->
                            Resource.error(message, false)
                        }
                }
            }
    }

    fun sendVerificationEmail() = viewModelScope.launch {
        _verificationEmailSent.value = Resource.loading(true)
        getUser()?.let { currentUser ->
            currentUser.sendEmailVerification().addOnCompleteListener {
                _verificationEmailSent.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _verificationEmailSent.value = Resource.success(true)
                } else {
                    _verificationEmailSent.value =
                        it.exception?.localizedMessage?.let { message ->
                            Resource.error(message, false)
                        }
                }
            }
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

    private fun updateUserToken(currentUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tokenMap = hashMapOf<String, Any?>(
                "token" to userToken.value?.data
            )
            firebaseRepo.updateUserData(currentUserId, tokenMap)
        }
    }
    fun signInWithGoogle(idToken: String?) {
        _authState.value = Resource.loading(null)
        val cridential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(cridential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.user
                if (user != null && user.email != null) {
                    checkIsUserExist(
                        user.uid,
                        user.email!!
                    )
                }
            }else{
                _authState.value = Resource.error("Hata : Tekrar deneyin",null)
            }
        }
    }
    private fun createUser(
        userId : String,
        email: String,
    ) = viewModelScope.launch{
        val tempUsername = email.substringBefore("@")
        val user = makeUser(userId,tempUsername,email,userToken.value?.data.toString())
        firebaseRepo.addUserToFirestore(user)
            .addOnSuccessListener {
                updateDisplayName(tempUsername)
            }.addOnFailureListener { e ->
                _authState.value = Resource.error(e.localizedMessage ?: "error : try again later",null)
            }
    }
    private fun makeUser(userId : String,userName: String,email: String,token: String) : UserModel {
        return UserModel(
            userId = userId,
            username = userName,
            email = email,
            token = token
        )
    }
    private fun updateDisplayName(newDisplayName : String){
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newDisplayName)
            .build()

        firebaseAuth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.value = Resource.success(true)
            } else {
                _authState.value = Resource.error(task.exception?.localizedMessage ?: "error : try again later",null)
            }
        }
    }
    private fun checkIsUserExist(userId: String,email : String) = viewModelScope.launch {
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { document ->
                val user = document.toUserModel()
                if (user?.userId != null) {
                    _authState.value = Resource.success(true)
                } else {
                    createUser(
                        userId = userId,
                        email = email
                    )
                }
            }
            .addOnFailureListener { exception ->
                createUser(
                    userId = userId,
                    email = email
                )
            }
    }


}