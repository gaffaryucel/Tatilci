package com.androiddevelopers.villabuluyorum.view.host.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaCreateDetailBinding
import com.androiddevelopers.villabuluyorum.viewmodel.host.create.HostVillaCreateDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostVillaCreateDetailFragment : Fragment() {
    private val viewModel: HostVillaCreateDetailViewModel by viewModels()
    private var _binding: FragmentHostVillaCreateDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaCreateDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}