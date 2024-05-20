package com.androiddevelopers.villabuluyorum.viewmodel.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.model.notification.NotificationData
import com.androiddevelopers.villabuluyorum.model.notification.PushNotification
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions.*
import com.androiddevelopers.villabuluyorum.util.getCurrentDate
import com.androiddevelopers.villabuluyorum.util.toUserModel
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
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}