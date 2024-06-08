package com.izmirsoftware.tatilci.view.user.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.izmirsoftware.tatilci.R
import com.izmirsoftware.tatilci.adapter.ReviewAdapter
import com.izmirsoftware.tatilci.databinding.FragmentReviewBinding
import com.izmirsoftware.tatilci.model.ReservationModel
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideBottomNavigation
import com.izmirsoftware.tatilci.util.showBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.user.review.ReviewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewFragment : Fragment() {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!

    val viewModel: ReviewViewModel by viewModels()

    private val reviewAdapter: ReviewAdapter = ReviewAdapter()

    private var ratedReservations = ArrayList<ReservationModel>()
    private var unRatedReservations = ArrayList<ReservationModel>()

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

        val buttons = listOf(binding.tv0, binding.tv1)

        buttons.forEach { button ->
            button.setOnClickListener {
                selectButton(button, buttons)
            }
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeLiveData() {
        viewModel.reviewMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbReviews.visibility = View.GONE
                }

                Status.LOADING -> {
                    binding.pbReviews.visibility = View.VISIBLE
                }

                Status.ERROR -> {
                    binding.pbReviews.visibility = View.GONE
                    binding.layoutEmptyList.visibility = View.VISIBLE
                }
            }
        })
        viewModel.reservations.observe(viewLifecycleOwner, Observer { reviews ->
            if (reviews != null && reviews.isNotEmpty()) {
                binding.layoutEmptyList.visibility = View.GONE
                splitReservationsList(reviews)
            } else {
                binding.layoutEmptyList.visibility = View.VISIBLE
            }
        })
        if (unRatedReservations.isEmpty()){
            binding.layoutEmptyList.visibility = View.VISIBLE
        }
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
        observeLiveData()
    }

    override fun onPause() {
        showBottomNavigation(requireActivity())
        super.onPause()
    }


    private fun selectButton(selected: TextView, buttons: List<TextView>) {
        buttons.forEach { button ->
            if (button == selected) {
                button.setBackgroundResource(R.drawable.selected_text_bg)
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                when(selected){
                    binding.tv0-> {
                        changeReservationList(unRatedReservations)
                        reviewAdapter.isReviewed = true
                    }
                    binding.tv1-> {
                        changeReservationList(ratedReservations)
                        reviewAdapter.isReviewed = false
                    }
                }
            } else {
                button.setBackgroundResource(R.drawable.selectable_text_bg)
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.normal_text_color))
            }
        }
    }
    private fun changeReservationList(reservations : ArrayList<ReservationModel>?){
        if (!reservations.isNullOrEmpty()) {
            reviewAdapter.reservationlist = reservations
            binding.layoutEmptyList.visibility = View.GONE
            binding.rvReviews.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyList.visibility = View.VISIBLE
            binding.rvReviews.visibility = View.INVISIBLE
        }
    }
    private fun splitReservationsList(reservations : List<ReservationModel>){
        val rReservations = ArrayList<ReservationModel>()
        val unrReservation = ArrayList<ReservationModel>()
        reservations.forEach {
            if (it.rated != null && it.rated == true){
                rReservations.add(it)
            }else{
                unrReservation.add(it)
            }
        }
        reviewAdapter.reservationlist = unrReservation
        ratedReservations = rReservations
        unRatedReservations = unrReservation
    }
}
