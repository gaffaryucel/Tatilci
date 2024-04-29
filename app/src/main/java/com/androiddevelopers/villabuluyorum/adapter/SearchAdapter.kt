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
import com.androiddevelopers.villabuluyorum.databinding.RowBestHouseBinding
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.view.user.search.SearchFragmentDirections
import com.bumptech.glide.Glide

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

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

    inner class SearchViewHolder(val binding: RowBestHouseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            RowBestHouseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val house = villaList[position]

        try {
            Glide.with(holder.itemView.context).load(house.coverImage)
                .placeholder(R.drawable.app_logo)
                .error(R.drawable.app_logo)
                .into(holder.binding.ivBestHouse)

            holder.binding.tvTitle.text = house.villaName ?: "Güzel Bir Villa"
            holder.binding.tvPrice.text = "₺${house.nightlyRate ?: 4000}/Gece"
            holder.binding.tvBedroom.text = "${house.bathroomCount ?: 3} Yatak odası"
            holder.binding.tvBathroom.text = "${house.bathroomCount ?: 2} Banyo"

            house.villaId?.let { id ->
                holder.itemView.setOnClickListener {
                    val directions =
                        SearchFragmentDirections.actionSearchFragmentToVillaDetailFragment(id)
                    Navigation.findNavController(it).navigate(directions)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(holder.itemView.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return villaList.size
    }
}
