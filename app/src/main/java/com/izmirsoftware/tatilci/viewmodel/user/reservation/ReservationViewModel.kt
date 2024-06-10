package com.izmirsoftware.tatilci.viewmodel.user.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.ReservationModel
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.toReservation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : ViewModel() {
    private var currentUserId = auth.currentUser?.uid.toString()

    private var _reservationMessage = MutableLiveData<Resource<Boolean>>()
    val reservationMessage: LiveData<Resource<Boolean>>
        get() = _reservationMessage

    private var _reservations = MutableLiveData<List<ReservationModel>>()
    val reservations: LiveData<List<ReservationModel>>
        get() =  _reservations


    init {
        getReservations()
    }

    private fun getReservations() = viewModelScope.launch {
        _reservationMessage.value = Resource.loading(true)
        firebaseRepo.getUserReservations(currentUserId)
            .addOnSuccessListener {
                val reservationList = mutableListOf<ReservationModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toReservation()?.let { reservation -> reservationList.add(reservation) }
                }
                _reservations.value = reservationList
                if (reservationList.isNotEmpty()){
                    _reservationMessage.value = Resource.success( true)
                }else{
                    _reservationMessage.value = Resource.success( false)
                }
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _reservationMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
                println("error : "+exception.localizedMessage)
            }
    }

}