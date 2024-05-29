package com.androiddevelopers.villabuluyorum.view.user.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentFilterBinding
import com.androiddevelopers.villabuluyorum.model.FilterModel
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.user.serch.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : Fragment() {

    private var filter = FilterModel()

    private lateinit var viewModel: FilterViewModel
    private lateinit var _binding: FragmentFilterBinding
    private val binding get() = _binding

    private val provinceList = mutableListOf<Province>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[FilterViewModel::class.java]
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBindingItems()
    }

    private fun setupBindingItems() {
        setupLocationSelection()
        setupSlider()
        setBedRoomSelection()
        setupBadSelection()
        setupBathSelection()
        isFavoriteHousesSelected()
        changeSelectedHouseType()
        amenitiesSelection()
        binding.btnFilterAndSearch.setOnClickListener {
            saveAAndExit()
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        observeLiveData()
    }

    //Konum
    private fun setupLocationSelection() {
        filter.city = "İzmir"

        binding.tvLocation.setOnClickListener {
            it.setBackgroundResource(R.drawable.selected_text_bg)
            binding.tvAllCities.setBackgroundResource(R.drawable.selectable_text_bg)
            filter.city = "İzmir"
        }
        binding.tvAllCities.setOnClickListener {
            it.setBackgroundResource(R.drawable.selected_text_bg)
            binding.tvLocation.setBackgroundResource(R.drawable.selectable_text_bg)
            filter.city = "Hepsi"
        }
        binding.tvNewLocation.setOnClickListener {
            binding.layoutCitySelection.visibility = View.GONE
            binding.layoutCity.visibility = View.VISIBLE
        }
        binding.dropDownCity.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = binding.dropDownCity.adapter.getItem(position).toString()
            filter.city = selectedCity
            binding.layoutCitySelection.visibility = View.VISIBLE
            binding.layoutCity.visibility = View.GONE
            binding.tvLocation.setBackgroundResource(R.drawable.selected_text_bg)
            binding.tvAllCities.setBackgroundResource(R.drawable.selectable_text_bg)
            binding.tvLocation.text = selectedCity
        }
    }

    //Fiyat Aralığı
    private fun setupSlider() {
        filter.maxPrice = 10000F
        filter.minPrice = 0F
        binding.slider.addOnChangeListener { rangeSlider, value, fromUser ->
            filter.maxPrice = rangeSlider.values[1]
            filter.minPrice = rangeSlider.values[0]
        }
    }

    //Odalar
    private fun setBedRoomSelection() {
        val allViews = listOf(
            binding.bedRoomAll,
            binding.bedRoom1,
            binding.bedRoom2,
            binding.bedRoom3,
            binding.bedRoom4,
            binding.bedRoom5
        )

        allViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                changeBackgrounds(allViews, it)

                //Seçilen elemanın değerini gerekli değişkene atama
                filter.bedrooms = when (index) {
                    0 -> 99
                    else -> index
                }
            }
        }


    }

    private fun setupBadSelection() {
        val allViews = listOf(
            binding.bedAll,
            binding.bed1,
            binding.bed2,
            binding.bed3,
            binding.bed4,
            binding.bed5
        )

        allViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                //Arka plan değiştirme
                changeBackgrounds(allViews, it)
                //Seçilen elemanın değerini g3erekli değişkene atama
                filter.beds = when (index) {
                    0 -> 99
                    else -> index
                }
            }
        }
    }

    private fun setupBathSelection() {
        val allViews = listOf(
            binding.bathAll,
            binding.bath1,
            binding.bath2,
            binding.bath3,
            binding.bath4,
            binding.bath5
        )

        allViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                changeBackgrounds(allViews, it)
                //Seçilen elemanın değerini gerekli değişkene atama
                filter.bathrooms = when (index) {
                    0 -> 99
                    else -> index
                }
            }
        }
    }

    private fun changeBackgrounds(allViews: List<View>, view: View) {
        // Tüm elemanların arka planını seçilebilir olarak ayarla
        allViews.forEach {
            it.setBackgroundResource(R.drawable.selectable_text_bg)
        }
        // Seçilen elemanın arka planını seçili olarak ayarla
        view.setBackgroundResource(R.drawable.selected_text_bg)
    }

    private fun isFavoriteHousesSelected() {
        filter.isForSale = false

        binding.layoutFavoriteHouse.setOnClickListener {
            if (filter.isForSale == true) {
                it.setBackgroundResource(R.drawable.secondary_button_bg)
                filter.isForSale = false
            } else {
                it.setBackgroundResource(R.drawable.main_button_gb)
                filter.isForSale = true
            }
        }
    }

    //Konaklama Türü
    private fun changeSelectedHouseType() {
        val itemGuestHouse = binding.itemGuestHouse
        val itemHotel = binding.itemHotel
        val itemHouse = binding.house
        val itemApartment = binding.itemApartment

        val allViews = listOf(itemHouse, itemApartment, itemGuestHouse, itemHotel)

        allViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                allViews.forEach { it.setBackgroundResource(R.drawable.secondary_button_bg) }
                // Seçilen elemanın arka planını seçili olarak ayarla
                view.setBackgroundResource(R.drawable.main_button_gb)
                // Seçilen elemanın adını ekrana yazdır
                filter.propertyType = when (index) {
                    0 -> PropertyType.HOUSE
                    1 -> PropertyType.APARTMENT
                    2 -> PropertyType.GUEST_HOUSE
                    3 -> PropertyType.HOTEL
                    else -> PropertyType.HOUSE
                }
            }
        }

    }

    //Olanakların seçilmesi
    private fun amenitiesSelection() {
        val layoutWifi = binding.layoutCbWifi
        val layoutKitchen = binding.layoutCbPool
        val layoutWashingMachine = binding.layoutCbQuite
        layoutWifi.setOnClickListener {
            val cbWifi = binding.cbWifi
            cbWifi.isChecked = !cbWifi.isChecked.also {
                filter.hasWifi = !it
                println("clickc : "+!it)
            }
        }

        layoutKitchen.setOnClickListener {
            val cbPool = binding.cbPool
            cbPool.isChecked = !cbPool.isChecked.also {
                filter.hasPool = !it
            }
        }
        layoutWashingMachine.setOnClickListener {
            val cbWashingMachine = binding.cbQuite
            cbWashingMachine.isChecked = !cbWashingMachine.isChecked.also {
                filter.quitePlace = !it
            }

        }

    }

    private fun saveAAndExit() {
        try {
            val sharedPreferences =
                requireContext().getSharedPreferences("FilterPref", Context.MODE_PRIVATE)

            // FilterModel'deki tüm değişkenleri kaydet
            val editor = sharedPreferences.edit()
            editor.putString("city", filter.city)
            editor.putFloat("minPrice", filter.maxPrice ?: 0F)
            editor.putFloat("maxPrice", filter.minPrice ?: 10000F)
            editor.putInt("bedrooms", filter.bedrooms ?: 0)
            editor.putInt("beds", filter.beds ?: 0)
            editor.putInt("bathrooms", filter.bathrooms ?: 0)
            editor.putString("propertyType", filter.propertyType.toString())
            editor.putBoolean("wifi", filter.hasWifi ?: false)
            editor.putBoolean("pool", filter.hasPool ?: false)
            editor.putBoolean("quite", filter.quitePlace ?: false)
            editor.putBoolean("sale", filter.isForSale ?: false)

            editor.apply().also {
                findNavController().popBackStack()
            }
        } catch (e: Exception) {
            println("error : " + e.localizedMessage)
        }
    }

    private fun observeLiveData() {
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

    override fun onStop() {
        super.onStop()
        showBottomNavigation(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }
}