package com.androiddevelopers.villabuluyorum.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.RowNotificationBinding
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.util.NotificationType
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.view.host.HostBottomNavigationActivity
import com.androiddevelopers.villabuluyorum.view.host.notification.HostNotificationFragment
import com.androiddevelopers.villabuluyorum.view.host.notification.HostNotificationFragmentDirections
import com.androiddevelopers.villabuluyorum.view.host.villa.HostVillaFragment
import com.androiddevelopers.villabuluyorum.view.host.villa.HostVillaFragmentDirections
import com.androiddevelopers.villabuluyorum.view.notification.NotificationsFragmentDirections
import com.androiddevelopers.villabuluyorum.view.user.BottomNavigationActivity
import com.androiddevelopers.villabuluyorum.view.user.villa.HomeFragmentDirections
import com.androiddevelopers.villabuluyorum.view.user.villa.VillaDetailFragmentDirections
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
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

    var isHostMode = false
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
        fun goToHostModeSnackBar(item : String,type: NotificationType){
            val snackbar = Snackbar.make(itemView, "Detayları görüntülemek için lütfen Ev Sahibi moduna geçin", Snackbar.LENGTH_LONG)
            snackbar.setAction("Ev sahibi moduna geç") {
                gotoHostActivity(itemView.context,item,type)
            }
            snackbar.show()
        }
        fun goToGuestModeSnackBar(item : String,type: NotificationType){
            val snackbar = Snackbar.make(itemView, "Detayları görüntülemek için lütfen Tatilci moduna geeing", Snackbar.LENGTH_LONG)
            snackbar.setAction("Tatilci moduna geç") {
                gotoUserHome(itemView.context,item,type)
            }
            snackbar.show()
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
        holder.itemView.setOnClickListener{
            val itemId = notification.itemId
            if (itemId!=null){
                when(notification.notificationType){
                    NotificationType.RESERVATION_STATUS_CHANGE ->{
                        if (isHostMode){
                            holder.goToGuestModeSnackBar(itemId,NotificationType.RESERVATION_STATUS_CHANGE)
                        }else{
                            val action = NotificationsFragmentDirections.actionNotificationsFragmentToReservationDetailsFragment(itemId)
                            Navigation.findNavController(holder.itemView).navigate(action)
                        }
                    }
                    NotificationType.HOST_RESERVATION ->{
                        if (isHostMode){
                            val action = HostNotificationFragmentDirections.actionNavigationHostNotificationToHostReservationDetailsFragment(itemId)
                            Navigation.findNavController(it).navigate(action)
                        }else{
                            holder.goToHostModeSnackBar(itemId,NotificationType.HOST_RESERVATION)
                        }
                    }
                    NotificationType.COMMENT ->{
                        if (isHostMode){
                            // TODO: Ev sahibi aktivitesinde bildirimler sayfasından villa yoryumlarına action sağla
                            Toast.makeText(holder.itemView.context, "Bu özellik etkin değil", Toast.LENGTH_SHORT).show()
                        }else{
                            holder.goToHostModeSnackBar(itemId,NotificationType.COMMENT)
                        }
                    }
                    null ->{

                    }
                }

            }
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
    private fun gotoUserHome(context : Context, item : String, type : NotificationType) {
        val intent = Intent(context, BottomNavigationActivity::class.java)
        intent.putExtra("type",type.toString())
        intent.putExtra("item",item)
        context.startActivity(intent)
    }
    private fun gotoHostActivity(context : Context, item : String, type : NotificationType) {
        val intent = Intent(context, HostBottomNavigationActivity::class.java)
        intent.putExtra("type",type.toString())
        intent.putExtra("item",item)
        context.startActivity(intent)
    }
}
