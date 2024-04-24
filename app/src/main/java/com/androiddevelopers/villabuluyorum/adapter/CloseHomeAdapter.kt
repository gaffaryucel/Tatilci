package com.androiddevelopers.villabuluyorum.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.databinding.RowHouseBinding
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.view.HomeFragmentDirections
import com.bumptech.glide.Glide
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class HouseAdapter(val myLocation :MyLocation? = null) : RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {

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
        val binding = holder.binding

        try {
            downloadImage(binding.imageHouse, house.coverImage)

            binding.textTitle.text = house.villaName ?: "Deniz kenarı villa"
            (house.locationNeighborhoodOrVillage + ", " + house.locationDistrict + ", " + house.locationProvince).also { address ->
                binding.textAddress.text = address
            }

            if (myLocation != null){
                val distanceInKm = calculateDistance(myLocation.latitude, myLocation.longitude, house.latitude ?: 0.0, house.longitude ?: 0.0)
                holder.binding.textDistance.text = "${distanceInKm.toInt()}km"
            }else{
                holder.binding.layoutDistance.visibility = ViewGroup.INVISIBLE
            }


            house.villaId?.let { id ->
                holder.itemView.setOnClickListener {
                    val directions =
                        HomeFragmentDirections.actionNavigationHomeToVillaDetailFragment(id)
                    Navigation.findNavController(it).navigate(directions)
                }
            }

        } catch (e: Exception) {
            Toast.makeText(holder.itemView.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return housesList.size
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) +
                cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515 // Mil cinsinden mesafeyi hesapla
        return dist * 1.60934 // Mil'i kilometreye çevir
    }

    fun deg2rad(deg: Double): Double {
        return deg * PI / 180.0
    }

    fun rad2deg(rad: Double): Double {
        return rad * 180.0 / PI
    }

}
class MyLocation(
    var latitude : Double,
    var longitude : Double
)
