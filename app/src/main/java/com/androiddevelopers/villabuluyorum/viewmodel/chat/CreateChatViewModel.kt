package com.androiddevelopers.villabuluyorum.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.chat.ChatModel
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CreateChatViewModel  @Inject constructor(
    private val repo : FirebaseRepoInterFace,
    private val auth : FirebaseAuth
): ViewModel() {
    val currentUserId = auth.currentUser?.uid.toString()

    var _dataStatus = MutableLiveData<Resource<Boolean>>()
    val dataStatus : LiveData<Resource<Boolean>>
        get() = _dataStatus

    private var currentUserData = MutableLiveData<UserModel>()

    var _isChatRoomExists = MutableLiveData<Boolean>()

    init {
        getCurrentUserData()
    }
    private fun getCurrentUserData() = viewModelScope.launch {
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    currentUserData.value = user
                }
            }
    }
    fun createChatRoom(user: UserModel) = viewModelScope.launch{
        _dataStatus.value = Resource.loading(null)
        if (!currentUserData.value?.userId.isNullOrEmpty() && currentUserId != user.userId) {
            try {
                val newChat = createChatForChatMate()
                val chat = ChatModel(
                    user.userId,
                    user.username,
                    user.profileImageUrl,
                    "",
                    ""
                )
                repo.createChatRoomForOwner(currentUserId,chat)
                    .addOnSuccessListener {
                        repo.createChatRoomForChatMate(chat.receiverId.toString(),newChat).addOnSuccessListener {
                            _dataStatus.value = Resource.success()
                        }.addOnFailureListener {
                            _dataStatus.value = Resource.error(it.localizedMessage)
                        }
                    }
                    .addOnFailureListener { error ->
                        _dataStatus.value = Resource.error(error.localizedMessage)
                    }
            }catch (e : Exception){
                _dataStatus.value = Resource.error(e.localizedMessage)
            }
        }else{
            _dataStatus.value = Resource.error("Hata, tekrar deneyyin")
        }
    }
    private fun createChatForChatMate() : ChatModel {
        return ChatModel(
            currentUserId,
            currentUserData.value?.username,
            currentUserData.value?.profileImageUrl,
            "",
            ""
        )
    }
    fun getChatRoomsByReceiverId(receiverId: String){
        repo.getChatRoomData(currentUserId,receiverId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    _isChatRoomExists.value = dataSnapshot.childrenCount > 0
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    _isChatRoomExists.value = false
                }
            })
    }
}