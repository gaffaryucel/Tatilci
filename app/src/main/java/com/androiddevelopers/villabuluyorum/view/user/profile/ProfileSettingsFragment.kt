package com.androiddevelopers.villabuluyorum.view.user.profile

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileSettingsBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.view.MainActivity
import com.androiddevelopers.villabuluyorum.view.host.HostBottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.user.profile.ProfileSettingsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingsFragment : Fragment() {


    private var _binding: FragmentProfileSettingsBinding? = null
    private val binding get() = _binding!!

    val viewModel: ProfileSettingsViewModel by viewModels()

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())

        setButtonActions()
        observeLiveData()
    }
    private fun setButtonActions(){
        binding.cardViewProfile.setOnClickListener {
            val action = ProfileSettingsFragmentDirections.actionProfileSettingsFragmentToEditProfileDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.cardViewBecomeHomeOwner.setOnClickListener {
            val intent = Intent(requireActivity(),HostBottomNavigationActivity::class.java)
            startActivity(intent)
        }
        binding.cardViewExit.setOnClickListener{
            viewModel.signOutAndExit(requireContext())
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeLiveData() {
        viewModel.userInfoMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbProfileSettings.visibility = View.GONE
                }

                Status.LOADING -> {
                    binding.pbProfileSettings.visibility = View.VISIBLE
                }

                Status.ERROR -> {
                    binding.pbProfileSettings.visibility = View.GONE
                }
            }
        })
        viewModel.exitMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    requireActivity().finish()
                    requireActivity().startActivity(intent)
                    progressDialog?.dismiss()
                }
                Status.LOADING -> {
                    startLoadingProcess(progressDialog)
                }

                Status.ERROR -> {
                    progressDialog?.dismiss()
                    Toast.makeText(requireContext(), "Hata oluştu, tekrar deneyin", Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                binding.apply {
                    user = userData
                }
            }
            Glide.with(requireContext()).load(userData.profileImageUrl).into(binding.ivUserPhoto)
        })
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }

}