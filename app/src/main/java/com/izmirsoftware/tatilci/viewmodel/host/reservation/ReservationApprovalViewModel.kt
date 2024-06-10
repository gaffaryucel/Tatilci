package com.izmirsoftware.tatilci.viewmodel.host.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.izmirsoftware.tatilci.model.ApprovalStatus
import com.izmirsoftware.tatilci.model.notification.InAppNotificationModel
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.NotificationType
import com.izmirsoftware.tatilci.util.NotificationTypeForActions
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.getCurrentTime
import com.izmirsoftware.tatilci.viewmodel.notification.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReservationApprovalViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
) : BaseNotificationViewModel(repo,auth) {


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


    fun reservationStatusNotifier(reservationId : String,title : String,status : String,villaImage : String,userId : String,token : String){
        val userName = currentUserData.value?.firstName + " " + currentUserData.value?.lastName
        InAppNotificationModel(
            itemId = reservationId,
            userId = userId,
            notificationType = NotificationType.RESERVATION_STATUS_CHANGE,
            notificationId = UUID.randomUUID().toString(),
            userName =  userName,
            title =  title,
            message = "$userName $status",
            userImage = currentUserData.value?.profileImageUrl,
            imageUrl = villaImage,
            userToken = token,
            time = getCurrentTime()
        ).also { notification->
            sendNotification(
                notification = notification,
                reservationId = reservationId,
                type = NotificationTypeForActions.RESERVATION_STATUS_CHANGE,
            )
        }
    }
}