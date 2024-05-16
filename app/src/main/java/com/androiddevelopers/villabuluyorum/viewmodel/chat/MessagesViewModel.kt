package com.androiddevelopers.villabuluyorum.viewmodel.chat

import androidx.lifecycle.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.chat.ChatModel
import com.androiddevelopers.villabuluyorum.model.chat.MessageModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.getCurrentTime
import com.androiddevelopers.villabuluyorum.util.sortListByDate
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.viewmodel.notification.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    auth: FirebaseAuth
) : BaseNotificationViewModel(repo,auth) {

    val currentUserId = auth.currentUser?.uid.toString()
    private val currentUserData = MutableLiveData<UserModel>()


    private var _messages = MutableLiveData<List<MessageModel>>()
    val messages: LiveData<List<MessageModel>>
        get() = _messages

    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus: LiveData<Resource<Boolean>>
        get() = _messageStatus


    private var _receiverData = MutableLiveData<UserModel>()
    val receiverData : LiveData<UserModel>
        get() = _receiverData

    init {
        getCurrentUserData()
    }

    fun sendMessage(
        messageData: String,
        messageReceiver: String,
    ) {
        val time = getCurrentTime()

        val usersMessage = createMessageModelForCurrentUser(
            messageData ,
            currentUserId ?: "" ,
            messageReceiver,
            time
        )

        _messageStatus.value = Resource.loading(null)
        repo.sendMessageToRealtimeDatabase(currentUserId, messageReceiver, usersMessage)
            .addOnSuccessListener {
                _messageStatus.value = Resource.success(null)
                changeLastMessage(messageData,time,messageReceiver)
                repo.changeReceiverSeenStatus(messageReceiver,currentUserId)
            }
            .addOnFailureListener { error ->
                _messageStatus.value = error.localizedMessage?.let { Resource.error(it, null) }
            }
        repo.addMessageInChatMatesRoom(messageReceiver, currentUserId, usersMessage)
    }

    private fun changeLastMessage(messageData: String, time: String,messageReceiver : String) = viewModelScope.launch{
        repo.changeLastMessage(currentUserId,messageReceiver,messageData,time)
        repo.changeLastMessageInChatMatesRoom(messageReceiver,currentUserId,messageData,time)
    }

    private fun createMessageModelForCurrentUser(
        messageData: String,
        messageSender: String,
        messageReceiver: String,
        time : String
    ) : MessageModel {
        val messageId = UUID.randomUUID().toString()
        return MessageModel(
            messageId,
            messageData,
            messageSender,
            messageReceiver,
            time
        )
    }

    fun getMessages(chatId: String) {
        _messageStatus.value = Resource.loading(null)
        repo.getAllMessagesFromRealtimeDatabase(currentUserId, chatId).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<MessageModel>()

                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(MessageModel::class.java)
                        message?.let {
                            messageList.add(it)
                        }
                    }
                    val sortedList = sortListByDate(messageList)
                    _messages.value = sortedList
                    repo.seeMessage(currentUserId,chatId)
                }

                override fun onCancelled(error: DatabaseError) {
                    _messageStatus.value = Resource.error(error.message, null)
                }
            }
        )
    }


    fun getUserData(userId : String) = viewModelScope.launch {
        repo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    _receiverData.value = user
                }
            }
    }

    private fun getCurrentUserData() = viewModelScope.launch {
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    currentUserData.value = user
                }
            }
    }

    fun sendNotificationToReceiver(title : String,message : String, notificationType : NotificationTypeForActions){
        if (_receiverData.value?.userId.toString().isEmpty() || currentUserData.value?.userId == null || currentUserData.value?.username == null || currentUserData.value?.profileImageUrl == null){
            return
        }
        InAppNotificationModel(
            userId = currentUserData.value?.userId,
            notificationId = UUID.randomUUID().toString(),
            title =  title,
            message = "${currentUserData.value?.firstName+" "+currentUserData.value?.lastName}: $message!",
            userImage = currentUserData.value?.profileImageUrl.toString(),
            imageUrl = "",
            userToken = receiverData.value?.token.toString(),
            time = getCurrentTime()
        ).also { notification->
            sendNotification(
                notification = notification,
                type = notificationType,
                chatId = currentUserData.value?.userId,
            )
        }
    }
    fun setUserOnline() {
        if (currentUserId.isNotEmpty()){
            repo.changeOnlineStatus(currentUserId,true)
        }
    }
    fun setUserOffline(){
        if (currentUserId.isNotEmpty()){
            repo.changeOnlineStatus(currentUserId,false)
        }
    }
}