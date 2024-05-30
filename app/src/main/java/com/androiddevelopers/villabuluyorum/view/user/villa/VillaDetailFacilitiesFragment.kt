package com.androiddevelopers.villabuluyorum.view.user.villa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentVillaDetailFacilitiesBinding
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.user.villa.VillaDetailFacilitiesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VillaDetailFacilitiesFragment : Fragment() {
    private val viewModel: VillaDetailFacilitiesViewModel by viewModels()
    private var _binding: FragmentVillaDetailFacilitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Detay sayfasından olanaklara yönlendir
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVillaDetailFacilitiesBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}