package com.izmirsoftware.tatilci.view.host.create

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.izmirsoftware.tatilci.R
import com.izmirsoftware.tatilci.databinding.FragmentHostVillaCreate4FacilitiesBinding
import com.izmirsoftware.tatilci.model.VillaPageArgumentsModel
import com.izmirsoftware.tatilci.model.villa.Facilities
import com.izmirsoftware.tatilci.model.villa.Villa
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideHostBottomNavigation
import com.izmirsoftware.tatilci.util.setupDialogs
import com.izmirsoftware.tatilci.util.showHostBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.host.create.HostVillaCreateBaseViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostVillaCreate4FacilitiesFragment : Fragment() {
    private val viewModel: HostVillaCreateBaseViewModel by viewModels()
    private var _binding: FragmentHostVillaCreate4FacilitiesBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog
    private lateinit var villaFromArgs: Villa
    private lateinit var villaPageArgumentsModel: VillaPageArgumentsModel

    private var selectedCoverImage: Uri? = null
    private val selectedOtherImages = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: HostVillaCreate4FacilitiesFragmentArgs by navArgs()
        villaPageArgumentsModel = args.createVillaPageArguments
        viewModel.setCreateVillaPageArguments(villaPageArgumentsModel)

        //Telefon geri tuşunu dinliyoruz
        requireActivity().onBackPressedDispatcher.addCallback(this) { //geri tuşuna basıldığında önceki sayfaya villayıda gönderiyoruz
            val navController = findNavController()
            villaPageArgumentsModel.villa = updateVilla(villaFromArgs)
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
        _binding = FragmentHostVillaCreate4FacilitiesBinding.inflate(
            inflater,
            container,
            false
        )

        setClickItems()

        errorDialog = AlertDialog.Builder(requireContext())
            .create()
        setupDialogs(errorDialog)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        observeLiveData(viewLifecycleOwner)
        setViewChips()

        //TODO: Ödeme Tipi Ekle
    }

    private fun updateVilla(villa: Villa): Villa {
        villa.facilities = collectChips()
        return villa
    }


    private fun setClickItems() {
        with(binding) {
            toolbarVillaCreate.setNavigationOnClickListener {
                gotoHostHome()
            }

            buttonSaveVillaCreatePage4.setOnClickListener {
                viewModel.addImagesAndVillaToFirebase(
                    selectedCoverImage,
                    selectedOtherImages,
                    villaFromArgs
                )
            }
        }
    }

    private fun gotoHostHome() {
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_global_navigation_host_villa)
        showHostBottomNavigation(requireActivity())
    }

    private fun setViewChips() {
        with(binding) {
            with(resources) {
                getStringArray(R.array.landscape).forEach {
                    chipGroupLandscape.addView(setChip(it))
                }

                getStringArray(R.array.bath).forEach {
                    chipGroupBath.addView(setChip(it))
                }

                getStringArray(R.array.bedroom).forEach {
                    chipGroupBedroom.addView(setChip(it))
                }

                getStringArray(R.array.entertainment).forEach {
                    chipGroupEntertainment.addView(setChip(it))
                }

                getStringArray(R.array.heating_cooling).forEach {
                    chipGroupHeatingCooling.addView(setChip(it))
                }

                getStringArray(R.array.kitchen_food).forEach {
                    chipGroupKitchenFood.addView(setChip(it))
                }

                getStringArray(R.array.location_features).forEach {
                    chipGroupLocationFeatures.addView(setChip(it))
                }

                getStringArray(R.array.outdoor).forEach {
                    chipGroupOutdoor.addView(setChip(it))
                }

                getStringArray(R.array.services).forEach {
                    chipGroupServices.addView(setChip(it))
                }
            }
        }
    }

    private fun setChip(text: String): Chip {
        val chip = Chip(binding.root.context)
        chip.text = text
        chip.isCheckable = true
        return chip
    }

    private fun collectChips(): Facilities {
        val facilities = Facilities()

        with(binding) {
            with(chipGroupLandscape) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.landscape.add(chip.text.toString())
                }
            }
            with(chipGroupBath) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.bath.add(chip.text.toString())
                }
            }
            with(chipGroupBedroom) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.bedroom.add(chip.text.toString())
                }
            }
            with(chipGroupEntertainment) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.entertainment.add(chip.text.toString())
                }
            }
            with(chipGroupHeatingCooling) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.heatingCooling.add(chip.text.toString())
                }
            }
            with(chipGroupKitchenFood) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.kitchenFood.add(chip.text.toString())
                }
            }
            with(chipGroupLocationFeatures) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.locationFeatures.add(chip.text.toString())
                }
            }
            with(chipGroupOutdoor) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.outdoor.add(chip.text.toString())
                }
            }
            with(chipGroupServices) {
                val checkedChipIds = checkedChipIds
                for (id in checkedChipIds) {
                    val chip = findViewById<Chip>(id)
                    facilities.services.add(chip.text.toString())
                }
            }
        }

        return facilities
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(binding) {
            with(viewModel) {
                liveDataFirebaseStatus.observe(owner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            gotoHostHome()
                        }

                        Status.LOADING -> it.data?.let { status ->
                            setProgressBarVisibility = status
                        }

                        Status.ERROR   -> {
                            errorDialog.setMessage("Hata mesajı:\n${it.message}")
                            errorDialog.show()
                        }
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
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideHostBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}