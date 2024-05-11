package com.androiddevelopers.villabuluyorum.repo

import android.net.Uri
import com.androiddevelopers.villabuluyorum.model.ReservationModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.chat.ChatModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.model.notification.PushNotification
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.service.NotificationAPI
import com.androiddevelopers.villabuluyorum.util.NotificationType
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    database: FirebaseDatabase,
    storage: FirebaseStorage,
    private val notificationAPI: NotificationAPI
) : FirebaseRepoInterFace {

    //Firestore
    private val userCollection = firestore.collection("users")
    private val notificationCollection = firestore.collection("notifications")
    private val homeCollection = firestore.collection("homes")
    private val villaCollection = firestore.collection("villas")
    private val reservationCollection = firestore.collection("reservations")

    //StorageRef
    private val imagesParentRef = storage.reference.child("users")

    //RealtimeRef
    private val messagesReference = database.getReference("messages")

    //Auth
    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun forgotPassword(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }

    override fun register(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }


    // Firestore - User
    override fun addUserToFirestore(data: UserModel): Task<Void> {
        return userCollection.document(data.userId.toString()).set(data)
    }

    override fun deleteUserFromFirestore(documentId: String): Task<Void> {
        return userCollection.document(documentId).delete()
    }

    override fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot> {
        return userCollection.document(documentId).get()
    }

    override fun getUsersFromFirestore(): Task<QuerySnapshot> {
        return userCollection.get()
    }

    override fun updateUserData(userId: String, updateData: HashMap<String, Any?>): Task<Void> {
        return userCollection.document(userId).update(updateData)
    }

    // Firestore - Vila
    override fun addVillaToFirestore(villaId: String, villa: Villa): Task<Void> {
        return villaCollection.document(villaId).set(villa)
    }

    override fun deleteVillaFromFirestore(villaId: String): Task<Void> {
        return villaCollection.document(villaId).delete()
    }

    override fun getAllVillasFromFirestore(limit: Long): Task<QuerySnapshot> {
        return villaCollection.limit(limit).get()
    }

    override fun getVillaByIdFromFirestore(villaId: String): Task<DocumentSnapshot> {
        return villaCollection.document(villaId).get()
    }

    override fun getVillasByStarRatingFromFirestore(limit: Long): Task<QuerySnapshot> {
        return villaCollection.orderBy("star", Query.Direction.DESCENDING).limit(limit).get()
    }

    override fun getVillasByCity(city: String, limit: Long): Task<QuerySnapshot> {
        return villaCollection.whereEqualTo("locationProvince", city).limit(limit).get()
    }
    override fun getVillasByUserId(id: String, limit: Long): Task<QuerySnapshot> {
        return villaCollection.whereEqualTo("hostId", id).limit(limit).get()
    }

    // Firestore - Reservation
    override fun createReservationForVilla(data: ReservationModel): Task<Void> {
        return reservationCollection.document(data.reservationId.toString()).set(data)
    }
    override fun getUserReservations(userId: String): Task<QuerySnapshot> {
        return reservationCollection.whereEqualTo("userId", userId).get()
    }
    override fun getReservationsForHost(userId: String): Task<QuerySnapshot> {
        return reservationCollection.whereEqualTo("hostId", userId).get()
    }
    override fun getReservationById(reservationId: String):Task<DocumentSnapshot>{
        return reservationCollection.document(reservationId).get()
    }
    override fun changeReservationStatus(reservationId: String, status: HashMap<String, Any?>): Task<Void> {
        return reservationCollection.document(reservationId).update(status)
    }

    // Storage - Villa
    override fun addVillaImage(
        uri: Uri,
        userId: String,
        villaId: String,
    ): UploadTask {
        return imagesParentRef
            .child("userId_$userId")
            .child("images")
            .child("employerPost")
            .child("postId_$villaId")
            .child("${UUID.randomUUID()}.jpg")
            .putFile(uri)

    }
    // Storage - User
    override fun uploadUserProfilePhoto(uri: Uri, userId: String,key : String): UploadTask {
        return imagesParentRef
            .child("userId_$userId")
            .child("images")
            .child(key)
            .child("${UUID.randomUUID()}.jpg")
            .putFile(uri)
    }


    //Notification
//Set
    override suspend fun postNotification(notification: PushNotification): Response<ResponseBody> {
        return notificationAPI.postNotification(notification)
    }

    override fun saveNotification(notification: InAppNotificationModel): Task<Void> {
        return notificationCollection.document(notification.notificationId.toString())
            .set(notification)
    }

    //Get
    override fun getNotificationsByType(
        userId: String,
        type: NotificationType,
        limit: Long
    ): Task<QuerySnapshot> {
        return notificationCollection.whereEqualTo("userId", userId)
            .whereEqualTo("notificationType", type)
            .orderBy("time", Query.Direction.ASCENDING) // createdAt alanına göre azalan sıralama
            .limit(limit)
            .get()
    }
    override fun getAllNotifications(userId: String, limit: Long): Task<QuerySnapshot> {
        return notificationCollection.whereEqualTo("userId", userId)
            .limit(limit)
            .get()
    }


//Realtime Database - Chat
    override fun createChatRoomForOwner(currentUserId: String, chat: ChatModel): Task<Void> {
        return messagesReference.child(currentUserId).child(chat.receiverId.toString()).setValue(chat)
    }
    override fun createChatRoomForChatMate(userId: String, chat: ChatModel): Task<Void> {
        return messagesReference.child(userId).child(chat.receiverId.toString()).setValue(chat)
    }

    override fun getAllChatRooms(currentUserId: String): DatabaseReference {
        return messagesReference.child(currentUserId)
    }

    override fun getChatRoomData(currentUserId: String,receiverId: String): DatabaseReference {
        return messagesReference.child(currentUserId).child(receiverId)
    }

    override fun changeOnlineStatus(userId: String, onlineData: Boolean): Task<Void> {
        val map = hashMapOf<String, Any?>(
            "isOnline" to onlineData,
        )
        return userCollection.document(userId).update(map)
    }
}