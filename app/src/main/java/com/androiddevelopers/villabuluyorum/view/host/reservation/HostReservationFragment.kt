package com.androiddevelopers.villabuluyorum.view.host.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.HostReservationAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostReservationBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.host.reservation.HostReservationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostReservationFragment : Fragment() {

    private var _binding: FragmentHostReservationBinding? = null
    private val binding get() = _binding!!

    val viewModel: HostReservationViewModel by viewModels()

    private val reservationAdapter: HostReservationAdapter = HostReservationAdapter()

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
    private fun selectButton(selected: TextView, buttons: List<TextView>) {
        buttons.forEach { button ->
            if (button == selected) {
                button.setBackgroundResource(R.drawable.selected_text_bg)
                button.setTextColor(R.color.white)
            } else {
                button.setBackgroundResource(R.drawable.selectable_text_bg)
                button.setTextColor(R.color.normal_text_color)
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
            } else {
                binding.layoutEmptyList.visibility = View.VISIBLE
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}