package com.androiddevelopers.villabuluyorum.viewmodel.host.profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.ReviewModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.repo.SharedPreferencesRepo
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toReview
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostProfileViewModel
@Inject
constructor(
    private val sharedPreferencesRepo: SharedPreferencesRepo,
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val currentUserId = auth.currentUser?.uid.toString()

    private var _userVillas = MutableLiveData<Int>()
    val userVillas: LiveData<Int>
        get() = _userVillas

    //Review
    private var _userReviews = MutableLiveData<List<ReviewModel>>()
    val userReviews: LiveData<List<ReviewModel>>
        get() = _userReviews

    //User
    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData


    //Exit
    private var _exitMessage = MutableLiveData<Resource<Boolean>>()
    val exitMessage: LiveData<Resource<Boolean>>
        get() = _exitMessage

    init {
        getUserVillas()
        getUserData()
        getUserReviews()
    }
    private fun getUserVillas() = viewModelScope.launch {
        repo.getVillasByUserId(currentUserId)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa -> villaList.add(villa) }
                }
                _userVillas.value = villaList.size
            }
    }

    private fun getUserReviews() = viewModelScope.launch {
        repo.getAllReviewsByUserId(currentUserId)
            .addOnSuccessListener {
                val reviewList = mutableListOf<ReviewModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toReview()?.let { review ->
                        reviewList.add(review)
                    }
                }
                _userReviews.value = reviewList
            }

    }

    private fun getUserData() = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(null)
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
                _firebaseMessage.value = Resource.success(null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun setStartModeUser() {
        sharedPreferencesRepo.setStartModeUser()
    }

    fun signOutAndExit(context: Context) {
        _exitMessage.value = Resource.loading(null)
        val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut()
        auth.signOut()
        _exitMessage.value = Resource.success(null)
    }
}