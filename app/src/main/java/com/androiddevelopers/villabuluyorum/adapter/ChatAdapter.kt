package com.androiddevelopers.villabuluyorum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.databinding.RowChatBinding
import com.androiddevelopers.villabuluyorum.model.chat.ChatModel
import com.androiddevelopers.villabuluyorum.view.chat.ChatsFragmentDirections
import com.androiddevelopers.villabuluyorum.view.host.chat.HostChatFragmentDirections
import com.bumptech.glide.Glide

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ChatModel>() {
        override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem.receiverId == newItem.receiverId
        }

        override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var chatsList: List<ChatModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class ChatViewHolder(val binding: RowChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = RowChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }
    var isHost = false

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatsList[position]
        val sharedPref = holder.itemView.context.getSharedPreferences("cht", Context.MODE_PRIVATE)

        try {
            Glide.with(holder.itemView.context).load(chat.receiverUserImage)
                .into(holder.binding.chatImage)
            val time = chat.chatLastMessageTimestamp
            if (time != null){
                holder.binding.chatLastMessageTimeStamp.text = time.substringAfter(" ").split(":").take(2).joinToString(separator = ":")
            }
            val seen = chat.seen
            if (seen != null){
                if (seen){
                    holder.binding.unreadMessageIndicator.visibility = ViewGroup.INVISIBLE
                }else{
                    holder.binding.unreadMessageIndicator.visibility = ViewGroup.VISIBLE
                }
            }else{
                holder.binding.unreadMessageIndicator.visibility = ViewGroup.INVISIBLE
            }
            holder.binding.apply {
                chatItem = chat
            }
            if (chat.chatLastMessage.isNullOrEmpty()){
                holder.binding.chatLastMessage.text = "İlk Mesajı gönder"
            }else{
                holder.binding.chatLastMessage.text = chat.chatLastMessage
            }
            holder.itemView.setOnClickListener {
                sharedPref.edit().putString("place", "chat").apply()
                if (isHost){
                    val action = HostChatFragmentDirections.actionHostChatFragmentToNavigationHostMessage(
                        chat.receiverId.toString()
                    )
                    Navigation.findNavController(it).navigate(action)
                }else{
                    val action = ChatsFragmentDirections.actionChatsFragmentToMessagesFragment(
                        chat.receiverId.toString()
                    )
                    Navigation.findNavController(it).navigate(action)
                }

            }
        }catch (e: Exception){
            Toast.makeText(holder.itemView.context, "Hata : Chat", Toast.LENGTH_SHORT).show()
        }


    }

    override fun getItemCount(): Int {
        return chatsList.size
    }
}