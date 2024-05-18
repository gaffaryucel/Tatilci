package com.androiddevelopers.villabuluyorum.view.user.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevelopers.villabuluyorum.adapter.ReviewAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentReviewBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.user.review.ReviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    val viewModel: ReviewViewModel by viewModels()

    private val reviewAdapter: ReviewAdapter = ReviewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvReviews.adapter = reviewAdapter
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.reviewMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbReviews.visibility = View.GONE
                    if (it.data == true) {
                        binding.layoutEmptyList.visibility = View.GONE
                    } else {
                        binding.layoutEmptyList.visibility = View.VISIBLE
                    }
                }

                Status.LOADING -> {
                    binding.pbReviews.visibility = View.VISIBLE
                    binding.layoutEmptyList.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbReviews.visibility = View.GONE
                    binding.layoutEmptyList.visibility = View.VISIBLE
                }
            }
        })
        viewModel.reservations.observe(viewLifecycleOwner, Observer { reviews ->
            if (reviews != null && reviews.isNotEmpty()) {
                reviewAdapter.reservationlist = reviews
                binding.layoutEmptyList.visibility = View.GONE
            } else {
                binding.layoutEmptyList.visibility = View.VISIBLE
            }
        })
    }
}
