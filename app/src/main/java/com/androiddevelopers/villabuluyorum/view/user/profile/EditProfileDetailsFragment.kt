package com.androiddevelopers.villabuluyorum.view.user.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.adapter.downloadImage
import com.androiddevelopers.villabuluyorum.databinding.FragmentEditProfileDetailsBinding
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.checkPermissionImageGallery
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.viewmodel.user.profile.EditProfileDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileDetailsFragment : Fragment() {


    private var _binding: FragmentEditProfileDetailsBinding? = null
    private val binding get() = _binding!!

    val viewModel: EditProfileDetailsViewModel by viewModels()

    private var selectedProfilePhoto: Uri? = null
    private var selectedBannerPgoto: Uri? = null
    private lateinit var profilePhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var bannerPhotoLauncher: ActivityResultLauncher<Intent>

    private var oldUser = UserModel()

    private var progressDialog: ProgressDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())

        binding.ivEditProfilePhoto.setOnClickListener {
            if (checkPermissionImageGallery(requireActivity(), 800)) {
                openProfilePicker()
            }
        }
        binding.ivEditProfileBanner.setOnClickListener {
            if (checkPermissionImageGallery(requireActivity(), 800)) {
                openBannerPicker()
            }
        }
        binding.btnSave.setOnClickListener {
            getUserDataAndSave()
        }
        binding.editLocationIcon.setOnClickListener {
            val action = EditProfileDetailsFragmentDirections.actionEditProfileDetailsFragmentToEditAddressFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        setupLaunchers()
        observeLiveData()
    }


    override fun onResume() {
        super.onResume()
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
        viewModel.uploadMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog?.dismiss()
                }

                Status.LOADING -> {
                    startLoadingProcess(progressDialog)
                }

                Status.ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog?.dismiss()
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                oldUser = userData
                binding.apply {
                    user = userData
                }
            }
            val userAddress = userData.address
            if (userAddress != null){
                val city = userAddress.province.toString()
                val district = userAddress.district.toString()
                var address = ""
                if (city.isNotEmpty()){
                    address += "$city,"
                }
                if(district.isNotEmpty()){
                    address += district
                }
                if (address.isNotEmpty()){
                    binding.etUserLocation.setText(address)
                }else{
                    lifecycleScope.launch {
                        val location = viewModel.getCityFromCoordinates(
                            requireContext(),
                            userData.latitude  ?: 41.00527,
                            userData.longitude ?:  28.97696)
                        binding.etUserLocation.setText(location)
                    }
                }
            }else{
                lifecycleScope.launch {
                    val location = viewModel.getCityFromCoordinates(
                        requireContext(),
                        userData.latitude  ?: 41.00527,
                        userData.longitude ?:  28.97696)
                    binding.etUserLocation.setText(location)
                }
            }
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
        bannerPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { image ->
                        selectedBannerPgoto = image
                        selectedBannerPgoto?.let {
                            downloadImage(binding.ivUserBanner, image.toString())
                        }
                    }
                }
            }
    }
    private fun openProfilePicker() {
        val imageIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        profilePhotoLauncher.launch(imageIntent)
    }
    private fun openBannerPicker() {
        val imageIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        bannerPhotoLauncher.launch(imageIntent)
    }

    // Kullanıcı verilerini alır ve kaydeder
    private fun getUserDataAndSave() {
        var isUpdated = false // Veri güncellendi mi kontrolü için bir değişken
        val newUser = UserModel() // Yeni kullanıcı modeli oluşturulur
        viewModel.startLoading() // Yükleme işlemi başlatılır

        // Kullanıcı tarafından girilen veriler alınır
        newUser.firstName = binding.etFirstName.text.toString().takeIf { it.isNotEmpty() }
        newUser.lastName = binding.etLastName.text.toString().takeIf { it.isNotEmpty() }
        newUser.username = binding.usernameInput.text.toString().takeIf { it.isNotEmpty() }
        newUser.email = binding.etEmail.text.toString().takeIf { it.isNotEmpty() }
        newUser.phoneNumber = binding.etPhoneNumber.text.toString().takeIf { it.isNotEmpty() }

        // Değişen alanlar için harita oluşturulur
        val uploadMap = viewModel.getMapIfDataChanged(oldUser, newUser)

        // Eğer profil banner'ı seçili ise
        if (selectedBannerPgoto != null) {
            // Değişiklik haritası boş değilse
            if (uploadMap.isNotEmpty()) {
                // Profil banner'ı güncellenir ve işlem tamamlandı olarak işaretlenir
                viewModel.uploadUserPhoto(selectedBannerPgoto!!, "profileBannerUrl", uploadMap)
                isUpdated = true
            } else {
                // Değişiklik haritası boşsa, profil banner'ı güncellenir ancak veri gönderilmez
                viewModel.uploadUserPhoto(selectedBannerPgoto!!, "profileBannerUrl", null)
                isUpdated = true
            }
        }

        // Eğer profil fotoğrafı seçili ise
        if (selectedProfilePhoto != null) {
            // İşlem tamamlanmadıysa ve değişiklik haritası boş değilse
            if (!isUpdated && uploadMap.isNotEmpty()) {
                // Profil fotoğrafı güncellenir ve işlem tamamlandı olarak işaretlenir
                viewModel.uploadUserPhoto(selectedProfilePhoto!!, "profileImageUrl", uploadMap)
                isUpdated = true
            } else {
                // İşlem tamamlandıysa veya değişiklik haritası boşsa, profil fotoğrafı güncellenir ancak veri gönderilmez
                viewModel.uploadUserPhoto(selectedProfilePhoto!!, "profileImageUrl", null)
            }
        }

        // İşlem tamamlanmadıysa ve değişiklik haritası boş değilse
        if (!isUpdated && uploadMap.isNotEmpty()) {
            // Diğer değişiklikler güncellenir ve işlem tamamlandı olarak işaretlenir
            viewModel.updateUserData(uploadMap)
            isUpdated = true
        }
    }

}
