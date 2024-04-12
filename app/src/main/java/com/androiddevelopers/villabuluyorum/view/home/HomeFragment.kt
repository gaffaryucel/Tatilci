package com.androiddevelopers.villabuluyorum.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.villabuluyorum.adapter.BestHouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentHomeBinding
import com.androiddevelopers.villabuluyorum.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val viewModel: HomeViewModel by viewModels()

    private lateinit var houseAdapter: HouseAdapter
    private lateinit var bestHouseAdapter: BestHouseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        houseAdapter = HouseAdapter()
        binding.rvCloseHomes.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,false)
        binding.rvCloseHomes.adapter = houseAdapter

        bestHouseAdapter = BestHouseAdapter()
        binding.rvBest.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBest.adapter = bestHouseAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}