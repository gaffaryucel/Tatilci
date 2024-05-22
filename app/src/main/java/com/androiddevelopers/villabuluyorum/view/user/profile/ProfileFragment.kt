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
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.UserType
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

    private var userType : UserType = UserType.NORMAL_USER

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
        setButtonActions()
    }

    override fun onResume() {
        super.onResume()
        observeLiveData()
    }
    private fun setButtonActions(){
        binding.ivSettings.setOnClickListener {
            goToEditProfileDetails(it)
        }
        binding.cardViewBecomeHomeOwner.setOnClickListener {
            if (viewModel.checkAllFieldsValid()){
                if (userType == UserType.HOMEOWNER){
                    viewModel.setStartModeHost()
                    goToHomeOwnerActivity()
                }else{
                    showTermsConditionsDialog()
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "Ev sahibi olmak için öncelikle tüm kullanıcı bilgilerini girmelisiniz",
                    Toast.LENGTH_SHORT
                ).show()
                goToEditProfileDetails(it)
            }
        }
        binding.cardViewExit.setOnClickListener{
            viewModel.signOutAndExit(requireContext())
        }
        binding.cardViewRateHoliday.setOnClickListener{
            val action = ProfileFragmentDirections.actionNavigationProfileToReviewFragment()
            Navigation.findNavController(it).navigate(action)
        }

    }

    private fun observeLiveData() {
        viewModel.userInfoMessage.observe(viewLifecycleOwner, Observer {
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
                }
            }
            userType = userData.userType ?: UserType.NORMAL_USER
        })
        viewModel.reservationCount.observe(viewLifecycleOwner, Observer {
            val count = it ?: 0
            binding.tvReservationCount.text = count.toString()
        })
    }
    private fun goToEditProfileDetails(view : View){
        val action = ProfileFragmentDirections.actionNavigationProfileToEditProfileDetailsFragment()
        Navigation.findNavController(view).navigate(action)
    }
    private fun showTermsConditionsDialog() {
        val dialogFragment = TermsConditionsDialogFragment()
        dialogFragment.show(parentFragmentManager, "TermsConditionsDialog")
        dialogFragment.onClick = {
            if (it){
                viewModel.setStartModeHost()
                viewModel.setUserTypeHomeOwner()
            }
        }
    }
    private fun goToHomeOwnerActivity(){
        val intent = Intent(requireActivity(), HostBottomNavigationActivity::class.java)
        requireActivity().finish()
        startActivity(intent)
        viewModel.setStartModeHost()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}