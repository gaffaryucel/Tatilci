package com.androiddevelopers.villabuluyorum.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.RowReservationBinding
import com.androiddevelopers.villabuluyorum.model.ApprovalStatus
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.ReservationModel
import com.androiddevelopers.villabuluyorum.view.host.reservation.HostReservationFragmentDirections
import com.androiddevelopers.villabuluyorum.view.user.reservation.ReservationFragmentDirections
import com.bumptech.glide.Glide

class HostReservationAdapter : RecyclerView.Adapter<HostReservationAdapter.HostReservationViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ReservationModel>() {
        override fun areItemsTheSame(
            oldItem: ReservationModel,
            newItem: ReservationModel
        ): Boolean {
            return oldItem.reservationId == newItem.reservationId
        }

        override fun areContentsTheSame(
            oldItem: ReservationModel,
            newItem: ReservationModel
        ): Boolean {
            return oldItem.reservationId == newItem.reservationId
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var reservationList: List<ReservationModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class HostReservationViewHolder(val binding: RowReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Görünümleri buraya ekle
        val ivBestHouse = binding.ivBestHouse
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostReservationViewHolder {
        val binding =
            RowReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HostReservationViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HostReservationViewHolder, position: Int) {
        val myReservation = reservationList[position]

        try {
            Glide.with(holder.itemView.context).load(myReservation.villaImage)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(holder.ivBestHouse)
            holder.binding.apply {
                reservation = myReservation
                if (myReservation.propertyType != null) {
                    when (myReservation.propertyType) {
                        PropertyType.HOUSE -> tvPropertyType.text = "Villa"
                        PropertyType.APARTMENT -> tvPropertyType.text = "Apartman"
                        PropertyType.GUEST_HOUSE -> tvPropertyType.text = "Misafir evi"
                        PropertyType.HOTEL -> tvPropertyType.text = "Otel"
                    }

                } else {
                    tvPropertyType.text = "Villa"
                }

                if (myReservation.approvalStatus != null) {
                    when (myReservation.approvalStatus) {
                        ApprovalStatus.APPROVED -> tvApprovalStatus.text = "Onaylandı"
                        ApprovalStatus.NOT_APPROVED -> tvApprovalStatus.text = "Onaylanmadı"
                        ApprovalStatus.WAITING_FOR_APPROVAL -> tvApprovalStatus.text =
                            "Onay bekliyor"
                    }
                } else {
                    tvApprovalStatus.text = "Onay bekliyor"
                }
            }
            //holder.binding.tvPropertyType.text = myReservation.propertyType ?: "Apartman"



            holder.itemView.setOnClickListener {
                val directions =
                    HostReservationFragmentDirections.actionNavigationHostReservationToHostReservationDetailsFragment(
                        myReservation.villaId.toString(),
                        myReservation.userId.toString(),
                        myReservation.reservationId.toString(),
                    )
                Navigation.findNavController(it).navigate(directions)
            }
        } catch (e: Exception) {
            // Hata oluştuğunda Toast göster
            Toast.makeText(holder.itemView.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return reservationList.size
    }
}
