package com.androiddevelopers.villabuluyorum.repo

import android.net.Uri
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask

interface FirebaseRepoInterFace {
    // Auth
    fun login(email: String, password: String): Task<AuthResult>
    fun forgotPassword(email: String): Task<Void>
    fun register(email: String, password: String): Task<AuthResult>


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
    fun sendMessageToRealtimeDatabase(
        userId: String,
        chatId: String,
        message: MessageModel
    ): Task<Void>

    fun addMessageInChatMatesRoom(
        chatMateId: String,
        chatId: String,
        message: MessageModel
    ): Task<Void>

    fun getAllMessagesFromRealtimeDatabase(currentUserId: String, chatId: String): DatabaseReference
    fun createChatRoomForOwner(currentUserId: String, chat: ChatModel): Task<Void>
    fun createChatRoomForChatMate(userId: String, chat: ChatModel): Task<Void>
    fun getAllChatRooms(currentUserId: String): DatabaseReference
    fun changeLastMessage(
        userId: String,
        chatId: String,
        message: String,
        time: String
    ): Task<Void>

    fun changeLastMessageInChatMatesRoom(
        chatMateId: String,
        chatId: String,
        message: String,
        time: String
    ): Task<Void>

    fun seeMessage(
        userId: String,
        chatId: String
    ): Task<Void>

    fun changeReceiverSeenStatus(
        receiverId: String,
        chatId: String
    ): Task<Void>


*/
    //Firebase Storage
    fun addVillaImage(
        uri: Uri,
        userId: String,
        villaId: String,
    ): UploadTask

    /*
        fun changeProfilePhoto(
            image: ByteArray,
            uId: String,
        ): UploadTask



    //Notification
        suspend fun postNotification(notification: PushNotification): Response<ResponseBody>
        fun saveNotification(notification: InAppNotificationModel): Task<Void>
        fun getAllNotifications(userId: String, limit: Long): Task<QuerySnapshot>
    */
}