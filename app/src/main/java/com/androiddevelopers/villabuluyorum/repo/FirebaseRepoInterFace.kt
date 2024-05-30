package com.androiddevelopers.villabuluyorum.repo

import android.net.Uri
import com.androiddevelopers.villabuluyorum.model.ReservationModel
import com.androiddevelopers.villabuluyorum.model.ReviewModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.chat.ChatModel
import com.androiddevelopers.villabuluyorum.model.chat.MessageModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.model.notification.PushNotification
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.NotificationType
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import okhttp3.ResponseBody
import retrofit2.Response

interface FirebaseRepoInterFace {
    // Auth
    fun login(email: String, password: String): Task<AuthResult>
    fun forgotPassword(email: String): Task<Void>
    fun register(email: String, password: String): Task<AuthResult>

// Firestore

    // Firestore - User
    fun addUserToFirestore(data: UserModel): Task<Void>
    fun deleteUserFromFirestore(documentId: String): Task<Void>
    fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot>
    fun getUsersFromFirestore(): Task<QuerySnapshot>
    fun updateUserData(userId: String, updateData: HashMap<String, Any?>): Task<Void>

    //Firestore - Villa
    fun addVillaToFirestore(villaId: String, villa: Villa): Task<Void>
    fun deleteVillaFromFirestore(villaId: String): Task<Void>
    fun getAllVillasFromFirestore(limit: Long): Task<QuerySnapshot>
    fun getVillaByIdFromFirestore(villaId: String): Task<DocumentSnapshot>
    fun getVillasByStarRatingFromFirestore(limit: Long): Task<QuerySnapshot>
    fun getVillasByCity(city: String, limit: Long): Task<QuerySnapshot>
    fun getVillasByUserId(id: String, limit: Long): Task<QuerySnapshot>
    fun getVillasByUserId(id: String): Task<QuerySnapshot>

    // Firestore - Reservation
    fun createReservationForVilla(data: ReservationModel): Task<Void>
    fun getUserReservations(userId: String): Task<QuerySnapshot>
    fun getReservationsForHost(userId: String): Task<QuerySnapshot>
    fun getReservationById(reservationId: String): Task<DocumentSnapshot>
    fun changeReservationStatus(
        reservationId: String,
        status: java.util.HashMap<String, Any?>
    ): Task<Void>

    fun changeReservationRateStatus(
        reservationId: String,
        status: java.util.HashMap<String, Any?>
    ): Task<Void>

    //Firestore - Review
    fun createReview(review: ReviewModel): Task<Void>
    fun getReservationsByRateStatus(
        userId: String,
        today: String,
        value: Boolean?
    ): Task<QuerySnapshot>

    fun getAllFinishedReservations(userId: String, today: String): Task<QuerySnapshot>
    fun getAllReviewsByUserId(userId: String): Task<QuerySnapshot>
    fun getAllReviewsByVillaId(villaId: String): Task<QuerySnapshot>


    //Firestore - Notification
    //Set
    suspend fun postNotification(notification: PushNotification): Response<ResponseBody>
    fun saveNotification(notification: InAppNotificationModel): Task<Void>

    //Get
    fun getNotificationsByType(
        userId: String,
        type: NotificationType,
        limit: Long
    ): Task<QuerySnapshot>

    fun getAllNotifications(userId: String, limit: Long): Task<QuerySnapshot>


    //Firebase Storage
    fun addVillaImage(
        uri: Uri,
        userId: String,
        villaId: String,
    ): UploadTask

    fun uploadUserProfilePhoto(
        uri: Uri,
        userId: String,
        key: String,
    ): UploadTask

    //Realtime Database - Chat
    fun createChatRoomForOwner(currentUserId: String, chat: ChatModel): Task<Void>
    fun createChatRoomForChatMate(userId: String, chat: ChatModel): Task<Void>
    fun getAllChatRooms(currentUserId: String): DatabaseReference

    fun getChatRoomData(currentUserId: String, receiverId: String): DatabaseReference


    //Message
    fun sendMessageToRealtimeDatabase(
        userId: String, chatId: String, message: MessageModel
    ): Task<Void>

    fun addMessageInChatMatesRoom(
        chatMateId: String, chatId: String, message: MessageModel
    ): Task<Void>

    fun getAllMessagesFromRealtimeDatabase(
        currentUserId: String, chatId: String
    ): DatabaseReference

    fun changeLastMessage(
        userId: String, chatId: String, message: String, time: String
    ): Task<Void>

    fun changeLastMessageInChatMatesRoom(
        chatMateId: String, chatId: String, message: String, time: String
    ): Task<Void>

    fun seeMessage(
        userId: String, chatId: String
    ): Task<Void>

    fun changeReceiverSeenStatus(
        receiverId: String, chatId: String
    ): Task<Void>

    fun changeOnlineStatus(userId: String, onlineData: Boolean): Task<Void>

    /*
    fun updateViewCountOfVilla(
        postId: String,
        newCount: List<String>
    ): Task<Void>

    fun updateLikeCountOfVilla(
        postId: String,
        likes: List<String>
    ): Task<Void>

    fun updateSavedUsersOfVilla(
        postId: String,
        savedUsers: List<String>
    ): Task<Void>


//Realtime Database - Chat


    fun getAllMessagesFromRealtimeDatabase(currentUserId: String, chatId: String): DatabaseReference
    fun createChatRoomForOwner(currentUserId: String, chat: ChatModel): Task<Void>
    fun createChatRoomForChatMate(userId: String, chat: ChatModel): Task<Void>
    fun getAllChatRooms(currentUserId: String): DatabaseReference



*/

    /*
      //Notification
          suspend fun postNotification(notification: PushNotification): Response<ResponseBody>
          fun saveNotification(notification: InAppNotificationModel): Task<Void>
          fun getAllNotifications(userId: String, limit: Long): Task<QuerySnapshot>
      */
}