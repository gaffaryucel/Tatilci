package com.androiddevelopers.villabuluyorum.view.host.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostNotificationBinding
import com.androiddevelopers.villabuluyorum.viewmodel.host.notification.HostNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HostNotificationFragment : Fragment() {
    private val viewModel: HostNotificationViewModel by viewModels()
    private var _binding: FragmentHostNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}