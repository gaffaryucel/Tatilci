package com.izmirsoftware.tatilci.view.host.create

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.izmirsoftware.tatilci.R
import com.izmirsoftware.tatilci.databinding.FragmentHostVillaCreate1EnterBinding
import com.izmirsoftware.tatilci.model.PropertyType
import com.izmirsoftware.tatilci.model.VillaPageArgumentsModel
import com.izmirsoftware.tatilci.model.villa.Villa
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideHostBottomNavigation
import com.izmirsoftware.tatilci.util.setupDialogs
import com.izmirsoftware.tatilci.util.showHostBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.host.create.HostVillaCreateBaseViewModel
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
        AlertDialog.Builder(requireContext())
            .create()
    }

    private var villaId: String? = null
    private var villaFromArgs = Villa()
    private lateinit var villaPageArgumentsModel: VillaPageArgumentsModel

    private lateinit var propertyStatus: List<CardView>
    private lateinit var propertyTypes: List<CardView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: HostVillaCreate1EnterFragmentArgs by navArgs()
        villaPageArgumentsModel = args.createVillaPageArguments
        viewModel.setCreateVillaPageArguments(villaPageArgumentsModel)

        villaId = args.villaId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaCreate1EnterBinding.inflate(
            inflater,
            container,
            false
        )

        with(binding) {
            villaId?.let { id ->
                setVillaId = id
                viewModel.getVillaByIdFromFirestore(id)
            }

            propertyStatus = listOf(
                cardRentHostVillaCreateEnter,
                cardSaleHostVillaCreateEnter,
            )

            propertyTypes = listOf(
                cardHouseHostVillaCreateEnter,
                cardApartmentHostVillaCreateEnter,
                cardGuestHouseHostVillaCreateEnter,
                cardHotelHostVillaCreateEnter
            )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        setClickItems()
        observeLiveData(viewLifecycleOwner)
    }

    private fun setClickItems() {
        with(binding) {
            toolbarVillaCreate.setNavigationOnClickListener {
                Navigation.findNavController(binding.root)
                    .popBackStack()
            }

            buttonNextVillaCreatePage1.setOnClickListener {

                //sayfalar arası taşınacak başlangıç modelini oluşturuyoruz
                villaPageArgumentsModel = setVillaPageArguments()

                val directions =
                    HostVillaCreate1EnterFragmentDirections.actionNavigationHostVillaCreateEnterToHostVillaCreateFragment(
                        villaPageArgumentsModel
                    )
                Navigation.findNavController(it)
                    .navigate(directions)
            }

            propertyStatus.forEachIndexed { index, cardView ->
                cardView.setOnClickListener {
                    clearAllCardBackground(propertyStatus)
                    setCardBackgroundColor(cardView)

                    //Seçilen durumu ana değişkene atıyoruız
                    selectedPropertyStatus = when (index) {
                        0    -> false
                        1    -> true
                        else -> false
                    }
                }

            }

            propertyTypes.forEachIndexed { index, cardView ->
                cardView.setOnClickListener {
                    clearAllCardBackground(propertyTypes)
                    setCardBackgroundColor(cardView)

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

    private fun clearAllCardBackground(cardList: List<CardView>) {
        // Tüm elemanların arka planını boş olarak ayarla
        cardList.forEach {
            it.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.host_cardview_background
                )
            )
        }
    }

    private fun setCardBackgroundColor(cardView: CardView) {
        // Seçilen elemanın arka planını seçili olarak ayarla
        cardView.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.md_theme_primary
            )
        )
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

                    with(villa) {
                        forSale?.let { status ->
                            selectedPropertyStatus = status

                            clearAllCardBackground(propertyStatus.toList())

                            if (status) {
                                setCardBackgroundColor(propertyStatus[1])
                            } else {
                                setCardBackgroundColor(propertyStatus[0])
                            }
                        }

                        propertyType?.let { type ->
                            selectedPropertyType = type

                            clearAllCardBackground(propertyTypes.toList())

                            when (type) {
                                PropertyType.HOUSE       -> setCardBackgroundColor(propertyTypes[0])
                                PropertyType.APARTMENT   -> setCardBackgroundColor(propertyTypes[1])
                                PropertyType.GUEST_HOUSE -> setCardBackgroundColor(propertyTypes[2])
                                PropertyType.HOTEL       -> setCardBackgroundColor(propertyTypes[3])
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setVillaPageArguments(): VillaPageArgumentsModel {
        return VillaPageArgumentsModel(
            villaId = villaId,
            coverImage = null,
            otherImages = mutableListOf(),
            villa = createVilla(villaFromArgs)
        )
    }

    private fun createVilla(villa: Villa): Villa {
        if (villa.villaId == null) {
            villa.villaId = UUID.randomUUID()
                .toString()
        }

        villa.apply {
            forSale = selectedPropertyStatus
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