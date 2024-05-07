package com.androiddevelopers.villabuluyorum.viewmodel.host.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.villabuluyorum.model.ApprovalStatus
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReservationApprovalViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
) : ViewModel() {
    private var _reservationMessage = MutableLiveData<Resource<String>>()
    val reservationMessage: LiveData<Resource<String>>
        get() = _reservationMessage

    fun changeReservationStatus(id : String, status : ApprovalStatus){
        _reservationMessage.value = Resource.loading()
        val statusMap = HashMap<String,Any?>()
        statusMap["approvalStatus"] = status
        repo.changeReservationStatus(id,statusMap).addOnSuccessListener {
            _reservationMessage.value = when(status){
                ApprovalStatus.WAITING_FOR_APPROVAL -> Resource.success("")
                ApprovalStatus.APPROVED -> Resource.success("Rezervasyon onaylandı")
                ApprovalStatus.NOT_APPROVED -> Resource.success("Rezervasyon iptal edildi")
            }
        }.addOnFailureListener {
            _reservationMessage.value = Resource.error("hata",null)
        }
    }
}