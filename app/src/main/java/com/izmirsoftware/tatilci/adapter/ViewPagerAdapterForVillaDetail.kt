package com.izmirsoftware.tatilci.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.izmirsoftware.tatilci.databinding.ColumnViewpagerForVillaCreateBinding

class ViewPagerAdapterForVillaDetail :
    RecyclerView.Adapter<ViewPagerAdapterForVillaDetail.ViewPagerHolder>() {

    private var images: ArrayList<String> = arrayListOf()

    inner class ViewPagerHolder(val binding: ColumnViewpagerForVillaCreateBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        holder.binding.buttonDeleteViewPagerVillaCreate.visibility = View.GONE
        holder.binding.imageUrl = images[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(newList: List<String>) {
        images.clear()
        images.addAll(newList.toList())
        notifyDataSetChanged()
    }
}