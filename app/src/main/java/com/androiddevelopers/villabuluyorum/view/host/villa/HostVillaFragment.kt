package com.androiddevelopers.villabuluyorum.view.host.villa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaBinding
import com.androiddevelopers.villabuluyorum.viewmodel.host.villa.HostVillaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostVillaFragment : Fragment() {
    private val viewModel: HostVillaViewModel by viewModels()
    private var _binding: FragmentHostVillaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}