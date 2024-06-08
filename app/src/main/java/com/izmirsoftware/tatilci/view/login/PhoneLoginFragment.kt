package com.izmirsoftware.tatilci.view.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.izmirsoftware.tatilci.databinding.FragmentPhoneLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit


class PhoneLoginFragment : Fragment() {


    private var _binding: FragmentPhoneLoginBinding? = null
    private val binding get() = _binding!!

    private var errorDialog: AlertDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firebaseAuth = Firebase.auth
        errorDialog = AlertDialog.Builder(requireContext()).create()

        binding.btnSendCode.setOnClickListener {
            val phone = "+90${binding.etPhoneConfirm.text.toString()}"
            it.isEnabled = false
            binding.pbPhoneConfirm.visibility = View.VISIBLE
            if (phone.isNotEmpty()) {
                val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phone) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity()) // Activity (for callback binding)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken,
                        ) {
                            // The SMS verification code has been sent to the provided phone number, we
                            // now need to ask the user to enter the code and then construct a credential
                            // by combining the code with a verification ID.

                            // Save verification ID and resending token so we can use them later
                            val storedVerificationId = verificationId
                            //resendToken = token
                            goToEnterCode(storedVerificationId)
                        }

                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            println("credential")
                            // This callback will be invoked in two situations:
                            // 1 - Instant verification. In some cases the phone number can be instantly
                            //     verified without needing to send or enter a verification code.
                            // 2 - Auto-retrieval. On some devices Google Play services can automatically
                            //     detect the incoming verification SMS and perform verification without
                            //     user action.
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            // This callback is invoked in an invalid request for verification is made,
                            // for instance if the the phone number format is not valid.
                            println()
                            setupDialog(e.localizedMessage)
                            binding.btnSendCode.isEnabled = true
                            binding.pbPhoneConfirm.visibility = View.INVISIBLE

                            if (e is FirebaseAuthInvalidCredentialsException) {
                                // Invalid request
                                errorDialog?.show()
                            } else if (e is FirebaseTooManyRequestsException) {
                                errorDialog?.show()
                                // The SMS quota for the project has been exceeded
                            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                                errorDialog?.show()
                                // reCAPTCHA verification attempted with null Activity
                            }

                            // Show a message and update the UI
                        }
                    }).build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            }
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setupDialog(message: String) {
        errorDialog?.setTitle("Doğrulama başarısız")
        errorDialog?.setMessage(message)
        errorDialog?.setCancelable(true)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Kapat") { _, _ ->

        }
    }

    private fun goToEnterCode(id: String) {
        val action = PhoneLoginFragmentDirections.actionPhoneLoginFragmentToEnterCodeFragment(id)
        Navigation.findNavController(requireView()).navigate(action)
    }

}