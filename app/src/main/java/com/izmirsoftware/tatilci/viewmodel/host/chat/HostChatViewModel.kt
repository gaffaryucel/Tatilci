package com.izmirsoftware.tatilci.viewmodel.host.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.chat.ChatModel
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HostChatViewModel  @Inject constructor(
    private val repo : FirebaseRepoInterFace,
    private val auth : FirebaseAuth
): ViewModel() {

    val currentUserId = auth.currentUser?.uid.toString()

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage : LiveData<Resource<Boolean>>
        get() = _firebaseMessage

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
        _firebaseMessage.value = Resource.loading(null)
        repo.getAllChatRooms(currentUserId ?: "").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<ChatModel>()
                    for (messageSnapshot in snapshot.children) {
                        val chat = messageSnapshot.getValue(ChatModel::class.java)
                        if (chat?.hostId.equals(currentUserId)){
                            chat?.let {
                                chatList.add(it)
                            }
                        }
                    }
                    _chatRooms.value = chatList
                    if (chatList.isEmpty()){
                        _firebaseMessage.value = Resource.success(false)
                    }else{
                        _firebaseMessage.value = Resource.success(true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _firebaseMessage.value = Resource.error("Hata, tekrar deneyin", null)
                }
            })
    }

    fun searchChatByUsername(query: String) = viewModelScope.launch{
        val list = chatRooms.value
        _chatSearchResult.value = list?.filter { it.receiverUserName!!.contains(query, ignoreCase = true) }
    }
}
/*
 repo.getAllChatRooms(currentUserId ?: "").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<ChatModel>()
                    for (messageSnapshot in snapshot.children) {
                        val chat = messageSnapshot.getValue(ChatModel::class.java)
                        println("chat : "+chat?.hostId)
                        println("currentUserId : "+currentUserId)
                        if (chat?.hostId.equals(currentUserId)){
                            chat?.let {
                                chatList.add(it)
                            }
                            println("hostId : "+chat?.chatLastMessage)
                        }
                    }
                    _chatRooms.value = chatList

                }

                override fun onCancelled(error: DatabaseError) {
                    _firebaseMessage.value = Resource.error("Hata, tekrar deneyin", null)
                }
            })
 */