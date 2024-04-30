package com.androiddevelopers.villabuluyorum.view.search

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.villabuluyorum.adapter.BestHouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.SearchAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentSearchBinding
import com.androiddevelopers.villabuluyorum.model.FilterModel
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.serch.SearchViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SearchViewModel
    private val searchAdapter = SearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivFilter.setOnClickListener {
            val action = SearchFragmentDirections.actionNavigationSearchToFilterFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearch.adapter = searchAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchInList(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Kullanıcı adına göre arama yap
                newText?.let {
                    viewModel.searchInList(it)
                }
                return true
            }
        })
        binding.ivRefresh.setOnClickListener {
            viewModel.getVillas(20)
        }
        binding.ivRefreshLayout.setOnClickListener {
            viewModel.getVillas(20)
        }
        getSharedPref()
    }

    private fun getSharedPref(){

        try {
            val sharedPreferences = requireContext().getSharedPreferences("FilterPref", Context.MODE_PRIVATE)

            // Verileri alın
            val city = sharedPreferences.getString("city", "")
            val maxPrice = sharedPreferences.getFloat("maxPrice", 0f)
            val minPrice = sharedPreferences.getFloat("minPrice", 0f)
            val bedrooms = sharedPreferences.getInt("bedrooms", 0)
            val beds = sharedPreferences.getInt("beds", 0)
            val bathrooms = sharedPreferences.getInt("bathrooms", 0)
            val isFavorite = sharedPreferences.getBoolean("isFavorite", false)
            val propertyType = sharedPreferences.getString("propertyType", "")
            val amenities = sharedPreferences.getStringSet("amenities", setOf())
// TODO: PropertyType sınıfının oluşturulmasından doğan hatalar giderilmeli SharedPref vs için yeni taktikler kullanılmalı

            val filter = createFilter(
                city,
                minPrice,
                maxPrice,
                bedrooms,
                beds,
                bathrooms,
                isFavorite,
                PropertyType.HOUSE,
                amenities?.toList(),
            )
            println(city)
            println(minPrice)
            println(maxPrice)
            println(bedrooms)
            println(beds)
            println(bathrooms)
            println(isFavorite)
            println(propertyType)
            println(amenities?.toList())
            if (filter.city.isNullOrEmpty()){
                return
            }else{
                viewModel.searchVillasByCity(filter,40)
                sharedPreferences.edit().clear().apply()
            }
        }catch (e : Exception){
            Toast.makeText(requireContext(), "Hata", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createFilter(
        city: String?,
        minPrice: Float?,
        maxPrice: Float?,
        bedrooms: Int?,
        beds: Int?,
        bathrooms: Int?,
        isFavorite: Boolean?,
        propertyType: PropertyType?,
        amenities: List<String>?
    ): FilterModel {
        return FilterModel(
            city ?: "İzmir",
            minPrice ?: 0F,
            maxPrice ?: 10000F,
            bedrooms ?: 4,
            beds ?: 4,
            bathrooms ?: 2,
            isFavorite ?: false,
            propertyType ?: PropertyType.HOUSE,
            null
        )
    }

    private fun observeLiveData() {
        viewModel.firebaseMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.rvSearch.visibility = View.VISIBLE
                    binding.pbSearch.visibility = View.GONE
                    binding.layoutEmptyList.visibility = View.GONE
                }

                Status.LOADING -> {
                    binding.pbSearch.visibility = View.VISIBLE
                    binding.layoutEmptyList.visibility = View.GONE
                }

                Status.ERROR -> {
                    println("error : "+it.message)
                    binding.pbSearch.visibility = View.GONE
                    binding.rvSearch.visibility = View.GONE
                    binding.layoutEmptyList.visibility = View.VISIBLE
                    binding.layoutRefresh.visibility = View.GONE
                }
            }
        })
        viewModel.searchResult.observe(viewLifecycleOwner, Observer { villas ->
            if (villas != null) {
                searchAdapter.villaList = villas
                binding.pbSearch.visibility = View.GONE
                binding.layoutRefresh.visibility = View.GONE
            }
        })
        viewModel.filterResult.observe(viewLifecycleOwner, Observer { villas ->
            if (!villas.isNullOrEmpty()) {
                searchAdapter.villaList = villas
                binding.pbSearch.visibility = View.GONE
                binding.layoutRefresh.visibility = View.VISIBLE
            }
        })

    }
    override fun onResume() {
        super.onResume()
        observeLiveData()
    }
}