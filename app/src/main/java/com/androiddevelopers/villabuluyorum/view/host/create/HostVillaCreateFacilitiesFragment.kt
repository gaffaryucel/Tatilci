package com.androiddevelopers.villabuluyorum.view.host.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaCreateFacilitiesBinding
import com.androiddevelopers.villabuluyorum.model.villa.Facilities
import com.androiddevelopers.villabuluyorum.util.hideHostBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.host.create.HostVillaCreateFacilitiesViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostVillaCreateFacilitiesFragment : Fragment() {
    private val viewModel: HostVillaCreateFacilitiesViewModel by viewModels()
    private var _binding: FragmentHostVillaCreateFacilitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaCreateFacilitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewChips()

        binding.facilitiesSave.setOnClickListener {
            val directions =
                HostVillaCreateFacilitiesFragmentDirections.actionHostVillaCreateFacilitiesFragmentToHostVillaCreateFragment(
                    collectChips()
                )

            Navigation.findNavController(view).navigate(directions)
        }
    }

    private fun setViewChips() {
        with(binding) {
            with(resources) {
                getStringArray(R.array.landscape)
                    .forEach {
                        chipGroupLandscape.addView(setChip(it))
                    }

                getStringArray(R.array.bath)
                    .forEach {
                        chipGroupBath.addView(setChip(it))
                    }

                getStringArray(R.array.bedroom)
                    .forEach {
                        chipGroupBedroom.addView(setChip(it))
                    }

                getStringArray(R.array.entertainment)
                    .forEach {
                        chipGroupEntertainment.addView(setChip(it))
                    }

                getStringArray(R.array.heating_cooling)
                    .forEach {
                        chipGroupHeatingCooling.addView(setChip(it))
                    }

                getStringArray(R.array.kitchen_food)
                    .forEach {
                        chipGroupKitchenFood.addView(setChip(it))
                    }

                getStringArray(R.array.location_features)
                    .forEach {
                        chipGroupLocationFeatures.addView(setChip(it))
                    }

                getStringArray(R.array.outdoor)
                    .forEach {
                        chipGroupOutdoor.addView(setChip(it))
                    }

                getStringArray(R.array.services)
                    .forEach {
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

    override fun onResume() {
        super.onResume()
        hideHostBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}