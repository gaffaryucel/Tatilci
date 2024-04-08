package com.androiddevelopers.villabuluyorum.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentHomeCreateFacilitiesBinding
import com.androiddevelopers.villabuluyorum.viewmodel.home.HomeCreateFacilitiesViewModel

class HomeCreateFacilitiesFragment : Fragment() {
    private val viewModel: HomeCreateFacilitiesViewModel by viewModels()
    private var _binding: FragmentHomeCreateFacilitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeCreateFacilitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}