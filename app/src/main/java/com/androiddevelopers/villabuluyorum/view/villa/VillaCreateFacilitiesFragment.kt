package com.androiddevelopers.villabuluyorum.view.villa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.databinding.FragmentVillaCreateFacilitiesBinding
import com.androiddevelopers.villabuluyorum.model.villa.Facilities
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.villa.VillaCreateFacilitiesViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VillaCreateFacilitiesFragment : Fragment() {
    private val viewModel: VillaCreateFacilitiesViewModel by viewModels()
    private var _binding: FragmentVillaCreateFacilitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVillaCreateFacilitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: Chip'leri listeden dinamik olarak oluştur

        binding.facilitiesSave.setOnClickListener {
            val directions =
                VillaCreateFacilitiesFragmentDirections
                    .actionVillaCreateFacilitiesFragmentToVillaCreateFragment(
                        collectChips()
                    )

            Navigation.findNavController(view).navigate(directions)
        }
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

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}