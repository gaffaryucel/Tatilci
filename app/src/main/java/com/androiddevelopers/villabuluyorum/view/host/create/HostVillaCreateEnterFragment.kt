package com.androiddevelopers.villabuluyorum.view.host.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaCreateEnterBinding
import com.androiddevelopers.villabuluyorum.viewmodel.host.create.HostVillaCreateEnterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostVillaCreateEnterFragment : Fragment() {
    private val viewModel: HostVillaCreateEnterViewModel by viewModels()
    private var _binding: FragmentHostVillaCreateEnterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaCreateEnterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}