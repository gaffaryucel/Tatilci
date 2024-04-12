package com.androiddevelopers.villabuluyorum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.databinding.RowHouseBinding
import com.androiddevelopers.villabuluyorum.model.Villa
import com.bumptech.glide.Glide

class HouseAdapter : RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Villa>() {
        override fun areItemsTheSame(oldItem: Villa, newItem: Villa): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Villa, newItem: Villa): Boolean {
            return oldItem.id == newItem.id
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var housesList: List<Villa>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class HouseViewHolder(val binding: RowHouseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        val binding = RowHouseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HouseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        val house = housesList[position]

        try {
            println("try")
            Glide.with(holder.itemView.context).load("")
                .into(holder.binding.imageHouse)

            holder.binding.textTitle.text = house.villaName
            holder.binding.textAddress.text = house.location
            holder.binding.textDistance.text = "Mesafe"

            holder.itemView.setOnClickListener {

            }
        } catch (e: Exception) {
            // Hata durumunda bir işlem yapabilirsiniz
            println("error : "+e)
        }
    }

    override fun getItemCount(): Int {
        return housesList.size
    }
}
