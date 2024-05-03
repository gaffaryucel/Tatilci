package com.androiddevelopers.villabuluyorum.viewmodel.user.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.ApprovalStatus
import com.androiddevelopers.villabuluyorum.model.PaymentMethod
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.ReservationModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateReservationViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : ViewModel() {
    private var currentUserId =auth.currentUser?.uid

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
        // TODO: yapılan rezearvasyonunun bilgisini ev sahibine bildirimle ilet
        _liveDataReserveStatus.value = Resource.loading(null)
        firebaseRepo.createReservationForVilla(reservation).addOnSuccessListener {
            _liveDataReserveStatus.value = Resource.success(null)
        }.addOnFailureListener {
            _liveDataReserveStatus.value = Resource.error("Hata :", null)
        }
    }
    fun createReservationInstance(
        villaId: String,
        hostId: String,
        startDate: String,
        endDate: String,
        nights: Int,
        totalPrice: Int,
        paymentMethod: PaymentMethod,
        nightlyRate: Int,
        propertyType: PropertyType,
        villaImage: String,
        bedroomCount: Int,
        bathCount: Int,
        title: String
    ) : ReservationModel{
        return ReservationModel(
            reservationId = UUID.randomUUID().toString(),
            userId = currentUserId,
            hostId = hostId,
            villaId = villaId,
            startDate = startDate,
            endDate = endDate,
            nights = nights,
            totalPrice = totalPrice,
            paymentMethod = paymentMethod,
            propertyType = propertyType,
            villaImage = villaImage,
            nightlyRate = nightlyRate,
            bedroomCount = bedroomCount,
            bathCount = bathCount,
            title = title,
            approvalStatus = ApprovalStatus.WAITING_FOR_APPROVAL
        )
    }
}