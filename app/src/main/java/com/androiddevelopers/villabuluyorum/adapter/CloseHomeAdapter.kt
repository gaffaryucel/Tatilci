package com.androiddevelopers.villabuluyorum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.RowHouseBinding
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.view.HomeFragmentDirections
import com.bumptech.glide.Glide

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
            Glide.with(holder.itemView.context).load(holder.binding.imageHouse)
                .into(holder.binding.imageHouse)

            holder.binding.textTitle.text = house.villaName ?: "Deniz kenarı villa"
            holder.binding.textAddress.text = house.location?.address ?: "İstanbul, Kadıköy"
            holder.binding.textDistance.text = "5KM"

            holder.itemView.setOnClickListener {
                val action = HomeFragmentDirections.actionNavigationHomeToVillaDetailFragment(house.villaId.toString())
                Navigation.findNavController(it).navigate(action)
            }
        } catch (e: Exception) {
            Toast.makeText(holder.itemView.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return housesList.size
    }
}
