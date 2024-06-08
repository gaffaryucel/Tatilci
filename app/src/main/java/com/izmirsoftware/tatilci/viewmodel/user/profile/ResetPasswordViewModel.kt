package com.izmirsoftware.tatilci.viewmodel.user.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.izmirsoftware.tatilci.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel
@Inject
constructor(
    private val auth: FirebaseAuth,
) : ViewModel() {

    private var _resetPasswordMessage = MutableLiveData<Resource<Boolean>>()
    val resetPasswordMessage: LiveData<Resource<Boolean>>
        get() = _resetPasswordMessage

    fun sendPasswordResetEmail(email : String) {
        _resetPasswordMessage.value = Resource.loading(null)
        if (email.equals(auth.currentUser?.email)){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _resetPasswordMessage.value = Resource.success(null)
                    } else {
                        _resetPasswordMessage.value = Resource.error("hata",null)
                    }
                }
        }else{
            _resetPasswordMessage.value = Resource.error("mail",null)
        }

    }
    fun signOutAndExit(context: Context) {
        val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut()
        auth.signOut()
    }
}