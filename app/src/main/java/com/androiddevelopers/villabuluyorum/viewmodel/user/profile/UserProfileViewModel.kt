package com.androiddevelopers.villabuluyorum.viewmodel.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
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
    private var _userVillas = MutableLiveData<List<Villa>>()
    val userVillas: LiveData<List<Villa>>
        get() = _userVillas

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData


    fun getUserVillas(userId: String, limit: Long) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(null)
        repo.getVillasByUserId(userId, limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa -> villaList.add(villa) }
                }
                _userVillas.value = villaList
                _firebaseMessage.value = Resource.success(null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
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