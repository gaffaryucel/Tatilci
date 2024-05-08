package com.androiddevelopers.villabuluyorum.viewmodel.user.villa

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.model.notification.NotificationData
import com.androiddevelopers.villabuluyorum.model.notification.PushNotification
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.repo.RoomProvinceRepo
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.androiddevelopers.villabuluyorum.viewmodel.notification.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject
constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
    private val roomProvinceRepo: RoomProvinceRepo
) : BaseNotificationViewModel(repo,auth) {

    private var _bestVillas = MutableLiveData<List<Villa>>()
    val bestVillas: LiveData<List<Villa>>
        get() = _bestVillas

    private var _closeVillas = MutableLiveData<List<Villa>>()
    val closeVillas: LiveData<List<Villa>>
        get() = _closeVillas

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _liveDataProvinceFromRoom = MutableLiveData<List<Province>>()
    val liveDataProvinceFromRoom: LiveData<List<Province>>
        get() = _liveDataProvinceFromRoom

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _notifyUser = MutableLiveData<String>()
    val notifyUser: LiveData<String>
        get() = _notifyUser

    private val currentUserId = auth.currentUser?.uid.toString()

    init {
        getUserDataFromFirebase()
        getCloseVillas("İzmir", 20)
        getBestVillas(20)
        getAllProvince()
    }

    fun getCloseVillas(city: String, limit: Long) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(null)
        repo.getVillasByCity(city, limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa -> villaList.add(villa) }
                }
                _closeVillas.value = villaList
                _firebaseMessage.value = Resource.success(null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    private fun getBestVillas(limit: Long) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(null)
        repo.getAllVillasFromFirestore(limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa -> villaList.add(villa) }
                }
                _bestVillas.value = villaList
                _firebaseMessage.value = Resource.success(null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    private fun getAllProvince() = viewModelScope.launch {
        roomProvinceRepo.getAllProvince().flowOn(Dispatchers.IO).collect {
            _liveDataProvinceFromRoom.value = it
        }
    }

    private fun getUserDataFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getUserDataByDocumentId(currentUserId)
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        documentSnapshot.toUserModel()?.let { userModel ->
                            _userData.value = userModel
                        }
                    }
                }
        }
    }

    fun updateUserLocation(
        latitude: Double?,
        longitude: Double?,
    ) = viewModelScope.launch {
        if (latitude == null || longitude == null) {
            return@launch
        }
        val updateMap: HashMap<String, Any?> = HashMap()
        updateMap["latitude"] = latitude
        updateMap["longitude"] = longitude
        repo.updateUserData(currentUserId, updateMap)
            .addOnSuccessListener {
                _notifyUser.value = "Konum Güncellendi"
            }.addOnFailureListener {
                _notifyUser.value = "Konum Güncellenirken bir hata oluştu"
            }
    }

    fun resetNotifyMessage() {
        _notifyUser.value = ""
    }

}