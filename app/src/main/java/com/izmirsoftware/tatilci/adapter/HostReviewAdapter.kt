package com.izmirsoftware.tatilci.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.izmirsoftware.tatilci.R
import com.izmirsoftware.tatilci.databinding.RowHostReviewBinding
import com.izmirsoftware.tatilci.model.ReviewModel

class HostReviewAdapter : RecyclerView.Adapter<HostReviewAdapter.HostReviewViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<ReviewModel>() {
        override fun areItemsTheSame(
            oldItem: ReviewModel, newItem: ReviewModel
        ): Boolean {
            return oldItem.reviewId == newItem.reviewId
        }

        override fun areContentsTheSame(
            oldItem: ReviewModel, newItem: ReviewModel
        ): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(
        this,
        diffUtil
    )

    var reviewList: List<ReviewModel>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    inner class HostReviewViewHolder(val binding: RowHostReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostReviewViewHolder {
        val binding = RowHostReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HostReviewViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HostReviewViewHolder, position: Int) {
        val myReview = reviewList[position]
        holder.binding.apply {
            review = myReview
        }
        val stars = arrayOf(
            holder.binding.star1,
            holder.binding.star2,
            holder.binding.star3,
            holder.binding.star4,
            holder.binding.star5
        )

        updateStars(
            myReview.rating ?: 1,
            stars
        )

    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}

private fun updateStars(rating: Int, stars: Array<ImageView>) {
    for (i in stars.indices) {
        if (i < rating) {
            stars[i].setImageResource(R.drawable.star)
        } else {
            stars[i].setImageResource(R.drawable.star_grey)
        }
    }
}
