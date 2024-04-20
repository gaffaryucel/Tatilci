package com.androiddevelopers.villabuluyorum.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.BestHouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentHomeBinding
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.provider.Settings
import com.androiddevelopers.villabuluyorum.adapter.MyLocation


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val viewModel: HomeViewModel by viewModels()

    private val bestHouseAdapter: BestHouseAdapter = BestHouseAdapter()

    private val provinceList = mutableListOf<Province>()

    private lateinit var location : MyLocation


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

        binding.rvCloseHomes.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvBest.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBest.adapter = bestHouseAdapter


        binding.sv.isClickable = false
        binding.sv.isFocusable = false
        binding.sv.isFocusableInTouchMode = false


        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation) as NavHostFragment?
        val navControl = navHostFragment?.navController

        binding.searchView.setOnClickListener {
            navControl?.let {
                navControl.navigate(R.id.action_global_navigation_search)
            }
        }

        binding.dropDownCity.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = binding.dropDownCity.adapter.getItem(position).toString()
            viewModel.getCloseVillas(selectedCity,20)
        }

    }

    override fun onResume() {
        super.onResume()
        binding.pbHome.visibility = View.VISIBLE
        observeLiveData()
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
                binding.pbHome.visibility = View.GONE
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                location = MyLocation(
                    user.latitude ?: 41.00527,
                    user.longitude ?: 28.97696
                )
                viewModel.closeVillas.observe(viewLifecycleOwner, Observer { villas ->
                    if (villas != null) {
                        val closeVillasAdapter = HouseAdapter(location)
                        binding.rvCloseHomes.adapter = closeVillasAdapter
                        closeVillasAdapter.housesList = villas
                        binding.pbHome.visibility = View.GONE
                    }
                })
            }
        })
        viewModel.liveDataProvinceFromRoom.observe(viewLifecycleOwner) {
            provinceList.clear()
            provinceList.addAll(it.toList())

            val listName = it.map { province -> province.name.toString() }

            binding.dropDownCity.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listName.toList()
                )
            )
        }
    }

    override fun onPause() {
        super.onPause()
        bestHouseAdapter.villaList = listOf()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    getLastKnownLocation
        val tokatCoordinates = findCityCoordinates("istanbul")
        if (tokatCoordinates != null) {
            println("Tokat şehrinin koordinatları: ${tokatCoordinates.first}, ${tokatCoordinates.second}")
        } else {
            println("Tokat şehrinin koordinatları bulunamadı.")
        }
         */
    /*
    fun findCityCoordinates(city : String): Pair<Double, Double>? {
        val cityList = resources.getStringArray(R.array.city_list)
        for (cityInfo in cityList) {
            val cityData = cityInfo.split(",")
            val cityName = cityData[0].trim()
            if (cityName.equals(city, ignoreCase = true) && cityData.size >= 3) {
                val latitude = cityData[1].trim().toDoubleOrNull()
                val longitude = cityData[2].trim().toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    return Pair(latitude, longitude)
                }
            }
        }
        return null
    }
*/
}