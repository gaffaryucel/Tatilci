package com.izmirsoftware.tatilci.viewmodel.user.villa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.ApprovalStatus
import com.izmirsoftware.tatilci.model.ReservationModel
import com.izmirsoftware.tatilci.model.provinces.Province
import com.izmirsoftware.tatilci.model.villa.Villa
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.repo.RoomProvinceRepo
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.getCurrentDate
import com.izmirsoftware.tatilci.util.toReservation
import com.izmirsoftware.tatilci.util.toVilla
import com.izmirsoftware.tatilci.viewmodel.notification.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject
constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
    private val roomProvinceRepo: RoomProvinceRepo
) : BaseNotificationViewModel(repo,auth) {

    var rateReservations = MutableLiveData<Boolean>()

    private var allNotRatedReservations = MutableLiveData<List<ReservationModel>>()

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

    private var _notifyUser =  MutableLiveData<Resource<Boolean>>()
    val notifyUser: LiveData<Resource<Boolean>>
        get() = _notifyUser


    init {
        getCloseVillas("İzmir", 20)
        getBestVillas(20)
        getAllProvince()
        getNotRatedFinishedReservations()
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

    private fun getNotRatedFinishedReservations() = viewModelScope.launch {
        repo.getReservationsByRateStatus(currentUserId, getCurrentDate(),null)
            .addOnSuccessListener {
                val set = mutableSetOf<ReservationModel>()
                for (document in it.documents) {
                    document.toReservation()?.let { reservation ->
                        if (reservation.approvalStatus == ApprovalStatus.APPROVED){
                            set.add(reservation)
                        }
                    }
                }
                rateReservations.value = set.size > 0
                allNotRatedReservations.value = set.toList()
            }.addOnFailureListener{
                println("error : "+it.localizedMessage)
            }
    }
    fun notReviewReservations() = viewModelScope.launch{
        allNotRatedReservations.value?.forEach {
            val map = HashMap<String,Any?>()
            map["rated"] = false
            if (it.reservationId != null){
                repo.changeReservationRateStatus(it.reservationId,map)
            }
        }
    }


    fun resetNotifyMessage() {
        _notifyUser.value = Resource.loading()
    }


    fun updateUserLocation(
        latitude: Double?,
        longitude: Double?,
    ) = viewModelScope.launch {
        println("updateUserLocation")
        if (latitude == null || longitude == null) {
            return@launch
        }
        val updateMap: HashMap<String, Any?> = HashMap()
        updateMap["latitude"] = latitude
        updateMap["longitude"] = longitude
        repo.updateUserData(currentUserId, updateMap)
            .addOnSuccessListener {
                println("s")
                _notifyUser.value = Resource.success()
            }.addOnFailureListener {
                println("ee")

                _notifyUser.value = it.localizedMessage?.let { it1 -> Resource.error(it1,null) }
            }
    }

}
