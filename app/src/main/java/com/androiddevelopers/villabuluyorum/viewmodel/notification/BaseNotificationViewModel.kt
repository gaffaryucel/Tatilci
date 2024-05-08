package com.androiddevelopers.villabuluyorum.viewmodel.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.model.notification.NotificationData
import com.androiddevelopers.villabuluyorum.model.notification.PushNotification
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.repo.RoomProvinceRepo
import com.androiddevelopers.villabuluyorum.util.NotificationType
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
open class BaseNotificationViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
) : ViewModel() {
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
                if (type != NotificationTypeForActions.MESSAGE){
                    repo.saveNotification(notification)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    internal fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }

}