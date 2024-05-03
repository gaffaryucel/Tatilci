package com.androiddevelopers.villabuluyorum.view.user.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.user.profile.ProfileViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

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


    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Yorumlar ve rezervasyonları firebase'den alıp gösterme işlemleri yapılmalı

        binding.rvUserVillas.adapter = villaAdapter
        villaAdapter.inProfile = true

        observeLiveData()

        binding.ivSettings.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToProfileSettingsFragment()
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
                    binding.pbComments.visibility = View.GONE
                    if (it.data == true){
                        binding.layoutEmptyVillas.visibility = View.GONE
                        binding.layoutEmptyComments.visibility = View.GONE
                    }else{
                        binding.layoutEmptyVillas.visibility = View.VISIBLE
                        binding.layoutEmptyComments.visibility = View.VISIBLE
                    }
                }

                Status.LOADING -> {
                    binding.pbVillas.visibility = View.VISIBLE
                    binding.layoutEmptyVillas.visibility = View.GONE
                    binding.pbComments.visibility = View.VISIBLE
                    binding.layoutEmptyComments.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbVillas.visibility = View.GONE
                    binding.layoutEmptyVillas.visibility = View.VISIBLE
                    binding.pbComments.visibility = View.GONE
                    binding.layoutEmptyComments.visibility = View.VISIBLE
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