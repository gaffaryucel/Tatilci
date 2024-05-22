package com.androiddevelopers.villabuluyorum.view.user.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentUserProfileBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.viewmodel.user.profile.UserProfileViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: UserProfileViewModel by viewModels()

    private val villaAdapter: HouseAdapter = HouseAdapter()

    private lateinit var userId: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        userId = arguments?.getString("user_id") ?: ""
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        villaAdapter.inProfile = true
        if (userId.isNotEmpty()) {
            viewModel.getUserVillas(userId, 20)
            viewModel.getUserData(userId)
        }
        binding.rvUserVillas.adapter = villaAdapter
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
        // TODO: Yorumları çekmek için gerekli kodlarım yazılıp gelen veriye göre gerekli elemanın gösteirlmesi gerekli
        binding.rvComments.visibility = View.GONE
        binding.pbComments.visibility = View.GONE
        binding.layoutEmptyComments.visibility = View.VISIBLE
        viewModel.firebaseMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.layoutProfile.visibility = View.VISIBLE
                    binding.pbProfile.visibility = View.GONE
                    binding.layoutProfileError.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.layoutProfile.visibility = View.GONE
                    binding.pbProfile.visibility = View.VISIBLE
                    binding.layoutProfileError.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.layoutProfile.visibility = View.GONE
                    binding.pbProfile.visibility = View.GONE
                    binding.layoutProfileError.visibility = View.VISIBLE
                }
            }
        })
        viewModel.villaMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                   if(it.data == true){
                       binding.rvUserVillas.visibility = View.VISIBLE
                       binding.pbUserProfileVillas.visibility = View.GONE
                       binding.layoutEmptyVillas.visibility = View.GONE
                   }else{
                       binding.rvUserVillas.visibility = View.GONE
                       binding.pbUserProfileVillas.visibility = View.GONE
                       binding.layoutEmptyVillas.visibility = View.VISIBLE
                   }
                }
                Status.LOADING -> {
                    binding.rvUserVillas.visibility = View.GONE
                    binding.pbUserProfileVillas.visibility = View.VISIBLE
                    binding.layoutEmptyVillas.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.rvUserVillas.visibility = View.GONE
                    binding.pbUserProfileVillas.visibility = View.GONE
                    binding.layoutEmptyVillas.visibility = View.VISIBLE
                }
            }
        })
        viewModel.userVillas.observe(viewLifecycleOwner, Observer { villas ->
            if (villas != null && villas.isNotEmpty()) {
                villaAdapter.housesList = villas
                binding.layoutEmptyVillas.visibility = View.GONE
            } else {
                binding.layoutEmptyVillas.visibility = View.VISIBLE
            }
            binding.apply {
                rating = "0.0"
                commentCount = "0"
                ratingCount = "0"
                yearCount = "0"
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            if (userData != null) {
                binding.apply {
                    user = userData
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}