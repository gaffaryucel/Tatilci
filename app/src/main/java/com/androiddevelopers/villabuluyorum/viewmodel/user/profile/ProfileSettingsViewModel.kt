package com.androiddevelopers.villabuluyorum.viewmodel.user.profile

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val repo : FirebaseRepoInterFace,
    private val auth : FirebaseAuth
):ViewModel() {

    private val currentUserId = auth.currentUser?.uid.toString()


    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _userInfoMessage = MutableLiveData<Resource<Boolean>>()
    val userInfoMessage: LiveData<Resource<Boolean>>
        get() = _userInfoMessage

    private var _exitMessage = MutableLiveData<Resource<Boolean>>()
    val exitMessage: LiveData<Resource<Boolean>>
        get() = _exitMessage

    init {
        getUserData()
    }
    private fun getUserData() = viewModelScope.launch{
        _userInfoMessage.value = Resource.loading(null)
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
                _userInfoMessage.value = Resource.success( null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _userInfoMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    fun signOutAndExit(context : Context){
        _exitMessage.value = Resource.loading( null)
        val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut()
        auth.signOut()
        _exitMessage.value = Resource.success( null)
    }
}