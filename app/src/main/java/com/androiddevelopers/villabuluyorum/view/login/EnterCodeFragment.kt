package com.androiddevelopers.villabuluyorum.view.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.databinding.FragmentEnterCodeBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.view.user.BottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.login.EntryCodeViewModel
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterCodeFragment : Fragment() {

    private var _binding: FragmentEnterCodeBinding? = null
    private val binding get() = _binding!!

    private var errorDialog: AlertDialog? = null

    private lateinit var viewModel: EntryCodeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterCodeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[EntryCodeViewModel::class.java]
        val view = binding.root
        return view
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storedVerificationId = arguments?.getString("code")
        errorDialog = AlertDialog.Builder(requireContext()).create()

        binding.btnConfirmNumber.setOnClickListener {
            it.isEnabled = false
            binding.pbVerification.visibility = View.VISIBLE
            val otp = binding.etVerificationCode.text.toString()
            if (storedVerificationId != null) {
                val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)
                viewModel.signInWithPhoneAuthCredential(requireActivity(), credential)
            }
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvResend.setOnClickListener {
            findNavController().popBackStack()
        }
        setupDialog()
        observeLiveData()
    }


    private fun setupDialog() {
        errorDialog?.setTitle("Hatalı kod")
        errorDialog?.setMessage("Lütfen doğrulama kodunu tekrar girin")
        errorDialog?.setCancelable(true)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->

        }
    }

    private fun observeLiveData() {
        viewModel.authState.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    if (it.data == true) {
                        goToHome()
                    } else {
                        goTOCreateUserName()
                    }
                }

                Status.ERROR -> {
                    errorDialog?.show()
                }

                Status.LOADING -> {
                    binding.pbVerification.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun goTOCreateUserName() {
        val action = EnterCodeFragmentDirections.actionEnterCodeFragmentToCreateUserNameFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun goToHome() {
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        requireActivity().finish()
        startActivity(intent)
    }
}