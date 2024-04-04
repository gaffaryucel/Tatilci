package com.androiddevelopers.villabuluyorum.view.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.databinding.FragmentEnterCodeBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentPhoneLoginBinding
import com.androiddevelopers.villabuluyorum.view.BottomNavigationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class EnterCodeFragment : Fragment() {

    private var _binding: FragmentEnterCodeBinding? = null
    private val binding get() = _binding!!

    private var errorDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterCodeBinding.inflate(inflater, container, false)
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
            if (storedVerificationId != null){
                val credential = PhoneAuthProvider.getCredential(storedVerificationId,otp)
                signInWithPhoneAuthCredential(credential)
            }
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvResend.setOnClickListener {
            findNavController().popBackStack()
        }

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = task.result?.user
                    goTOCreateUserName()
                } else {
                    setupDialog()
                }
            }
    }

    private fun goTOCreateUserName(){
        val action = EnterCodeFragmentDirections.actionEnterCodeFragmentToCreateUserNameFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }
    private fun setupDialog(){
        errorDialog?.setTitle("Hatalı kod")
        errorDialog?.setMessage("Lütfen doğrulama kodunu tekrar girin")
        errorDialog?.setCancelable(true)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->

        }
    }
}