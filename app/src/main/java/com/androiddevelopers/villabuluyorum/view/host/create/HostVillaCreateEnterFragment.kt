package com.androiddevelopers.villabuluyorum.view.host.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaCreateEnterBinding
import com.androiddevelopers.villabuluyorum.model.CreateVillaPageArguments
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.hideHostBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showHostBottomNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostVillaCreateEnterFragment : Fragment() {
    //private val viewModel: HostVillaCreateBaseViewModel by viewModels()
    private var _binding: FragmentHostVillaCreateEnterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaCreateEnterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickItems()

        //TODO: İlk sayfaya ev tipi ekle

        binding.buttonNextVillaCreatePage1.setOnClickListener {
            val directions =
                HostVillaCreateEnterFragmentDirections.actionNavigationHostVillaCreateEnterToHostVillaCreateFragment(
                    CreateVillaPageArguments(
                        coverImage = null,
                        otherImages = mutableListOf(),
                        villa = Villa()
                    )
                )
            Navigation.findNavController(it).navigate(directions)
        }
    }

    private fun setClickItems() {
        with(binding) {
            toolbarVillaCreate.setNavigationOnClickListener {
                Navigation.findNavController(binding.root).popBackStack()
            }
        }
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