package com.androiddevelopers.villabuluyorum.view.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.view.MainActivity
import com.androiddevelopers.villabuluyorum.viewmodel.profile.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: ProfileViewModel by viewModels()

    private val villaAdapter: HouseAdapter = HouseAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvUserVillas.adapter = villaAdapter
        observeLiveData()

        binding.btnBecomeHomeowner.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToVillaCreateFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.ivSettings.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }
    private fun observeLiveData() {
        viewModel.userInfoMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.layoutProfileError.visibility = View.GONE
                }

                Status.LOADING -> {
                    binding.pbProfile.visibility = View.VISIBLE
                    binding.layoutProfileError.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.layoutProfileError.visibility = View.VISIBLE
                }
            }
        })
        viewModel.villaInfoMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbVillas.visibility = View.GONE
                    if (it.data == true){
                        binding.layoutEmptyVillas.visibility = View.GONE
                    }else{
                        binding.layoutEmptyVillas.visibility = View.VISIBLE
                    }
                }

                Status.LOADING -> {
                    binding.pbVillas.visibility = View.VISIBLE
                    binding.layoutEmptyVillas.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbVillas.visibility = View.GONE
                    binding.layoutEmptyVillas.visibility = View.VISIBLE
                }
            }
        })
        viewModel.userVillas.observe(viewLifecycleOwner, Observer { villas ->
            if (villas != null && villas.isNotEmpty()) {
                villaAdapter.housesList = villas
                binding.layoutEmptyVillas.visibility = View.GONE
            }else{
                binding.layoutEmptyVillas.visibility = View.VISIBLE
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                binding.apply {
                    user = userData
                }
            }
            Glide.with(requireContext()).load(userData.profileImageUrl).into(binding.ivProfilePhoto)
            Glide.with(requireContext()).load(userData.profileBannerUrl).into(binding.ivProfileBanner)
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}