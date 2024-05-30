package com.androiddevelopers.villabuluyorum.view.host.create

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.ViewPagerAdapterForVillaCreate
import com.androiddevelopers.villabuluyorum.adapter.downloadImage
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaCreate2ImagesBinding
import com.androiddevelopers.villabuluyorum.databinding.MergeItemCoverImageOnlyTitleLocationBinding
import com.androiddevelopers.villabuluyorum.model.VillaPageArgumentsModel
import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.checkPermissionImageGallery
import com.androiddevelopers.villabuluyorum.util.getCurrentTime
import com.androiddevelopers.villabuluyorum.util.hideHostBottomNavigation
import com.androiddevelopers.villabuluyorum.util.setupDialogs
import com.androiddevelopers.villabuluyorum.viewmodel.host.create.HostVillaCreateBaseViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@AndroidEntryPoint
class HostVillaCreate2ImagesFragment : Fragment() {
    private val viewModel: HostVillaCreateBaseViewModel by viewModels()
    private var _binding: FragmentHostVillaCreate2ImagesBinding? = null
    private val binding get() = _binding!!

    private var _mergeBinding: MergeItemCoverImageOnlyTitleLocationBinding? = null
    private val mergeBinding get() = _mergeBinding!!

    private val provinceList = mutableListOf<Province>()
    private val districtList = mutableListOf<District>()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private var viewPagerAdapter = ViewPagerAdapterForVillaCreate()
    private var selectedCoverImage: Uri? = null
    private lateinit var coverImageLauncher: ActivityResultLauncher<Intent>
    private val selectedOtherImages = mutableListOf<Uri>()
    private lateinit var otherImageLauncher: ActivityResultLauncher<Intent>

    private val errorDialog: AlertDialog by lazy {
        AlertDialog.Builder(requireContext())
            .create()
    }

    private var selectedLongitude: Double? = null
    private var selectedLatitude: Double? = null

    private var villaId: String? = null
    private lateinit var villaFromArgs: Villa
    private lateinit var villaPageArgumentsModel: VillaPageArgumentsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: HostVillaCreate2ImagesFragmentArgs by navArgs()
        villaPageArgumentsModel = args.createVillaPageArguments
        viewModel.setCreateVillaPageArguments(villaPageArgumentsModel)

