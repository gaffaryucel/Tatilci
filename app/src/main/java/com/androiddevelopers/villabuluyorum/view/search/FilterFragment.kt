package com.androiddevelopers.villabuluyorum.view.search

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentFilterBinding
import com.androiddevelopers.villabuluyorum.viewmodel.serch.FilterViewModel
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider

class FilterFragment : Fragment() {

    private lateinit var viewModel: FilterViewModel
    private lateinit var _binding: FragmentFilterBinding
    private val binding get() = _binding!!

    private var minPrice : Float? = null
    private var maxPrice : Float? = null
    private var selectedBedRoomCount : String? = null
    private var selectedBedCount : String? = null
    private var selectedBathCount : String? = null
    private var selectedHouseType : String? = null
    private var isFavoriteSelected  = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(FilterViewModel::class.java)
        _binding = FragmentFilterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBindingItems()
    }
    private fun setupBindingItems(){
        setupLocationSelection()
        setupSlider()
        setBedRoomSelection()
        setupBadSelection()
        setupBathSelection()
        isFavoriteHousesSelected()
        changeSelectedHouseType()
        amenitiesSelection()
    }
    //Konum
    private fun setupLocationSelection(){
        binding.tvLocation.setOnClickListener {
            it.setBackgroundResource(R.drawable.selected_text_bg)
            binding.tvNewLocation.setBackgroundResource(R.drawable.selectable_text_bg)
        }
        binding.tvNewLocation.setOnClickListener {
            it.setBackgroundResource(R.drawable.selected_text_bg)
            binding.tvLocation.setBackgroundResource(R.drawable.selectable_text_bg)
        }
    }
    //Fiyat Aralığı
    private fun setupSlider(){
        minPrice = binding.slider.values[0]
        maxPrice = binding.slider.values[1]

        binding.slider.addOnChangeListener { _, _: Float, _ ->
            // Responds to when slider's value is changed
            minPrice = binding.slider.values[0]
            maxPrice = binding.slider.values[1]
        }
    }
    //Odalar
    private fun setBedRoomSelection(){
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
                changeBackgrounds(allViews,it)
                //Seçilen elemanın değerini gerekli değişkene atama
                selectedBedRoomCount = when (index) {
                    0 -> "Tümü"
                    else -> (index + 1).toString()
                }
            }
        }


    }
    private fun setupBadSelection(){
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
                changeBackgrounds(allViews,it)
                //Seçilen elemanın değerini gerekli değişkene atama
                selectedBedCount = when (index) {
                    0 -> "Tümü"
                    else -> (index + 1).toString()
                }
            }
        }
    }
    private fun setupBathSelection(){
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
                changeBackgrounds(allViews,it)
                //Seçilen elemanın değerini gerekli değişkene atama
                selectedBathCount = when (index) {
                    0 -> "Tümü"
                    else -> (index + 1).toString()
                }
            }
        }
    }
    private fun changeBackgrounds(allViews : List<View>,view : View){
        // Tüm elemanların arka planını seçilebilir olarak ayarla
        allViews.forEach {
            it.setBackgroundResource(R.drawable.selectable_text_bg)
        }
        // Seçilen elemanın arka planını seçili olarak ayarla
        view.setBackgroundResource(R.drawable.selected_text_bg)
    }

    private fun isFavoriteHousesSelected(){
        binding.layoutFavoriteHouse.setOnClickListener {
            if (isFavoriteSelected){
                it.setBackgroundResource(R.drawable.secondary_button_bg)
                isFavoriteSelected = false
            }else{
                it.setBackgroundResource(R.drawable.main_button_gb)
                isFavoriteSelected = true
            }
        }
    }
    //Konaklama Türü
    private fun changeSelectedHouseType(){
        val itemGuestHouse = binding.itemGuestHouse
        val itemHotel = binding.itemHotel
        val house = binding.house
        val itemApartment = binding.itemApartment

        val allViews = listOf(itemGuestHouse, itemHotel, house, itemApartment)

        allViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                allViews.forEach { it.setBackgroundResource(R.drawable.secondary_button_bg) }
                // Seçilen elemanın arka planını seçili olarak ayarla
                view.setBackgroundResource(R.drawable.main_button_gb)
                // Seçilen elemanın adını ekrana yazdır
                selectedHouseType = when (index) {
                    0 -> "item_guest_house"
                    1 -> "item_hotel"
                    2 -> "house"
                    else -> "item_apartment"
                }
            }
        }

    }

    //Olanakların seçilmesi

    private fun amenitiesSelection(){
        val layoutWifi = binding.layoutCbWifi
        val layoutKitchen = binding.layoutCbKitchen
        val layoutWashingMachine = binding.layoutCbWashingMachine
        val layout_cb_air = binding.layoutCbAir

        layoutWifi.setOnClickListener {
            val cbWifi = binding.cbWifi
            cbWifi.isChecked = !cbWifi.isChecked
        }

        layoutKitchen.setOnClickListener {
            val cbKitchen = binding.cbKitchen
            cbKitchen.isChecked = !cbKitchen.isChecked
        }
        layoutWashingMachine.setOnClickListener {
            val cbWashingMachine = binding.cbWashingMachine
            cbWashingMachine.isChecked = !cbWashingMachine.isChecked
        }
        layout_cb_air.setOnClickListener {
            val cbAir = binding.cbAir
            cbAir.isChecked = !cbAir.isChecked
        }

    }
}