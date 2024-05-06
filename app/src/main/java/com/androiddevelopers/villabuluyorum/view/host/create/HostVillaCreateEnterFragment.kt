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

        binding.cardHostVillaCreateEnter.setOnClickListener {
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}