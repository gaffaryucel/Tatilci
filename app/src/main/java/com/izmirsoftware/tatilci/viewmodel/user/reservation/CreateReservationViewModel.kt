package com.izmirsoftware.tatilci.viewmodel.user.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.ApprovalStatus
import com.izmirsoftware.tatilci.model.PaymentMethod
import com.izmirsoftware.tatilci.model.PropertyType
import com.izmirsoftware.tatilci.model.ReservationModel
import com.izmirsoftware.tatilci.model.villa.Villa
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.getCurrentTime
import com.izmirsoftware.tatilci.util.toVilla
import com.izmirsoftware.tatilci.viewmodel.notification.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateReservationViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : BaseNotificationViewModel(firebaseRepo,auth) {

    private var _liveDataFirebaseStatus = MutableLiveData<Resource<Boolean>>()
    val liveDataFirebaseStatus: LiveData<Resource<Boolean>>
        get() = _liveDataFirebaseStatus

    private var _liveDataFirebaseVilla = MutableLiveData<Villa>()
    val liveDataFirebaseVilla: LiveData<Villa>
        get() = _liveDataFirebaseVilla

    private var _liveDataReserveStatus = MutableLiveData<Resource<Boolean>>()
    val liveDataReserveStatus: LiveData<Resource<Boolean>>
        get() = _liveDataReserveStatus


    fun getVillaByIdFromFirestore(villaId: String) = viewModelScope.launch{
        _liveDataFirebaseStatus.value = Resource.loading(true)
        firebaseRepo.getVillaByIdFromFirestore(villaId)
            .addOnSuccessListener { documentSnapshot ->
                _liveDataFirebaseVilla.value = documentSnapshot.toVilla()
                _liveDataFirebaseStatus.value = Resource.success(true)
            }.addOnFailureListener {
                _liveDataFirebaseStatus.value = it.message?.let { message ->
                    Resource.error(message, null)
                }
            }
    }
    fun makeReservation(reservation : ReservationModel) = viewModelScope.launch{
        if (currentUserId != reservation.hostId){
            _liveDataReserveStatus.value = Resource.loading(null)
            firebaseRepo.createReservationForVilla(reservation).addOnSuccessListener {
                _liveDataReserveStatus.value = Resource.success(null)
            }.addOnFailureListener {
                _liveDataReserveStatus.value = Resource.error("Hata :", null)
            }
        }

    }



    fun createReservationInstance(
        reservationId: String,
        villaId: String,
        hostId: String,
        startDate: String,
        endDate: String,
        nights: Int,
        totalPrice: Int,
        guestCount: Int,
        paymentMethod: PaymentMethod,
        nightlyRate: Int,
        propertyType: PropertyType,
        villaImage: String,
        bedroomCount: Int,
        bathCount: Int,
        title: String
    ) : ReservationModel{
        return ReservationModel(
            reservationId = reservationId,
            userId = currentUserId,
            hostId = hostId,
            villaId = villaId,
            startDate = startDate,
            endDate = endDate,
            nights = nights,
            totalPrice = totalPrice,
            guestCount = guestCount,
            paymentMethod = paymentMethod,
            propertyType = propertyType,
            villaImage = villaImage,
            nightlyRate = nightlyRate,
            bedroomCount = bedroomCount,
            bathCount = bathCount,
            title = title,
            approvalStatus = ApprovalStatus.WAITING_FOR_APPROVAL,
            time = getCurrentTime(),
            rated = null
        )
    }
}