package com.androiddevelopers.villabuluyorum.service

import com.androiddevelopers.villabuluyorum.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.view.host.HostBottomNavigationActivity
import com.androiddevelopers.villabuluyorum.view.user.BottomNavigationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        saveToken(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

// TODO: Moda göre değişen sharedPref değerine bakuılarak gerrekli şekilde gerekli aktivite içindeki chat sayfasına atılacak

        val sharedPref = applicationContext.getSharedPreferences("notification", Context.MODE_PRIVATE)
        var intent = Intent(this, BottomNavigationActivity::class.java)

        val type = message.data["type"] ?: ""
        sharedPref.edit().putString("not_type", type).apply()
        when(type){
            NotificationTypeForActions.MESSAGE_RECEIVER_HOST.toString()->{
                intent = Intent(this, HostBottomNavigationActivity::class.java)
                val chatId = message.data["chatId"] ?: ""
                intent.putExtra("chatId",chatId)
            }
            NotificationTypeForActions.MESSAGE_RECEIVER_USER.toString()->{
                val chatId = message.data["chatId"] ?: ""
                intent.putExtra("chatId",chatId)
            }
            NotificationTypeForActions.RESERVATION_STATUS_CHANGE.toString()->{
                val reservationObject = message.data["reservationObject"] ?: ""
                intent.putExtra("reservation",reservationObject)
            }
            NotificationTypeForActions.HOST_RESERVATION.toString()->{
                intent = Intent(this, HostBottomNavigationActivity::class.java)
                val reservationId = message.data["reservationId"] ?: ""
                intent.putExtra("reservation_host",reservationId)
                //Bu kımın altında kalan when'ler farklı bir intent kullanmalı
            }
            NotificationTypeForActions.COMMENT.toString()->{
                intent = Intent(this, HostBottomNavigationActivity::class.java)
                val homeId = message.data["homeId"] ?: ""
                intent.putExtra("home_id",homeId)
            }
            NotificationTypeForActions.RATING.toString()->{
                intent = Intent(this, HostBottomNavigationActivity::class.java)
                val homeId = message.data["homeId"] ?: ""
                intent.putExtra("home_id",homeId)
            }
        }



        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // Görselin URL'sini al
        val imageUrl = message.data["imageUrl"]
        val profileImageUrl = message.data["profileImage"]

        // Görseli indir ve bildirimde göstermek için büyük bir stil oluştur
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(getBitmapFromUrl(imageUrl)) // Görseli URL'den al ve bitmap olarak indir

        val random = Random.nextInt(1000)

        val pendingIntent = PendingIntent.getActivity(
            this, random, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.app_logo)
            .setAutoCancel(true)
            .setLargeIcon(getBitmapFromUrl(profileImageUrl))
            .setContentIntent(pendingIntent)
            .setStyle(bigPictureStyle) // Büyük resim stili
            .build()


        // Bildirim geldiğinde
        notificationManager.notify(notificationID, notification)
    }

    // URL'den bitmap olarak görsel indiren bir fonksiyon
    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(
            CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveToken(myToken : String){
        val userCollection = FirebaseFirestore.getInstance().collection("users")
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        GlobalScope.launch(Dispatchers.IO) {
            val tokenMap = hashMapOf<String,Any?>(
                "token" to myToken
            )
            userCollection.document(userId).update(tokenMap)
        }

    }
}
