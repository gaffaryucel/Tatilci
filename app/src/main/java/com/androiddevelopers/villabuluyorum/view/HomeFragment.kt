package com.androiddevelopers.villabuluyorum.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.BestHouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentHomeBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val viewModel: HomeViewModel by viewModels()

    private lateinit var closeVillasAdapter : HouseAdapter
    private lateinit var bestHouseAdapter: BestHouseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeVillasAdapter = HouseAdapter()
        binding.rvCloseHomes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        binding.rvCloseHomes.adapter = closeVillasAdapter

        bestHouseAdapter = BestHouseAdapter()
        binding.rvBest.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBest.adapter = bestHouseAdapter


        binding.searchView.setOnClickListener {
            search?.invoke("Home")
            println("click")
        }
        binding.sv.isClickable = false
        binding.sv.isFocusable = false
        binding.sv.isFocusableInTouchMode = false
        observeLiveData()

        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation) as NavHostFragment?
        val navControl = navHostFragment?.navController

        binding.searchView.setOnClickListener {
            navControl?.let {
                navControl.navigate(R.id.action_global_navigation_search)
            }
        }
    }
    private fun observeLiveData() {
        viewModel.firebaseMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbHome.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.pbHome.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.pbHome.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        })
        viewModel.bestVillas.observe(viewLifecycleOwner, Observer { villas ->
            if (villas != null) {
                bestHouseAdapter.villaList = villas
            }
        })
        viewModel.closeVillas.observe(viewLifecycleOwner, Observer { villas ->
            if (villas != null) {
                closeVillasAdapter.housesList = villas
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    var search: ((String) -> Unit)? = null

}