package com.androiddevelopers.villabuluyorum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.RowNotificationBinding
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<InAppNotificationModel>() {
        override fun areItemsTheSame(oldItem: InAppNotificationModel, newItem: InAppNotificationModel): Boolean {
            return oldItem.notificationId == newItem.notificationId
        }

        override fun areContentsTheSame(oldItem: InAppNotificationModel, newItem: InAppNotificationModel): Boolean {
            return oldItem.notificationId == newItem.notificationId
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var notificationList: List<InAppNotificationModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class NotificationViewHolder(val binding: RowNotificationBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindView(notification : InAppNotificationModel) {
            binding.message = notification.title
            binding.name = notification.userName
            binding.time = getTimeFromDate(notification.time.toString())
            val image = notification.imageUrl
            val userImage = notification.userImage
            if (!image.isNullOrEmpty()){
                Glide.with(itemView.context).load(image).into(binding.ivNotificationImage)
            }else{
                binding.ivNotificationImage.visibility = ViewGroup.GONE
            }
            if (!userImage.isNullOrEmpty()){
                Glide.with(itemView.context).load(userImage).into(binding.ivNotificationUserPhoto)
            }else{
                binding.ivNotificationUserPhoto.setBackgroundResource(R.drawable.app_logo)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = RowNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]

        try {
            holder.bindView(notification)
        } catch (e: Exception) {
            Toast.makeText(holder.itemView.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    private fun getTimeFromDate(date: String): String? {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val parsedDate = dateFormat.parse(date) // String formatındaki tarihi Date objesine çevir
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return parsedDate?.let { timeFormat.format(it) }
    }
}
