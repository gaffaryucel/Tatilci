package com.androiddevelopers.villabuluyorum.view.host.reservation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.ReservationAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostReservationDetailsBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentReservationBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentReservationDetailsBindingImpl
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.host.reservation.HostReservationDetailsViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.user.reservation.ReservationViewModel

class HostReservationDetailsFragment : Fragment() {

    private var _binding: FragmentHostReservationDetailsBinding? = null
    private val binding get() = _binding!!

    val viewModel: HostReservationDetailsViewModel by viewModels()

    private val reservationAdapter: ReservationAdapter = ReservationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostReservationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.rvReservations.adapter = reservationAdapter
        observeLiveData()
    }

    private fun observeLiveData() {
        /*
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
            } else {
                binding.layoutEmptyList.visibility = View.VISIBLE
            }
        })

         */
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