package com.androiddevelopers.villabuluyorum.view.host.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostMessageBinding
import com.androiddevelopers.villabuluyorum.viewmodel.host.message.HostMessageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostMessageFragment : Fragment() {
    private val viewModel: HostMessageViewModel by viewModels()
    private var _binding: FragmentHostMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}