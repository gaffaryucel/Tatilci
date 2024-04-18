package com.androiddevelopers.villabuluyorum.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.view.MainActivity
import com.androiddevelopers.villabuluyorum.viewmodel.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivSettings.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.btnMessage.setOnClickListener {
            FirebaseAuth.getInstance().signOut().also {
                val intent = Intent(requireActivity(),MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                requireActivity().finish()
                requireActivity().startActivity(intent)
            }
        }

        binding.buttonCreateVilla.setOnClickListener {
            Navigation.findNavController(it).navigate(
                ProfileFragmentDirections.actionNavigationProfileToVillaCreateFragment()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}