package com.izmirsoftware.tatilci.view.host.profile

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
import com.izmirsoftware.tatilci.databinding.FragmentHostProfileBinding
import com.izmirsoftware.tatilci.model.ReviewModel
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.startLoadingProcess
import com.izmirsoftware.tatilci.view.MainActivity
import com.izmirsoftware.tatilci.view.user.BottomNavigationActivity
import com.izmirsoftware.tatilci.view.user.profile.ResetPasswordFragment
import com.izmirsoftware.tatilci.viewmodel.host.profile.HostProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostProfileFragment : Fragment() {
    private val viewModel: HostProfileViewModel by viewModels()
    private var _binding: FragmentHostProfileBinding? = null
    private val binding get() = _binding!!

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())


        binding.cardViewBecomeHomeOwner.setOnClickListener {
            goToGuestMode()
        }
        binding.cardViewExit.setOnClickListener{
            viewModel.signOutAndExit(requireContext())
        }
        binding.cardViewSecurity.setOnClickListener{
            val dialog = ResetPasswordFragment()
            dialog.show(parentFragmentManager,"dialog")
        }
        binding.cardViewAppOptions.setOnClickListener{
            val action = HostProfileFragmentDirections.actionNavigationHostProfileToContactUsFragment2()
            Navigation.findNavController(it).navigate(action)
        }
        binding.cardViewAppTheme.setOnClickListener{
            val action = HostProfileFragmentDirections.actionNavigationHostProfileToContactUsFragment2()
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
    }


    private fun goToGuestMode(){
        val activity = requireActivity()
        val intent = Intent(activity, BottomNavigationActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
        viewModel.setStartModeUser()
    }
    private fun observeLiveData() {
        viewModel.firebaseMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.tvErrorProfile.visibility = View.GONE
                    binding.layoutProfile.visibility = View.VISIBLE
                }

                Status.LOADING -> {
                    binding.pbProfile.visibility = View.VISIBLE
                    binding.tvErrorProfile.visibility = View.GONE
                    binding.layoutProfile.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbProfile.visibility = View.GONE
                    binding.tvErrorProfile.visibility = View.VISIBLE
                    binding.layoutProfile.visibility = View.GONE
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
                    rating = "0.0"
                }
            }
        })
        viewModel.userVillas.observe(viewLifecycleOwner, Observer {
            val count = it ?: 0
            binding.postCount = count.toString()
        })
        viewModel.userReviews.observe(viewLifecycleOwner, Observer { reviews ->
            if (reviews != null && reviews.isNotEmpty()) {
                binding.reviewCount = reviews.size.toString()
                try {
                    val averageRating = calculateAverageRating(reviews)
                    val formattedNumber = String.format("%.1f", averageRating)
                    binding.rating = formattedNumber
                }catch (e : Exception){
                    println("e : "+e.localizedMessage)
                }
            } else{
                binding.reviewCount = "0"
                binding.rating = "0.0"
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


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}