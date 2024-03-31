package com.androiddevelopers.villabuluyorum.service

import com.androiddevelopers.villabuluyorum.model.PushNotification
import com.androiddevelopers.villabuluyorum.util.Util
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {
    @Headers("Authorization: key=${Util.SERVER_KEY}", "Content-Type:${Util.CONTENT_TYPE}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}