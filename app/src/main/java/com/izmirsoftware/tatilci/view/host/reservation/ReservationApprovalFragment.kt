package com.izmirsoftware.tatilci.view.host.reservation

import android.app.AlertDialog
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
import com.izmirsoftware.tatilci.databinding.FragmentReservationApprovalBinding
import com.izmirsoftware.tatilci.model.ApprovalStatus
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideHostBottomNavigation
import com.izmirsoftware.tatilci.util.showHostBottomNavigation
import com.izmirsoftware.tatilci.util.startLoadingProcess
import com.izmirsoftware.tatilci.viewmodel.host.reservation.ReservationApprovalViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ReservationApprovalFragment : Fragment() {

    private var _binding: FragmentReservationApprovalBinding? = null
    private val binding get() = _binding!!

    val viewModel: ReservationApprovalViewModel by viewModels()

    private lateinit var reservationId: String
    private lateinit var villaImage : String
    private lateinit var userId : String
    private lateinit var token : String

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationApprovalBinding.inflate(inflater, container, false)
        reservationId = arguments?.getString("id") ?: ""
        villaImage = arguments?.getString("image") ?: ""
        userId = arguments?.getString("userId") ?: ""
        token = arguments?.getString("token") ?: ""
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
                    viewModel.reservationStatusNotifier(reservationId,"Rezervasyon Onaylandı","rezervasyon isteğinizi onayladı",villaImage,userId,token)
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
            viewModel.reservationStatusNotifier(reservationId,"Rezervasyon reddedildi","rezervasyon isteğinizi geri çevirdi",villaImage,userId,token)
        }
        builder.setNegativeButton("İptal") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}