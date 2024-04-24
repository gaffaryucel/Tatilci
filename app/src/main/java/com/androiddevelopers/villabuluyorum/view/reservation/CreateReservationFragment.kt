package com.androiddevelopers.villabuluyorum.view.reservation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentCreateReservationBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentUserProfileBinding
import com.androiddevelopers.villabuluyorum.databinding.MergeItemCoverImageBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.profile.UserProfileViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.reservation.CreateReservationViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateReservationFragment : Fragment() {

    private var _binding: FragmentCreateReservationBinding? = null
    private val binding get() = _binding!!

    val viewModel: CreateReservationViewModel by viewModels()

    private var _mergeBinding: MergeItemCoverImageBinding? = null
    private val mergeBinding get() = _mergeBinding!!

    private lateinit var villaId : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReservationBinding.inflate(inflater, container, false)
        _mergeBinding = MergeItemCoverImageBinding.bind(binding.root)
        villaId = arguments?.getString("villa_id") ?: ""
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (villaId.isNotEmpty()){
            viewModel.getVillaByIdFromFirestore(villaId)
        }
        observeLiveData()
    }
    private fun observeLiveData() {
        with(binding) {
            with(viewModel) {
                liveDataFirebaseStatus.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.pbReservation.visibility = View.GONE
                            binding.tvErrorReservation.visibility = View.GONE
                        }

                        Status.LOADING -> {
                            binding.pbReservation.visibility = View.VISIBLE
                            binding.tvErrorReservation.visibility = View.GONE
                        }

                        Status.ERROR -> {
                            binding.pbReservation.visibility = View.GONE
                            binding.tvErrorReservation.visibility = View.VISIBLE
                        }
                    }
                }

                liveDataFirebaseVilla.observe(viewLifecycleOwner) {villa->
                    mergeBinding.textDetailTitle.text =  villa.villaName
                    mergeBinding.textDetailAddress.text =  villa.locationAddress
                    mergeBinding.textDetailBedRoom.text =  villa.bedroomCount.toString()+" Yatak odası"
                    mergeBinding.textDetailBathRoom.text =  villa.bathroomCount.toString()+" Banyo"
                    Glide.with(requireContext()).load(villa.coverImage).into(mergeBinding.imageTitle)
                }
            }
        }
    }


}