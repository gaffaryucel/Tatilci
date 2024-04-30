package com.androiddevelopers.villabuluyorum.view.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentEditAddressBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentEditProfileDetailsBinding
import com.androiddevelopers.villabuluyorum.model.UserAddress
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.viewmodel.profile.EditAddressViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.profile.EditProfileDetailsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditAddressFragment : Fragment() {


    private var _binding: FragmentEditAddressBinding? = null
    private val binding get() = _binding!!

    val viewModel: EditAddressViewModel by viewModels()

    private var progressDialog: ProgressDialog? = null


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val PREFS_FILENAME = "permission"

    private val KEY_VALUE = "location"


    private val provinceList = mutableListOf<Province>()
    private val districtList = mutableListOf<District>()
    private val userAddress = UserAddress()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditAddressBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()

        progressDialog = ProgressDialog(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        setDropdownItems()

        binding.btnSave.setOnClickListener {
            saveAddress()
        }

        //requestGPSPermission(requireContext())

        /*
         district = s.toString()
         if (!s.isNullOrBlank()) {
             val address = buildString {
                 append(district)
                 append(", ")
                 append(province)
             }

             mergeBinding.textDetailAddress.text = address

             getGeocoderLocation(address, binding.root)
         */
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    private fun observeLiveData() {
        viewModel.uploadMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog?.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Konum Güncellendi",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }

                Status.LOADING -> {
                    startLoadingProcess(progressDialog)
                }

                Status.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog?.dismiss()
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            with(binding){
                val address = it.address
                if (address != null){
                    if(!address.province.isNullOrEmpty()){
                        dropdownProvince.setText(address.province)
                    }
                    if(!address.district.isNullOrEmpty()){
                        dropdownDistrict.setText(address.district)
                    }
                    if(!address.neighborhood.isNullOrEmpty()){
                        dropdownNeighborhoodAndVillage.setText(address.neighborhood)
                    }
                    if(!address.address.isNullOrEmpty()){
                        editTextAddress.setText(address.address)
                    }
                }
                observeRoomLiveData()

            }
        })
    }
    private fun observeRoomLiveData(){
        with(binding) {
            with(viewModel) {
                liveDataProvinceFromRoom.observe(viewLifecycleOwner) {
                    provinceList.clear()
                    provinceList.addAll(it.toList())

                    val listName = it.map { province -> province.name.toString() }

                    dropdownProvince.setAdapter(
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1,
                            listName.toList()
                        )
                    )
                }

                liveDataDistrictFromRoom.observe(viewLifecycleOwner) {

                    districtList.clear()
                    districtList.addAll(it.toList())

                    val listName = it.map { district -> district.name.toString() }

                    dropdownDistrict.setAdapter(
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1,
                            listName.toList()
                        )
                    )

                }

                liveDataNeighborhoodFromRoom.observe(viewLifecycleOwner) { neighborhoods ->
                    val listName: MutableList<String> = mutableListOf()
                    listName.addAll(neighborhoods.map { neighborhood -> neighborhood.name.toString() })

                    liveDataVillageFromRoom.observe(viewLifecycleOwner) { villages ->
                        listName.addAll(villages.map { village -> village.name.toString() })

                        dropdownNeighborhoodAndVillage.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                android.R.id.text1,
                                listName.toList()
                            )
                        )
                    }
                }
            }
        }
    }
    private fun setDropdownItems() {
        with(binding) {
            // seçtiğimiz illeri yakalıyoruz
            dropdownProvince.setOnItemClickListener { parent, view, position, id ->
                // il değiştiğinde ilçe ve mahalle/köy içeriğini temizliyoruz
                dropdownDistrict.text = null
                dropdownNeighborhoodAndVillage.text = null
                //seçtiğimizz ilin id si ile ilçeleri getiriyoruz
                provinceList[position].id?.let { provinceId ->
                    viewModel.getAllDistrictById(
                        provinceId
                    )
                }
            }

            // seçtiğimiz ilçeleri yakalıyoruz
            dropdownDistrict.setOnItemClickListener { parent, view, position, id ->
                // ilçe değiştiğinde mahalle/köy içeriğini temizliyoruz
                dropdownNeighborhoodAndVillage.text = null

                // seçtiğimiz ilçenmin id si ile mahalle/köy leri getiriyoruz
                districtList[position].id?.let { districtId ->
                    viewModel.getAllNeighborhoodById(
                        districtId
                    )
                    viewModel.getAllVillageById(
                        districtId
                    )
                }
            }
        }
    }
    private fun saveAddress(){
        with(binding){
            val province = dropdownProvince.text.toString()
            val district = dropdownDistrict.text.toString()
            val neighborhood = dropdownNeighborhoodAndVillage.text.toString()
            val address = editTextAddress.text.toString()
            userAddress.province = province
            userAddress.district = district
            userAddress.neighborhood = neighborhood
            userAddress.address = address
            if (province.isNullOrBlank() || district.isNullOrBlank() || neighborhood.isNullOrBlank() || address.isNullOrBlank()) {
                showAlertDialog()
            }else{
                val map = HashMap<String, Any?>()
                map["address"] = userAddress
                viewModel.updateUserData(map)
            }
        }
    }

    private fun showAlertDialog() {
        // Boş bir alan varsa uyarı göster
        AlertDialog.Builder(context)
            .setTitle("Eksik Bilgi!")
            .setMessage("Bazı bilgiler eksik, devam etmek istediğinizden emin misiniz?")
            .setPositiveButton("Devam et") { dialog, _ ->
                dialog.dismiss()
                val map = HashMap<String, Any?>()
                map["userAddress"] = userAddress
                viewModel.updateUserData(map)
            }.setNegativeButton("Vazgeç"){dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
