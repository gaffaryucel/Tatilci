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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
open class ChatsViewModel  @Inject constructor(
    private val repo : FirebaseRepoInterFace,
    private val auth : FirebaseAuth
): ViewModel() {

    val currentUserId = auth.currentUser?.uid.toString()

    private var _firebasemessage = MutableLiveData<Resource<Boolean>>()
    val firebasemessage : LiveData<Resource<Boolean>>
        get() = _firebasemessage

    //Chat Rooms
    private var _chatRooms = MutableLiveData<List<ChatModel>>()
    val chatRooms : LiveData<List<ChatModel>>
        get() = _chatRooms

    //Search Results
    private var _chatSearchResult = MutableLiveData<List<ChatModel>>()
    val chatSearchResult : LiveData<List<ChatModel>>
        get() = _chatSearchResult

    init {
        getChatRooms()
    }

    private fun getChatRooms () {
        _firebasemessage.value = Resource.loading(null)
        repo.getAllChatRooms(currentUserId ?: "").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<ChatModel>()
                    for (messageSnapshot in snapshot.children) {
                        val chat = messageSnapshot.getValue(ChatModel::class.java)
                        chat?.let {
                            chatList.add(it)
                        }
                    }
                    _chatRooms.value = chatList
                    _firebasemessage.value = Resource.success(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    _firebasemessage.value = Resource.error("Hata, tekrar deneyin", null)
                }
            })
    }
    fun searchChatByUsername(query: String) = viewModelScope.launch{
        val list = chatRooms.value
        _chatSearchResult.value = list?.filter { it.receiverUserName!!.contains(query, ignoreCase = true) }
    }
}