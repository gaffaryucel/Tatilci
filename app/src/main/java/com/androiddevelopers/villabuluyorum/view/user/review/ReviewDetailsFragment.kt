package com.androiddevelopers.villabuluyorum.view.user.review

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.databinding.FragmentReviewDetailsBinding
import com.androiddevelopers.villabuluyorum.databinding.MergeItemCoverImageBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.viewmodel.user.review.ReviewDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewDetailsFragment : Fragment() {
    private var _binding: FragmentReviewDetailsBinding? = null
    private val binding get() = _binding!!

    private var _mergeBinding: MergeItemCoverImageBinding? = null
    private val mergeBinding get() = _mergeBinding!!

    val viewModel: ReviewDetailsViewModel by viewModels()
    private var reservationId : String? = null

    private var progressDialog: ProgressDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewDetailsBinding.inflate(inflater, container, false)
        _mergeBinding = MergeItemCoverImageBinding.bind(binding.root)
        reservationId = arguments?.getString("id")
        if (reservationId != null){
            viewModel.getReservationById(reservationId!!)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())

        binding.btnMakeReview.setOnClickListener {
            makeReview()
        }
    }
    private fun makeReview(){
        val rating = binding.ratingBar.rating.toInt()
        val comment = binding.etComment.text.toString()

        if (rating == 0) {
            Toast.makeText(requireContext(), "Lütfen önce yıldız verin", Toast.LENGTH_SHORT).show()
        } else if (comment.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen bir yorum yazın", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.createReview(comment,rating)
        }
    }

    private fun observeLiveData() {
        viewModel.reservationMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbReservationDetails.visibility = View.GONE
                    binding.tvErrorReservationDetails.visibility = View.GONE
                    binding.layoutReservationDetails.visibility = View.VISIBLE
                }

                Status.LOADING -> {
                    binding.pbReservationDetails.visibility = View.VISIBLE
                    binding.tvErrorReservationDetails.visibility = View.GONE
                    binding.layoutReservationDetails.visibility = View.INVISIBLE
                }

                Status.ERROR -> {
                    binding.pbReservationDetails.visibility = View.GONE
                    binding.tvErrorReservationDetails.visibility = View.VISIBLE
                    binding.layoutReservationDetails.visibility = View.INVISIBLE
                }
            }
        })
        viewModel.reviewMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog?.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Geri bildiriminiz için teşekkürler",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }

                Status.LOADING -> {
                    startLoadingProcess(progressDialog)
                }

                Status.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog?.dismiss()
                }

            }
        })
        viewModel.reservation.observe(viewLifecycleOwner, Observer { myReservation ->
            if (myReservation != null) {
                binding.apply {
                    reservation = myReservation
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { myUser ->
            if (myUser != null) {
                binding.apply {
                    user = myUser
                }
                Glide.with(requireContext())
                    .load(myUser.profileImageUrl)
                    .into(binding.ivUserPhoto)
            }
        })
        viewModel.liveDataFirebaseVilla.observe(viewLifecycleOwner, Observer { villa ->
            if (villa != null) {
                mergeBinding.textDetailTitle.text = villa.villaName
                mergeBinding.textDetailAddress.text = villa.locationAddress
                mergeBinding.textDetailBedRoom.text = villa.bedroomCount.toString() + " Yatak odası"
                mergeBinding.textDetailBathRoom.text = villa.bathroomCount.toString() + " Banyo"
                Glide.with(requireContext()).load(villa.coverImage).into(mergeBinding.imageTitle)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }
}
