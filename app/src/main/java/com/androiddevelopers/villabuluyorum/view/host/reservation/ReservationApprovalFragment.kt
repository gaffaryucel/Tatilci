package com.androiddevelopers.villabuluyorum.view.host.reservation

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostReservationDetailsBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentReservationApprovalBinding
import com.androiddevelopers.villabuluyorum.databinding.MergeItemCoverImageBinding
import com.androiddevelopers.villabuluyorum.model.ApprovalStatus
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideHostBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showHostBottomNavigation
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.viewmodel.host.reservation.HostReservationDetailsViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.host.reservation.ReservationApprovalViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReservationApprovalFragment : Fragment() {

    private var _binding: FragmentReservationApprovalBinding? = null
    private val binding get() = _binding!!

    val viewModel: ReservationApprovalViewModel by viewModels()

    private lateinit var reservationId: String

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationApprovalBinding.inflate(inflater, container, false)
        reservationId = arguments?.getString("id") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())

        binding.btnApprove.setOnClickListener {
            if (reservationId.isNotEmpty()){
                if (binding.checkBoxAccept.isChecked){
                    viewModel.changeReservationStatus(
                        reservationId,
                        ApprovalStatus.APPROVED
                    )
                }else{
                    Toast.makeText(
                        requireContext(),
                        "Lütfen koşulları kabul edin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.btnDecline.setOnClickListener {
            if (reservationId.isNotEmpty()){
                showRejectConfirmationDialog()
            }
        }
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
    private fun observeLiveData() {
        viewModel.reservationMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog?.dismiss()
                    Toast.makeText(
                        requireContext(),
                        it.data,
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
    }
    private fun showRejectConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Rezervasyonu Reddet")
        builder.setMessage("Rezervasyonu reddettiğinizde, bu bilgi kullanıcıya iletilir ve rezervasyon iptal edilir. Devam etmek istiyor musunuz?")
        builder.setPositiveButton("Evet") { _, _ ->
            viewModel.changeReservationStatus(reservationId,ApprovalStatus.NOT_APPROVED)
        }
        builder.setNegativeButton("İptal") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}