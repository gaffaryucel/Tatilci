package com.androiddevelopers.villabuluyorum.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.databinding.RowHostHouseBinding
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.view.host.villa.HostVillaFragmentDirections

class HostHouseAdapter : RecyclerView.Adapter<HostHouseAdapter.HostHouseViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<Villa>() {
        override fun areItemsTheSame(oldItem: Villa, newItem: Villa): Boolean {
            return oldItem.villaId == newItem.villaId
        }

        override fun areContentsTheSame(oldItem: Villa, newItem: Villa): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(
        this,
        diffUtil
    )

    var housesList: List<Villa>
        get() = asyncListDiffer.currentList
        set(value) = asyncListDiffer.submitList(value)

    inner class HostHouseViewHolder(val binding: RowHostHouseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostHouseViewHolder {
        val binding = RowHostHouseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HostHouseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HostHouseViewHolder, position: Int) {
        val house = housesList[position]
        with(holder) {
            with(binding) {
                downloadImage(
                    imageHouse,
                    house.coverImage
                )

                textTitle.text = house.villaName

                (house.locationNeighborhoodOrVillage + ", " + house.locationDistrict + ", " + house.locationProvince).also { address ->
                    textAddress.text = address
                }

                house.propertyType?.let { type ->
                    textType.visibility = View.VISIBLE
                    when (type) {
                        PropertyType.HOUSE       -> {
                            textType.text = buildString {
                                append("Villa")
                            }
                        }

                        PropertyType.APARTMENT   -> {
                            textType.text = buildString {
                                append("Apartman")
                            }
                        }

                        PropertyType.GUEST_HOUSE -> {
                            textType.text = buildString {
                                append("Misafir Evi")
                            }
                        }

                        PropertyType.HOTEL       -> {
                            textType.text = buildString {
                                append("Otel")
                            }
                        }
                    }
                } ?: run {
                    textType.visibility = View.GONE
                }

                house.isForSale?.let { status ->
                    if (status) {
                        textStatus.text = buildString {
                            append("Satılık")
                        }
                    } else {
                        textStatus.text = buildString {
                            append("Kiralık")
                        }
                    }

                }
            }

            itemView.setOnClickListener { view ->
                house.villaId?.let { id ->
                    val directions =
                        HostVillaFragmentDirections.actionNavigationHostVillaToHostVillaDetailFragment(
                            id
                        )
                    Navigation.findNavController(view)
                        .navigate(directions)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return housesList.size
    }
}