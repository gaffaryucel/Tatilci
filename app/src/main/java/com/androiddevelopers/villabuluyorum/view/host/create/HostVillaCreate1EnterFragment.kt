package com.androiddevelopers.villabuluyorum.view.host.create

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaCreate1EnterBinding
import com.androiddevelopers.villabuluyorum.model.CreateVillaPageArguments
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideHostBottomNavigation
import com.androiddevelopers.villabuluyorum.util.setupDialogs
import com.androiddevelopers.villabuluyorum.util.showHostBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.host.create.HostVillaCreateBaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HostVillaCreate1EnterFragment : Fragment() {
    private val viewModel: HostVillaCreateBaseViewModel by viewModels()
    private var _binding: FragmentHostVillaCreate1EnterBinding? = null
    private val binding get() = _binding!!
    private var selectedPropertyStatus = false

    private var selectedPropertyType = PropertyType.HOUSE
    private val errorDialog: AlertDialog by lazy {
        AlertDialog
            .Builder(requireContext())
            .create()
    }

    private var villaId: String? = null
    private lateinit var villaFromArgs: Villa
    private lateinit var createVillaPageArguments: CreateVillaPageArguments

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: HostVillaCreate1EnterFragmentArgs by navArgs()
        createVillaPageArguments = args.createVillaPageArguments
        viewModel.setCreateVillaPageArguments(createVillaPageArguments)

        villaId = args.villaId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaCreate1EnterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickItems()
        observeLiveData(viewLifecycleOwner)

        //TODO: İlk sayfaya ev tipi ekle


    }

    private fun setClickItems() {
        with(binding) {
            toolbarVillaCreate.setNavigationOnClickListener {
                Navigation
                    .findNavController(binding.root)
                    .popBackStack()
            }

            buttonNextVillaCreatePage1.setOnClickListener {
                val directions =
                    HostVillaCreate1EnterFragmentDirections.actionNavigationHostVillaCreateEnterToHostVillaCreateFragment(
                        createVillaPageArguments, villaId
                    )
                Navigation
                    .findNavController(it)
                    .navigate(directions)
            }


            val propertyStatus = listOf(
                cardRentHostVillaCreateEnter,
                cardSaleHostVillaCreateEnter,
            )

            propertyStatus.forEachIndexed { index, cardView ->
                cardView.setOnClickListener {

                    // Tüm elemanların arka planını boş olarak ayarla
                    propertyStatus.forEach {
                        it.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.host_cardview_background
                            )
                        )
                    }

                    // Seçilen elemanın arka planını seçili olarak ayarla
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.md_theme_primary
                        )
                    )

                    //Seçilen durumu ana değişkene atıyoruız
                    selectedPropertyStatus = when (index) {
                        0    -> false
                        1    -> true
                        else -> false
                    }
                }

            }

            val propertyTypes = listOf(
                cardHouseHostVillaCreateEnter,
                cardApartmentHostVillaCreateEnter,
                cardGuestHouseHostVillaCreateEnter,
                cardHotelHostVillaCreateEnter
            )

            propertyTypes.forEachIndexed { index, cardView ->
                cardView.setOnClickListener {

                    // Tüm elemanların arka planını boş olarak ayarla
                    propertyTypes.forEach {
                        it.setCardBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(), R.color.host_cardview_background
                            )
                        )
                    }
                    // Seçilen elemanın arka planını seçili olarak ayarla
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.md_theme_primary
                        )
                    )

                    //Seçilen tipi ana değişkene atıyoruız
                    selectedPropertyType = when (index) {
                        0    -> PropertyType.HOUSE
                        1    -> PropertyType.APARTMENT
                        2    -> PropertyType.GUEST_HOUSE
                        3    -> PropertyType.HOTEL
                        else -> PropertyType.HOUSE
                    }
                }

            }
        }
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

                liveDataVilla.observe(owner) { villa ->
                    villaFromArgs = villa
                }
            }
        }
    }

    private fun createVillaPageArguments(): CreateVillaPageArguments {
        return CreateVillaPageArguments(
            coverImage = null, otherImages = mutableListOf(), villa = createVilla(villaFromArgs)
        )
    }

    private fun createVilla(villa: Villa): Villa {
        if (villa.villaId == null) {
            villa.villaId = UUID
                .randomUUID()
                .toString()
        }

        villa.apply {
            isForSale = selectedPropertyStatus
            propertyType = selectedPropertyType
        }

        return villa
    }

    override fun onResume() {
        super.onResume()
        hideHostBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showHostBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}