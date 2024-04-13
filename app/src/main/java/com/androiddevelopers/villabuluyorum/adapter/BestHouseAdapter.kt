package com.androiddevelopers.villabuluyorum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.RowBestHouseBinding
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.bumptech.glide.Glide

class BestHouseAdapter : RecyclerView.Adapter<BestHouseAdapter.HouseViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Villa>() {
        override fun areItemsTheSame(oldItem: Villa, newItem: Villa): Boolean {
            return oldItem.villaId == newItem.villaId
        }

        override fun areContentsTheSame(oldItem: Villa, newItem: Villa): Boolean {
            return oldItem.villaId == newItem.villaId
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var villaList: List<Villa>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class HouseViewHolder(val binding: RowBestHouseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        val binding = RowBestHouseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HouseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = villaList[position]

        try {
            holder.binding.ivBestHouse.setImageResource(R.drawable.ic_launcher_background)
           /*
            Glide.with(holder.itemView.context).load("")
                .into(holder.binding.ivBestHouse)
            */

            holder.binding.tvTitle.text = house.villaName ?: "Güzel Bir Villa"
            holder.binding.tvPrice.text = "₺${house.nightlyRate ?: 4000}/Ay"
            holder.binding.tvBedroom.text = "${house.bathroomCount ?: 3} Yatak odası"
            holder.binding.tvBathroom.text ="${house.bathroomCount ?: 2} Banyo"

            holder.itemView.setOnClickListener {

            }
        } catch (e: Exception) {
            // Hata durumunda bir işlem yapabilirsiniz
            println("error : $e")
        }
    }

    override fun getItemCount(): Int {
        return villaList.size
    }

}
