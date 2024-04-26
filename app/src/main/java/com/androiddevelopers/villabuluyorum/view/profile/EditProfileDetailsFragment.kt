package com.androiddevelopers.villabuluyorum.view.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.downloadImage
import com.androiddevelopers.villabuluyorum.databinding.FragmentEditProfileDetailsBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.checkPermissionImageGallery
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.profile.EditProfileDetailsViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.profile.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileDetailsFragment : Fragment() {


    private var _binding: FragmentEditProfileDetailsBinding? = null
    private val binding get() = _binding!!

    val viewModel: EditProfileDetailsViewModel by viewModels()

    private var selectedProfilePhoto: Uri? = null
    private lateinit var profilePhotoLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLaunchers()
       /*
        if (checkPermissionImageGallery(requireActivity(), 800)) {
            openCoverImagePicker()
        }
        */
    }


    override fun onResume() {
        super.onResume()
        observeLiveData()
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }

    private fun observeLiveData() {
        viewModel.userInfoMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbEditProfileInfo.visibility = View.GONE
                    binding.tvErrorEditProfile.visibility = View.GONE
                    binding.svEditPRofile.visibility = View.VISIBLE
                }

                Status.LOADING -> {
                    binding.pbEditProfileInfo.visibility = View.VISIBLE
                    binding.tvErrorEditProfile.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbEditProfileInfo.visibility = View.GONE
                    binding.tvErrorEditProfile.visibility = View.VISIBLE
                    binding.svEditPRofile.visibility = View.GONE
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                binding.apply {
                    user = userData
                }
            }
            lifecycleScope.launch {
                val location = viewModel.getCityFromCoordinates(
                   requireContext(),
                   userData.latitude  ?: 41.00527,
                   userData.longitude ?:  28.97696)
               binding.etUserLocation.setText(location)
            }
            Glide.with(requireContext()).load(userData.profileImageUrl).into(binding.ivUserProfilePhoto)
            Glide.with(requireContext()).load(userData.profileBannerUrl).into(binding.ivUserBanner)
        })
    }
    private fun setupLaunchers() {
        profilePhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { image ->
                        selectedProfilePhoto = image
                        selectedProfilePhoto?.let {
                            downloadImage(binding.ivUserProfilePhoto, image.toString())
                        }
                    }
                }
            }
    }
    private fun openCoverImagePicker() {
        val imageIntent =
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        profilePhotoLauncher.launch(imageIntent)
    }
}
