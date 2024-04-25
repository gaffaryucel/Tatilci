package com.androiddevelopers.villabuluyorum.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.view.MainActivity
import com.androiddevelopers.villabuluyorum.viewmodel.profile.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
            Navigation.findNavController(it).navigate(
                ProfileFragmentDirections.actionNavigationProfileToVillaCreateFragment()
            )
        }

        /*
         val googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
            googleSignInClient.signOut().addOnCompleteListener {
                // Kullanıcıyı başka bir aktiviteye yönlendir veya ek işlemler yap
                // Örneğin, giriş ekranına geri dönme gibi
            }
            FirebaseAuth.getInstance().signOut().also {
                val intent = Intent(requireActivity(),MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                requireActivity().finish()
                requireActivity().startActivity(intent)
            }
         */

        observeLiveData()
    }
    private fun observeLiveData() {
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                binding.apply {
                    user = userData
                }
            }
            Glide.with(requireContext()).load(userData.profileImageUrl).into(binding.ivProfilePhoto)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}