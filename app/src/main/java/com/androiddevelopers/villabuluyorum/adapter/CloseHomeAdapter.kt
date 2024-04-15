package com.androiddevelopers.villabuluyorum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.databinding.RowHouseBinding
import com.androiddevelopers.villabuluyorum.model.villa.Villa

class HouseAdapter : RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Villa>() {
        override fun areItemsTheSame(oldItem: Villa, newItem: Villa): Boolean {
            return oldItem.villaId == newItem.villaId
        }

        override fun areContentsTheSame(oldItem: Villa, newItem: Villa): Boolean {
            return oldItem.villaId == newItem.villaId
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var housesList: List<Villa>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class HouseViewHolder(val binding: RowHouseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        val binding = RowHouseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HouseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = housesList[position]

        try {
            downloadImage(holder.binding.imageHouse, house.coverImage)

            holder.binding.textTitle.text = house.villaName ?: "Deniz kenarı villa"
            (house.locationAddress + house.locationNeighborhoodOrVillage + house.locationDistrict + house.locationProvince).also { address ->
                holder.binding.textAddress.text = address
            }
            holder.binding.textDistance.text = "5KM"

            holder.itemView.setOnClickListener {

            }
        } catch (e: Exception) {
            // Hata durumunda bir işlem yapabilirsiniz
            println("error : " + e)
        }
    }

    override fun getItemCount(): Int {
        return housesList.size
    }
}
