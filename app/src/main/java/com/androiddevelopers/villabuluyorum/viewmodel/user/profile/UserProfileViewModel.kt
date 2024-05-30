package com.androiddevelopers.villabuluyorum.viewmodel.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.ReviewModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toReview
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject
constructor(
    private val repo: FirebaseRepoInterFace,
) : ViewModel() {
    //Villa
    private var _userVillas = MutableLiveData<List<Villa>>()
    val userVillas: LiveData<List<Villa>>
        get() = _userVillas

    private var _villaMessage =  MutableLiveData<Resource<Boolean>>()
    val villaMessage: LiveData<Resource<Boolean>>
        get() = _villaMessage

    //Review
    private var _userReviews = MutableLiveData<List<ReviewModel>>()
    val userReviews: LiveData<List<ReviewModel>>
        get() = _userReviews

    private var _reviewMessage =  MutableLiveData<Resource<Boolean>>()
    val reviewMessage: LiveData<Resource<Boolean>>
        get() = _reviewMessage

    //User
    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData


    fun getUserVillas(userId: String, limit: Long) = viewModelScope.launch {
        _villaMessage.value = Resource.loading(null)
        repo.getVillasByUserId(userId)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa -> villaList.add(villa) }
                }
                _userVillas.value = villaList
                if (villaList.isEmpty()){
                    _villaMessage.value = Resource.success(false)
                }else{
                    _villaMessage.value = Resource.success(true)
                }
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _villaMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    fun getUserReviews(userId: String, limit: Long) = viewModelScope.launch {
        _reviewMessage.value = Resource.loading(null)
        repo.getAllReviewsByUserId(userId)
            .addOnSuccessListener {
                val reviewList = mutableListOf<ReviewModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toReview()?.let { review ->
                        reviewList.add(review)
                    }
                }
                _userReviews.value = reviewList
                if (reviewList.isEmpty()){
                    _reviewMessage.value = Resource.success(false)
                }else{
                    _reviewMessage.value = Resource.success(true)
                }
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _reviewMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun getUserData(userId: String) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(null)
        repo.getUserDataByDocumentId(userId)
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
}