        //Telefon geri tuşunu dinliyoruz
        requireActivity().onBackPressedDispatcher.addCallback(this) { //geri tuşuna basıldığında önceki sayfaya villayıda gönderiyoruz
            val navController = findNavController()
            villaPageArgumentsModel.apply {
                villa = createVilla(villaFromArgs)
                coverImage = selectedCoverImage
                otherImages = selectedOtherImages.toList()
            }

            navController.previousBackStackEntry?.savedStateHandle?.set(
                "createVillaPageArgumentsToBack",
                villaPageArgumentsModel
            )
            navController.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaCreate2ImagesBinding.inflate(
            inflater,
            container,
            false
        )
        _mergeBinding = MergeItemCoverImageOnlyTitleLocationBinding.bind(binding.root)

        setDropdownItems()
        setClickItems()
        viewModel.getAllProvince()



        return binding.root
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        setEdittextListener()
        observeLiveData(viewLifecycleOwner)

        with(binding) {
            setProgressBarVisibility = false
            setImageCoverVisibility = false
            setViewPagerVisibility = false

            //viewpager adapter ve indicatoru set ediyoruz
            viewPagerVillaCreate.adapter = viewPagerAdapter
            indicatorVillaCreate.setViewPager(viewPagerVillaCreate)

            toolbarVillaCreate.setNavigationOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_hostVillaCreateFragment_to_navigation_host_villa_create_enter)
            }
        }

        viewModel.setImageUriList(selectedOtherImages.toList())

        setupLaunchers()

        viewPagerAdapter.listenerImages = { images ->
            viewModel.setImageUriList(images.toList())
        }

        //geri tuşuna basıldığında sonraki sayfadan gelen argümanı yakalıyoruz
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<VillaPageArgumentsModel>(
            "createVillaPageArgumentsToBack"
        )
            ?.observe(viewLifecycleOwner) { data ->
                villaPageArgumentsModel = data
                viewModel.setCreateVillaPageArguments(villaPageArgumentsModel)
            }
    }

    private fun createVilla(villa: Villa): Villa {
        if (villa.villaId == null) {
            villa.villaId = UUID.randomUUID()
                .toString()
            villa.time = getCurrentTime()
        }

        with(binding) {
            with(villa) {
                hostId = userId
                villaName = editTextTitleVillaCreate.text.toString()
                description = editTextDescriptionVillaCreate.text.toString()
                locationProvince = dropdownProvinceVillaCreate.text.toString()
                locationDistrict = dropdownDistrictVillaCreate.text.toString()
                locationNeighborhoodOrVillage =
                    dropdownNeighborhoodAndVillageVillaCreate.text.toString()
                locationAddress = editTextAddressVillaCreate.text.toString()
                nightlyRate = editTextNightlyRateVillaCreate.text.toString()
                    .toDoubleOrNull() ?: 0.0
                longitude = selectedLongitude
                latitude = selectedLatitude
            }
        }

        return villa
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(binding) {
            with(viewModel) {
                liveDataFirebaseStatus.observe(owner) {
                    when (it.status) {
                        Status.SUCCESS -> {}

                        Status.LOADING -> it.data?.let { status ->
                            setProgressBarVisibility = status
                        }

                        Status.ERROR   -> {
                            setupDialogs(errorDialog)
                            errorDialog.setMessage("Hata mesajı:\n${it.message}")
                            errorDialog.show()
                        }
                    }

                }

                liveDataProvinceFromRoom.observe(owner) {
                    provinceList.clear()
                    provinceList.addAll(it.toList())

                    val listName = it.map { province -> province.name.toString() }

                    dropdownProvinceVillaCreate.setAdapter(
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1,
                            listName.toList()
                        )
                    )
                }

                liveDataDistrictFromRoom.observe(owner) {

                    districtList.clear()
                    districtList.addAll(it.toList())

                    val listName = it.map { district -> district.name.toString() }

                    dropdownDistrictVillaCreate.setAdapter(
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            android.R.id.text1,
                            listName.toList()
                        )
                    )

                }

                liveDataNeighborhoodFromRoom.observe(owner) { neighborhoods ->
                    val listName: MutableList<String> = mutableListOf()
                    listName.addAll(neighborhoods.map { neighborhood -> neighborhood.name.toString() })

                    liveDataVillageFromRoom.observe(owner) { villages ->
                        listName.addAll(villages.map { village -> village.name.toString() })

                        dropdownNeighborhoodAndVillageVillaCreate.setAdapter(
                            ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                android.R.id.text1,
                                listName.toList()
                            )
                        )
                    }
                }

                liveDataVilla.observe(owner) { villa ->
                    villaFromArgs = villa
                }

                liveDataImageCover.observe(owner) { image ->
                    selectedCoverImage = image
                }

                liveDataImageUriList.observe(owner) { images ->
                    selectedOtherImages.clear()
                    selectedOtherImages.addAll(images.toList())
                    viewPagerAdapter.refreshList(images.toList())
                    with(binding) { //indicatoru viewpager yeni liste ile set ediyoruz
                        indicatorVillaCreate.setViewPager(viewPagerVillaCreate)
                    }
                }

                imageSize.observe(owner) { //seçilen resim olmadığında viewpager 'ı gizleyip boş bir resim gösteriyoruz
                    //resim seçildiğinde işlemi tersine alıyoruz
                    with(binding) {
                        setViewPagerVisibility = !(it == 0 || it == null)
                    }
                }
            }
        }

    }

    private fun setDropdownItems() {
        with(binding) { // seçtiğimiz illeri yakalıyoruz
            dropdownProvinceVillaCreate.setOnItemClickListener { parent, view, position, id -> // il değiştiğinde ilçe ve mahalle/köy içeriğini temizliyoruz
                dropdownDistrictVillaCreate.text = null
                dropdownNeighborhoodAndVillageVillaCreate.text = null

                //seçtiğimizz ilin id si ile ilçeleri getiriyoruz
                provinceList[position].id?.let { provinceId ->
                    viewModel.getAllDistrictById(
                        provinceId
                    )
                }
            }

            // seçtiğimiz ilçeleri yakalıyoruz
            dropdownDistrictVillaCreate.setOnItemClickListener { parent, view, position, id -> // ilçe değiştiğinde mahalle/köy içeriğini temizliyoruz
                dropdownNeighborhoodAndVillageVillaCreate.text = null

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

    private fun setClickItems() {
        with(binding) {
            buttonNextVillaCreatePage2.setOnClickListener {
                villaPageArgumentsModel.coverImage = selectedCoverImage
                villaPageArgumentsModel.otherImages = selectedOtherImages.toList()
                villaPageArgumentsModel.villa = createVilla(villaFromArgs)

                val directions =
                    HostVillaCreate2ImagesFragmentDirections.actionHostVillaCreateFragmentToHostVillaCreateDetailFragment(
                        villaPageArgumentsModel
                    )
                Navigation.findNavController(it)
                    .navigate(directions)
            }

            textAddImageCover.setOnClickListener {
                if (checkPermissionImageGallery(
                        requireActivity(),
                        800
                    )
                ) {
                    openCoverImagePicker()
                }
            }

            buttonImageCoverEditVillaCreate.setOnClickListener {
                if (checkPermissionImageGallery(
                        requireActivity(),
                        800
                    )
                ) {
                    openCoverImagePicker()
                }
            }

            textAddMoreImage.setOnClickListener {
                if (checkPermissionImageGallery(
                        requireActivity(),
                        800
                    )
                ) {
                    openOtherImagePicker()
                }
            }

            fabButtonMoreAddImageVillaCreate.setOnClickListener {
                if (checkPermissionImageGallery(
                        requireActivity(),
                        800
                    )
                ) {
                    openOtherImagePicker()
                }
            }
        }
    }

    private fun setupLaunchers() {
        coverImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { image ->
                        selectedCoverImage = image
                        selectedCoverImage?.let {
                            binding.setImageCoverVisibility = true
                            downloadImage(
                                mergeBinding.imageTitle,
                                image.toString()
                            )
                        }
                    }
                }
            }

        otherImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.clipData?.let { data ->
                        val size = data.itemCount
                        for (i in 0..<size) {
                            val image = data.getItemAt(i).uri
                            selectedOtherImages.add(image)
                        }
                        viewModel.setImageUriList(selectedOtherImages.toList())
                    }
                }
            }
    }

    private fun openCoverImagePicker() {
        val imageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        coverImageLauncher.launch(imageIntent)
    }

    private fun openOtherImagePicker() {
        val imageIntent = Intent()
        imageIntent.setType("image/*")
        imageIntent.putExtra(
            Intent.EXTRA_ALLOW_MULTIPLE,
            true
        )
        imageIntent.setAction(Intent.ACTION_GET_CONTENT)

        otherImageLauncher.launch(
            Intent.createChooser(
                imageIntent,
                "Resimleri seçin"
            )
        )
    }

    private fun setEdittextListener() {
        var province = ""
        var district = ""
        var neighborhoodOrVillage: String

        with(binding) {
            editTextTitleVillaCreate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    mergeBinding.textDetailTitle.text = s
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            dropdownProvinceVillaCreate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    province = s.toString()

                    getGeocoderLocation(
                        province,
                        binding.root
                    )

                    mergeBinding.textDetailAddress.text = s
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            dropdownDistrictVillaCreate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    district = s.toString()
                    if (!s.isNullOrBlank()) {
                        val address = buildString {
                            append(district)
                            append(", ")
                            append(province)
                        }

                        mergeBinding.textDetailAddress.text = address

                        getGeocoderLocation(
                            address,
                            binding.root
                        )
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

            dropdownNeighborhoodAndVillageVillaCreate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    neighborhoodOrVillage = s.toString()
                    if (!s.isNullOrBlank()) {
                        val address = buildString {
                            append(neighborhoodOrVillage)
                            append(", ")
                            append(district)
                            append(", ")
                            append(province)
                        }

                        mergeBinding.textDetailAddress.text = address

                        getGeocoderLocation(
                            address,
                            binding.root
                        )
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }

    // seçilen adres bilgisinden koordinat bilgisini almak için kullanılan metod
    private fun getGeocoderLocation(
        address: String,
        view: View,
    ) {
        val geocoder = Geocoder(
            view.context,
            Locale.getDefault()
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(
                    address,
                    1
                ) {
                    val data = it[0]
                    selectedLatitude = data.latitude
                    selectedLongitude = data.longitude
                }
            } else {
                lifecycleScope.launch {
                    @Suppress("DEPRECATION") geocoder.getFromLocationName(
                        address,
                        1
                    )
                        ?.let {
                            val data = it[0]
                            selectedLatitude = data.latitude
                            selectedLongitude = data.longitude
                        }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                view.context,
                e.message,
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        hideHostBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _mergeBinding = null
    }

}