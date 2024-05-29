package com.androiddevelopers.villabuluyorum.view.host.create

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaCreate3DetailBinding
import com.androiddevelopers.villabuluyorum.model.VillaPageArgumentsModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideHostBottomNavigation
import com.androiddevelopers.villabuluyorum.util.setupDialogs
import com.androiddevelopers.villabuluyorum.util.showHostBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.host.create.HostVillaCreateBaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@AndroidEntryPoint
class HostVillaCreate3DetailFragment : Fragment() {
    private val viewModel: HostVillaCreateBaseViewModel by viewModels()
    private var _binding: FragmentHostVillaCreate3DetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog

    private var villaId: String? = null
    private lateinit var villaFromArgs: Villa
    private lateinit var villaPageArgumentsModel: VillaPageArgumentsModel

    private var selectedHasPool: Boolean? = null
    private var selectedHasWifi: Boolean? = null
    private var selectedHasQuietArea: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: HostVillaCreate3DetailFragmentArgs by navArgs()
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
        _binding = FragmentHostVillaCreate3DetailBinding.inflate(
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
        setDropdownItems()


        //geri tuşuna basıldığında sonraki sayfadan gelen argümanı yakalıyoruz
        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<VillaPageArgumentsModel>("createVillaPageArgumentsToBack")
            ?.observe(viewLifecycleOwner) { data ->
                villaPageArgumentsModel = data
                viewModel.setCreateVillaPageArguments(villaPageArgumentsModel)
            }
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(binding) {
            with(viewModel) {
                liveDataFirebaseStatus.observe(owner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            val directions =
                                HostVillaCreate3DetailFragmentDirections.actionGlobalNavigationHostProfile()
                            Navigation.findNavController(binding.root)
                                .navigate(directions)
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
            }
        }
    }

    private fun setClickItems() {
        with(binding) {
            toolbarVillaCreate.setNavigationOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_hostVillaCreateDetailFragment_to_navigation_host_villa_create_enter)
                showHostBottomNavigation(requireActivity())
            }

            buttonNextVillaCreatePage3.setOnClickListener {
                villaPageArgumentsModel.villa = updateVilla(villaFromArgs)
                val directions =
                    HostVillaCreate3DetailFragmentDirections.actionHostVillaCreateDetailFragmentToHostVillaCreateFacilitiesFragment(
                        villaPageArgumentsModel
                    )
                Navigation.findNavController(it)
                    .navigate(directions)
            }
        }
    }

    private fun setDropdownItems() {
        with(binding) { // string-array olarak belirleridğimiz numara listesini databinding ile görünüme gönderiyoruz
            numberAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                resources.getStringArray(R.array.numbers)
            )

            chooseAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                resources.getStringArray(R.array.choose)
            )

            dropdownHasWifiVillaCreate.setOnItemClickListener { parent, view, position, id ->
                selectedHasWifi = position == 0
            }

            dropdownHasPoolVillaCreate.setOnItemClickListener { parent, view, position, id ->
                selectedHasPool = position == 0
            }

            dropdownQuietAreaVillaCreate.setOnItemClickListener { parent, view, position, id ->
                selectedHasQuietArea = position == 0
            }
        }
    }

    private fun updateVilla(villa: Villa): Villa {
        with(binding) {
            with(villa) {
                capacity = dropdownCapacityVillaCreate.text.toString()
                    .toIntOrNull() ?: 0
                bedroomCount = dropdownBedroomCountVillaCreate.text.toString()
                    .toIntOrNull() ?: 0
                bedCount = dropdownBedCountVillaCreate.text.toString()
                    .toIntOrNull() ?: 0
                bathroomCount = dropdownBathroomCountVillaCreate.text.toString()
                    .toIntOrNull() ?: 0
                restroom = dropdownRestroomCountVillaCreate.text.toString()
                    .toIntOrNull() ?: 0
                minStayDuration = dropdownMinStayDurationVillaCreate.text.toString()
                    .toIntOrNull() ?: 0
                hasWifi = selectedHasWifi
                hasPool = selectedHasPool
                isQuietArea = selectedHasQuietArea
                interiorDesign = editTextInteriorDesignVillaCreate.text.toString()
                gardenArea = editTextGardenAreaVillaCreate.text.toString()
                    .toDoubleOrNull() ?: 0.0
            }
        }

        return villa
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