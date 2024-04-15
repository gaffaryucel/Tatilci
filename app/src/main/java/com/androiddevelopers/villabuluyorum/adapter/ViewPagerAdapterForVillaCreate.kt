package com.androiddevelopers.villabuluyorum.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.villabuluyorum.databinding.ColumnViewpagerForVillaCreateBinding

class ViewPagerAdapterForVillaCreate :
    RecyclerView.Adapter<ViewPagerAdapterForVillaCreate.ViewPagerHolder>() {

    lateinit var listenerImages: (ArrayList<Uri>) -> Unit
    private var images: ArrayList<Uri> = arrayListOf()

    inner class ViewPagerHolder(val binding: ColumnViewpagerForVillaCreateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setImages(images: ArrayList<Uri>) {
            listenerImages.invoke(images)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder =
        ViewPagerHolder(
            ColumnViewpagerForVillaCreateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun getItemCount(): Int = images.size


    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        with(holder) {
            with(binding) {
                imageUrl = images[position].toString()

                buttonDeleteViewPagerVillaCreate.setOnClickListener {
                    images.removeAt(position)
                    setImages(images)
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(newList: List<Uri>) {
        images.clear()
        images.addAll(newList.toList())
        notifyDataSetChanged()
    }
}