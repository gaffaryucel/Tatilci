package com.androiddevelopers.villabuluyorum.view.user.profile

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.view.MainActivity
import com.androiddevelopers.villabuluyorum.view.host.HostBottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.user.profile.ProfileViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: ProfileViewModel by viewModels()

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        observeLiveData()
        setButtonActions()
    }
    private fun setButtonActions(){
        binding.ivSettings.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.cardViewBecomeHomeOwner.setOnClickListener {
            val intent = Intent(requireActivity(), HostBottomNavigationActivity::class.java)
            requireActivity().finish()
            viewModel.setStartModeHost()
            startActivity(intent)
        }
        binding.cardViewExit.setOnClickListener{
            viewModel.signOutAndExit(requireContext())
        }

    }

    private fun observeLiveData() {
        viewModel.userInfoMessage.observe(viewLifecycleOwner, Observer {
           when (it.status) {
                Status.SUCCESS -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.tvErrorProfile.visibility = View.GONE
                }

                Status.LOADING -> {
                    binding.pbProfile.visibility = View.VISIBLE
                    binding.tvErrorProfile.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.tvErrorProfile.visibility = View.VISIBLE
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
            Glide.with(requireContext()).load(userData.profileImageUrl).into(binding.ivProfilePhoto)
            Glide.with(requireContext()).load(userData.profileBannerUrl).into(binding.ivProfileBanner)
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}