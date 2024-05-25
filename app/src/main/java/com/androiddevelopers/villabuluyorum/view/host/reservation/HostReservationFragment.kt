package com.androiddevelopers.villabuluyorum.view.host.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.HostReservationAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostReservationBinding
import com.androiddevelopers.villabuluyorum.model.ApprovalStatus
import com.androiddevelopers.villabuluyorum.model.ReservationModel
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.host.reservation.HostReservationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostReservationFragment : Fragment() {

    private var _binding: FragmentHostReservationBinding? = null
    private val binding get() = _binding!!

    val viewModel: HostReservationViewModel by viewModels()

    private val reservationAdapter: HostReservationAdapter = HostReservationAdapter()

    private var allReservations = ArrayList<ReservationModel>()
    private var approvedReservations = ArrayList<ReservationModel>()
    private var notApprovedReservations = ArrayList<ReservationModel>()
    private var waitingReservations = ArrayList<ReservationModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvReservations.adapter = reservationAdapter
        val buttons = listOf(binding.tv0, binding.tv1, binding.tv2, binding.tv3)

        buttons.forEach { button ->
            button.setOnClickListener {
                selectButton(button, buttons)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.reservationMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbReservations.visibility = View.GONE
                    if (it.data == true) {
                        binding.layoutEmptyList.visibility = View.GONE
                    } else {
                        binding.layoutEmptyList.visibility = View.VISIBLE
                    }
                }

                Status.LOADING -> {
                    binding.pbReservations.visibility = View.VISIBLE
                    binding.layoutEmptyList.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbReservations.visibility = View.GONE
                    binding.layoutEmptyList.visibility = View.VISIBLE
                }
            }
        })
        viewModel.reservations.observe(viewLifecycleOwner, Observer { reservations ->
            if (reservations != null && reservations.isNotEmpty()) {
                reservationAdapter.reservationList = reservations
                binding.layoutEmptyList.visibility = View.GONE
                allReservations = reservations as ArrayList<ReservationModel>
                splitReservationsList(reservations)
            } else {
                binding.layoutEmptyList.visibility = View.VISIBLE
            }
        })
    }


    private fun selectButton(selected: TextView, buttons: List<TextView>) {
        buttons.forEach { button ->
            if (button == selected) {
                button.setBackgroundResource(R.drawable.selected_text_bg)
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                when(selected){
                    binding.tv0-> changeReservationList(allReservations)
                    binding.tv1-> changeReservationList(waitingReservations)
                    binding.tv2-> changeReservationList(approvedReservations)
                    binding.tv3->changeReservationList(notApprovedReservations)
                }
            } else {
                button.setBackgroundResource(R.drawable.selectable_text_bg)
                button.setTextColor(ContextCompat.getColor(requireContext(), R.color.normal_text_color))
            }
        }
    }
    private fun changeReservationList(reservations : ArrayList<ReservationModel>?){
        if (!reservations.isNullOrEmpty()) {
            reservationAdapter.reservationList = reservations
            binding.layoutEmptyList.visibility = View.GONE
            binding.rvReservations.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyList.visibility = View.VISIBLE
            binding.rvReservations.visibility = View.INVISIBLE
        }
    }
    private fun splitReservationsList(reservations : List<ReservationModel>){
        val apRes = ArrayList<ReservationModel>()
        val notApRes = ArrayList<ReservationModel>()
        val wRes = ArrayList<ReservationModel>()
        reservations.forEach {
            when(it.approvalStatus){
                ApprovalStatus.WAITING_FOR_APPROVAL -> wRes.add(it)
                ApprovalStatus.APPROVED -> apRes.add(it)
                ApprovalStatus.NOT_APPROVED -> notApRes.add(it)
                null -> {

                }
            }
        }
        approvedReservations = apRes
        notApprovedReservations = notApRes
        waitingReservations = wRes
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}