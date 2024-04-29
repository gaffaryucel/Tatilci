package com.androiddevelopers.villabuluyorum.view.host.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostReservationBinding
import com.androiddevelopers.villabuluyorum.viewmodel.host.reservation.HostReservationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostReservationFragment : Fragment() {
    private val viewModel: HostReservationViewModel by viewModels()
    private var _binding: FragmentHostReservationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}