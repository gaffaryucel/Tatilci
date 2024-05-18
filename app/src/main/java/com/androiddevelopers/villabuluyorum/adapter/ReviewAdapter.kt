package com.androiddevelopers.villabuluyorum.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.RowReviewBinding
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.ReservationModel
import com.bumptech.glide.Glide

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

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

    var reservationlist: List<ReservationModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class ReviewViewHolder(val binding: RowReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Görünümleri buraya ekle
        val ivBestHouse = binding.ivBestHouse
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = RowReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val myReservation = reservationlist[position]

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
            }
            //holder.binding.tvPropertyType.text = myReview.propertyType ?: "Apartman"

            holder.itemView.setOnClickListener {
                /*

            val directions =
                ReviewFragmentDirections.actionReviewFragmentToReviewDetailsFragment(
                    myReview.villaId.toString(),
                    myReview.hostId.toString(),
                    myReview.reviewId.toString(),
                )
                Navigation.findNavController(it).navigate(directions)

                 */
            }

        } catch (e: Exception) {
            // Hata oluştuğunda Toast göster
            Toast.makeText(holder.itemView.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return reservationlist.size
    }
}
