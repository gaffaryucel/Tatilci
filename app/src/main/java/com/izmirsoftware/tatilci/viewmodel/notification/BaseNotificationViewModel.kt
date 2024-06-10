package com.izmirsoftware.tatilci.viewmodel.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.UserModel
import com.izmirsoftware.tatilci.model.notification.InAppNotificationModel
import com.izmirsoftware.tatilci.model.notification.NotificationData
import com.izmirsoftware.tatilci.model.notification.PushNotification
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.NotificationTypeForActions
import com.izmirsoftware.tatilci.util.NotificationTypeForActions.*
import com.izmirsoftware.tatilci.util.toUserModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseNotificationViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
) : ViewModel() {
    val currentUserId = auth.currentUser?.uid.toString()
    val currentUserData = MutableLiveData<UserModel>()

    init {
        getCurrentUserData()
    }
    internal fun getCurrentUserData() = viewModelScope.launch {
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    currentUserData.value = user
                }
            }
    }
    internal fun sendNotification(
        notification: InAppNotificationModel,
        type: NotificationTypeForActions,
        chatId: String? = null,
        reservationId: String? = null,
        homeId: String? = null,
    ) = CoroutineScope(Dispatchers.IO).launch {
        val TAG = "Notification"
        try {
            PushNotification(
                NotificationData(
                    type = type,
                    title = notification.title.toString(),
                    message = notification.message.toString(),
                    profileImage = notification.userImage.toString(),
                    imageUrl = notification.imageUrl.toString(),
                    chatId = chatId,
                    reservationId = reservationId,
                    homeId = homeId,
                ),
                notification.userToken.toString()
            ).also {
                repo.postNotification(it)
                if ((type != MESSAGE_RECEIVER_HOST) && (type != MESSAGE_RECEIVER_USER)){
                    repo.saveNotification(notification)
                    changeNotificationReedStatus(notification.userId.toString(),false)
                }else{
                    changeChatReedStatus(notification.userId.toString(),false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
    private fun changeNotificationReedStatus(userId : String, value : Boolean){
        val map = HashMap<String,Any?>()
        map["notificationRead"] = value
        repo.updateUserData(userId,map)
    }
    private fun changeChatReedStatus(userId : String, value : Boolean){
        val map = HashMap<String,Any?>()
        map["chatRead"] = value
        repo.updateUserData(userId,map)
    }
}