package com.androiddevelopers.villabuluyorum.viewmodel.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject
constructor(
    private val firebaseRepo : FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private var _authState = MutableLiveData<Resource<Boolean>>()
    val authState : LiveData<Resource<Boolean>>
        get() = _authState
    private var userToken = MutableLiveData<String>()

    private val currentUserID = firebaseAuth.currentUser?.uid.toString()

    init {
        getToken()
    }
    fun createUserName(name : String){
        _authState.value = Resource.loading(null)
        firebaseAuth.currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        updateUserData(name)
                    } else {
                        _authState.value = Resource.error("Hata : Tekrar deneyin",null)
                    }
                }
        }
    }
    private fun saveUserToFirestore() = viewModelScope.launch{
        _authState.value = Resource.loading(null)
        firebaseAuth.currentUser?.let {
            // Kullanıcı oturum açmışsa
           val user = UserModel(
               userId = it.uid,
               phoneNumber =  it.phoneNumber,
               token = userToken.value
           )
            firebaseRepo.addUserToFirestore(user)
                .addOnSuccessListener {
                    _authState.value = Resource.success(null)
                }.addOnFailureListener { e ->
                    _authState.value = Resource.error(e.localizedMessage ?: "error : try again later",null)
                }
        }
    }
    private fun updateUserData(userName : String){
        val userMap = hashMapOf<String, Any?>(
            "username" to userName
        )
        firebaseRepo.updateUserData(currentUserID,userMap)
            .addOnSuccessListener {
                _authState.value = Resource.success(null)
            }.addOnFailureListener { e ->
                _authState.value = Resource.error(e.localizedMessage ?: "error : try again later",null)
            }
    }

    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                _authState.value = it.exception?.localizedMessage?.let { it1 -> Resource.error(it1,null) }
                return@addOnCompleteListener
            }
            val token = it.result //this is the token retrieved
            userToken.value = token
        }
    }

    fun signInWithPhoneAuthCredential(activity : Activity,credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user?.displayName.isNullOrEmpty()) {
                        _authState.value = Resource.success(false)
                        saveUserToFirestore()
                    } else {
                        _authState.value = Resource.success(true)
                    }
                } else {
                    _authState.value = Resource.error(  "error : try again later",null)
                }
            }
    }

}