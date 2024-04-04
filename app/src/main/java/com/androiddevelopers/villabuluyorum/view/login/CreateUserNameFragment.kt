package com.androiddevelopers.villabuluyorum.view.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentCreateUserNameBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentEnterCodeBinding
import com.androiddevelopers.villabuluyorum.view.BottomNavigationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider


class CreateUserNameFragment : Fragment() {

    private var _binding: FragmentCreateUserNameBinding? = null
    private val binding get() = _binding!!

    private var errorDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateUserNameBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storedVerificationId = arguments?.getString("code")
        errorDialog = AlertDialog.Builder(requireContext()).create()

        binding.btnCreateUserName.setOnClickListener {
            it.isEnabled = false
            binding.pbUserName.visibility = View.VISIBLE
            val otp = binding.etUserName.text.toString()
            if (storedVerificationId != null){
                goToHome()
            }
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }


    private fun goToHome(){
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }
    private fun setupDialog(){
        errorDialog?.setTitle("Kullanıcı adı")
        errorDialog?.setMessage("Farklı bir Kullanıcı adı girin")
        errorDialog?.setCancelable(true)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->

        }
    }
}