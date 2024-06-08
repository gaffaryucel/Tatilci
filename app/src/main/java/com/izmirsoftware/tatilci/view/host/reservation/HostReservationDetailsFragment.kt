package com.izmirsoftware.tatilci.view.host.reservation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.izmirsoftware.tatilci.R
import com.izmirsoftware.tatilci.databinding.FragmentHostReservationDetailsBinding
import com.izmirsoftware.tatilci.databinding.MergeItemCoverImageBinding
import com.izmirsoftware.tatilci.model.ApprovalStatus
import com.izmirsoftware.tatilci.model.PaymentMethod
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideHostBottomNavigation
import com.izmirsoftware.tatilci.util.showHostBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.host.reservation.HostReservationDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostReservationDetailsFragment : Fragment() {

    private var _binding: FragmentHostReservationDetailsBinding? = null
    private val binding get() = _binding!!

    val viewModel: HostReservationDetailsViewModel by viewModels()

    private var _mergeBinding: MergeItemCoverImageBinding? = null
    private val mergeBinding get() = _mergeBinding!!

    private lateinit var reservationId: String
    private var villaImage: String? = null
    private var userId : String? = null
    private var token : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostReservationDetailsBinding.inflate(inflater, container, false)
        _mergeBinding = MergeItemCoverImageBinding.bind(binding.root)
        reservationId = arguments?.getString("reservation_id") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(reservationId.isNotEmpty()) {
            viewModel.getReservationById(reservationId)
        }
        binding.ivMessage.setOnClickListener {
            //goToMessageFragment
            // TODO: Rezervasyon detaylarından Mesajlar ekranına gidicek
        }
        binding.btnConfirmReservation.setOnClickListener {
            if (binding.cbConfirmRules.isChecked){
                goToApprovalFragment(reservationId)
            }else{
                Toast.makeText(requireContext(), "Lütfen Rezervasyon Koşullarını Onaylayın", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun goToApprovalFragment(id : String){
        val action = HostReservationDetailsFragmentDirections.actionHostReservationDetailsFragmentToReservationApprovalFragment(id,villaImage.toString(),userId.toString(),token.toString())
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun observeLiveData() {
        viewModel.reservationMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbReservationDetails.visibility = View.GONE
                    binding.tvErrorReservationDetails.visibility = View.GONE
                    binding.layoutHostReservationDetails.visibility = View.VISIBLE
                }

                Status.LOADING -> {
                    binding.pbReservationDetails.visibility = View.VISIBLE
                    binding.tvErrorReservationDetails.visibility = View.GONE
                    binding.layoutHostReservationDetails.visibility = View.INVISIBLE
                }

                Status.ERROR -> {
                    binding.pbReservationDetails.visibility = View.GONE
                    binding.tvErrorReservationDetails.visibility = View.VISIBLE
                    binding.layoutHostReservationDetails.visibility = View.INVISIBLE
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
                            layoutConfirmRules.visibility = View.GONE
                            btnConfirmReservation.apply {
                                isEnabled = false
                                text = "Onaylandı"
                            }
                            status = "Onaylandı"
                        }
                        ApprovalStatus.NOT_APPROVED -> {
                            tvReservationApprovalStatusNotApproved.visibility = View.VISIBLE
                            layoutConfirmRules.visibility = View.GONE
                            btnConfirmReservation.apply {
                                isEnabled = false
                                text = "İptal Edildi"
                            }
                            status =  "İptal Edildi"
                        }
                        null ->{
                            status = "Onay Bekliyor"
                        }
                    }
                    payment = when(myReservation.paymentMethod){
                        PaymentMethod.CASH ->  "Nakit"
                        PaymentMethod.CREDIT_OR_DEBIT_CARD ->  "Banka/Kredi kartı"
                        PaymentMethod.BANK_TRANSFER ->"EFT/Havale"
                        null -> "Nakit"
                    }
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { myUser ->
            if (myUser != null) {
                Glide.with(requireContext())
                    .load(myUser.profileImageUrl)
                    .into(binding.ivUserPhoto)
                binding.apply {
                    user = myUser
                }
                userId = myUser.userId
                token = myUser.token
            }
        })
        viewModel.liveDataFirebaseVilla.observe(viewLifecycleOwner, Observer { villa ->
            if (villa != null) {
                villaImage = villa.coverImage
                mergeBinding.textDetailTitle.text = villa.villaName
                mergeBinding.textDetailAddress.text = villa.locationAddress
                mergeBinding.textDetailBedRoom.text = villa.bedroomCount.toString() + " Yatak odası"
                mergeBinding.textDetailBathRoom.text = villa.bathroomCount.toString() + " Banyo"
                Glide.with(requireContext())
                    .load(villa.coverImage)
                    .placeholder(R.drawable.app_logo)
                    .into(mergeBinding.imageTitle)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
        hideHostBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showHostBottomNavigation(requireActivity())
    }
}