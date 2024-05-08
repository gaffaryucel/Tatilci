package com.androiddevelopers.villabuluyorum.view.user.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.databinding.FragmentReservationDetailsBinding
import com.androiddevelopers.villabuluyorum.databinding.MergeItemCoverImageBinding
import com.androiddevelopers.villabuluyorum.model.ApprovalStatus
import com.androiddevelopers.villabuluyorum.model.PaymentMethod
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.user.reservation.ReservationDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReservationDetailsFragment : Fragment() {

    private var _binding: FragmentReservationDetailsBinding? = null
    private val binding get() = _binding!!

    val viewModel: ReservationDetailsViewModel by viewModels()

    private var _mergeBinding: MergeItemCoverImageBinding? = null
    private val mergeBinding get() = _mergeBinding!!

    private lateinit var userId: String
    private lateinit var villaId: String
    private lateinit var reservationId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationDetailsBinding.inflate(inflater, container, false)
        _mergeBinding = MergeItemCoverImageBinding.bind(binding.root)
        userId = arguments?.getString("user_id") ?: ""
        villaId = arguments?.getString("villa_id") ?: ""
        reservationId = arguments?.getString("reservation_id") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (reservationId.isNotEmpty() && userId.isNotEmpty() && villaId.isNotEmpty()) {
            viewModel.getReservationById(reservationId)
            viewModel.getUserDataById(userId)
            viewModel.getVillaById(villaId)
        }
        binding.layoutUser.setOnClickListener {
            val action = ReservationDetailsFragmentDirections.actionReservationDetailsFragmentToUserProfileFragment(userId)
            Navigation.findNavController(it).navigate(action)
        }
        binding.ivMessage.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Bu sürümde Mesaj özelliği kullanılamaz",
                Toast.LENGTH_SHORT
            ).show()
            //goToMessageFragment
            // TODO: Rezervasyon detaylarından Mesajlar ekranına gidicek
        }
        binding.btnMessageToHomeOwner.setOnClickListener {
            //goToMessageFragment
            Toast.makeText(
                requireContext(),
                "Bu sürümde Mesaj özelliği kullanılamaz",
                Toast.LENGTH_SHORT
            ).show()
        }
        observeLiveData()

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
        viewModel.reservation.observe(viewLifecycleOwner, Observer { myReservation ->
            if (myReservation != null) {
               binding.apply {
                   reservation = myReservation
                   when(myReservation.approvalStatus){
                       ApprovalStatus.WAITING_FOR_APPROVAL ->  {
                           status ="Onay Bekliyor"
                       }
                       ApprovalStatus.APPROVED ->  {
                           tvReservationApprovalStatusApproved.visibility = View.VISIBLE
                           tvNotifyUser.visibility = View.GONE
                           status = "Onaylandı"
                       }
                       ApprovalStatus.NOT_APPROVED -> {
                           tvReservationApprovalStatusNotApproved.visibility = View.VISIBLE
                           tvNotifyUser.visibility = View.GONE
                           status =  "İptal Edildi"
                       }
                       null ->{
                           status = "Onay Bekliyor"
                       }
                   }
               }
                when(myReservation.paymentMethod){
                    PaymentMethod.CASH -> {
                        binding.payment = "Nakit"
                    }
                    PaymentMethod.BANK_TRANSFER -> {
                        binding.payment = "EFT/Havale"
                    }
                    PaymentMethod.CREDIT_OR_DEBIT_CARD -> {
                        binding.payment = "Kredi/Banka kartı"
                    }
                    else -> {
                        binding.payment = "Nakit"
                    }
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
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }
}