package com.androiddevelopers.villabuluyorum.view.user.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevelopers.villabuluyorum.adapter.HostReviewAdapter
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentUserProfileBinding
import com.androiddevelopers.villabuluyorum.model.ReviewModel
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.viewmodel.user.profile.UserProfileViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: UserProfileViewModel by viewModels()

    private val villaAdapter: HouseAdapter = HouseAdapter()
    private val reviewAdapter: HostReviewAdapter = HostReviewAdapter()

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
            viewModel.getUserVillas(userId, 10)
            viewModel.getUserData(userId)
            viewModel.getUserReviews(userId,10)
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
        viewModel.reviewMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                   if(it.data == true){
                       binding.rvComments.visibility = View.VISIBLE
                       binding.pbComments.visibility = View.GONE
                       binding.layoutEmptyComments.visibility = View.GONE
                   }else{
                       binding.rvComments.visibility = View.GONE
                       binding.pbComments.visibility = View.GONE
                       binding.layoutEmptyComments.visibility = View.VISIBLE
                   }
                }
                Status.LOADING -> {
                    binding.rvComments.visibility = View.GONE
                    binding.pbComments.visibility = View.VISIBLE
                    binding.layoutEmptyComments.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.rvComments.visibility = View.GONE
                    binding.pbComments.visibility = View.GONE
                    binding.layoutEmptyComments.visibility = View.VISIBLE
                }
            }
        })
        viewModel.userVillas.observe(viewLifecycleOwner, Observer { villas ->
            if (villas != null && villas.isNotEmpty()) {
                villaAdapter.housesList = villas
                binding.postcount = villas.size.toString()
            } else{
                binding.postcount = "0"
                binding.layoutEmptyVillas.visibility = View.VISIBLE
            }
        })
        viewModel.userReviews.observe(viewLifecycleOwner, Observer { reviews ->
            if (reviews != null && reviews.isNotEmpty()) {
                reviewAdapter.reviewList = reviews
                binding.rateCount = reviews.size.toString()
                binding.rvComments.adapter = reviewAdapter
                try {
                    val averageRating = calculateAverageRating(reviews)
                    val formattedNumber = String.format("%.1f", averageRating)
                    binding.rating = formattedNumber
                }catch (e : Exception){
                    println("e : "+e.localizedMessage)
                }
            } else{
                binding.rateCount = "0"
                binding.layoutEmptyComments.visibility = View.VISIBLE
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
    private fun calculateAverageRating(reviews: List<ReviewModel>): Double {
        val total = reviews.sumBy { it.rating ?: 0 }
        return if (reviews.isNotEmpty()) {
            total.toDouble() / reviews.size
        } else {
            0.0
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}