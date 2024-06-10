package com.izmirsoftware.tatilci.viewmodel.user.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.ApprovalStatus
import com.izmirsoftware.tatilci.model.ReservationModel
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.getCurrentDate
import com.izmirsoftware.tatilci.util.toReservation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : ViewModel() {
    private var currentUserId = auth.currentUser?.uid.toString()

    private var _reviewMessage = MutableLiveData<Resource<Boolean>>()
    val reviewMessage: LiveData<Resource<Boolean>>
        get() = _reviewMessage

    private var _reservations = MutableLiveData<List<ReservationModel>>()
    val reservations: LiveData<List<ReservationModel>>
        get() =  _reservations


    init {
        getNotRatedFinishedReservations()
    }

    private fun getNotRatedFinishedReservations() = viewModelScope.launch {
        _reviewMessage.value = Resource.loading(true)
        firebaseRepo.getAllFinishedReservations(currentUserId, getCurrentDate())
            .addOnSuccessListener {
                val reviewList = mutableListOf<ReservationModel>()
                for (document in it.documents) {
                    // Belgeden her bir yorumu çek
                    document.toReservation()?.let { reservation ->
                        if (reservation.approvalStatus == ApprovalStatus.APPROVED){
                            reviewList.add(reservation)
                        }
                    }
                }
                _reservations.value = reviewList
                if (reviewList.isNotEmpty()){
                    _reviewMessage.value = Resource.success( true)
                }else{
                    _reviewMessage.value = Resource.success( false)
                }
            }.addOnFailureListener { exception ->
                // Hata durumunda işlemleri buraya ekleyebilirsiniz
                _reviewMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
                println("error : "+exception.localizedMessage)
            }
    }
}

