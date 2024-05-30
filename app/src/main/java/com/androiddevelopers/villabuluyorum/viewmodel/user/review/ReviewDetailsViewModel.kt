package com.androiddevelopers.villabuluyorum.viewmodel.user.review

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.ReservationModel
import com.androiddevelopers.villabuluyorum.model.ReviewModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.NotificationType
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.getCurrentTime
import com.androiddevelopers.villabuluyorum.util.toReservation
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.androiddevelopers.villabuluyorum.viewmodel.notification.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReviewDetailsViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : BaseNotificationViewModel(firebaseRepo,auth){

    private var _reservationMessage = MutableLiveData<Resource<Boolean>>()
    val reservationMessage: LiveData<Resource<Boolean>>
        get() = _reservationMessage

  private var _reviewMessage = MutableLiveData<Resource<Boolean>>()
    val reviewMessage: LiveData<Resource<Boolean>>
        get() = _reviewMessage

    private var _loadMessage = MutableLiveData<Resource<Boolean>>()
    val loadMessage: LiveData<Resource<Boolean>>
        get() = _loadMessage

    private var _reservation = MutableLiveData<ReservationModel>()
    val reservation : LiveData<ReservationModel>
        get() =  _reservation

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _liveDataFirebaseVilla = MutableLiveData<Villa>()
    val liveDataFirebaseVilla: LiveData<Villa>
        get() = _liveDataFirebaseVilla

    fun getReservationById(reservationId : String) = viewModelScope.launch {
        _reservationMessage.value = Resource.loading(true)
        firebaseRepo.getReservationById(reservationId)
            .addOnSuccessListener {
                it.toReservation()?.let { reservation ->
                    _reservation.value = reservation
                    getUserDataById(reservation.hostId.toString())
                    getVillaById(reservation.villaId.toString())
                }
                _reservationMessage.value = Resource.success( true)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _reservationMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    private fun getUserDataById(userId : String) = viewModelScope.launch {
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
            }
    }

    private fun getVillaById(villaId: String) {
        firebaseRepo.getVillaByIdFromFirestore(villaId)
            .addOnSuccessListener { documentSnapshot ->
                _liveDataFirebaseVilla.value = documentSnapshot.toVilla()
            }
    }

    fun createReview(comment : String,rating : Int){
        if (currentUserId != reservation.value?.hostId){
            val review = makeReviewMode(comment,rating)
            _reviewMessage.value = Resource.loading(null)
            firebaseRepo.createReview(review).addOnSuccessListener {
                _reviewMessage.value = Resource.success(null)
                val map = HashMap<String,Any?>()
                map["rated"] = true
                firebaseRepo.changeReservationRateStatus(reservation.value?.reservationId.toString(),map).addOnSuccessListener {
                    sendReviewNotification()
                }
            }.addOnFailureListener {
                _reviewMessage.value = Resource.error("Hata :", null)
            }
        }
    }
    private fun makeReviewMode(
        comment: String? = null,
        rating: Int? = null
    ) : ReviewModel{
        return ReviewModel(
            reviewId = UUID.randomUUID().toString(),
            rating = rating,
            comment = comment,
            userId = currentUserId,
            userName = currentUserData.value?.firstName+" "+currentUserData.value?.lastName,
            userProfilePictureUrl = currentUserData.value?.profileImageUrl,
            reservationId = reservation.value?.reservationId,
            hostId = reservation.value?.hostId,
            time = getCurrentTime()
        )
    }
    private fun sendReviewNotification(){
        val userName= currentUserData.value?.firstName+" "+currentUserData.value?.lastName
        val message = "$userName, ${reservation.value?.startDate} - ${reservation.value?.endDate}  tarihleri arasındaki konaklaması için bir değerlendirme bıraktı"
        InAppNotificationModel(
            itemId = reservation.value?.villaId,
            userId = userData.value?.userId,
            notificationType = NotificationType.COMMENT,
            notificationId = UUID.randomUUID().toString(),
            userName =  userName,
            title =  "Yeni Bir Değerlendirme",
            message = message,
            userImage = currentUserData.value?.profileImageUrl,
            imageUrl = reservation.value?.villaImage,
            userToken = userData.value?.token,
            time = getCurrentTime()
        ).also { notification->
            sendNotification(
                notification = notification,
                homeId = reservation.value?.villaId,
                type = NotificationTypeForActions.COMMENT,
            )
        }
    }
}