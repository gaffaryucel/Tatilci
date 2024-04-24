package com.androiddevelopers.villabuluyorum.view.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.MyLocation
import com.androiddevelopers.villabuluyorum.databinding.FragmentEntryBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentRegisterBinding
import com.androiddevelopers.villabuluyorum.view.BottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.login.EntryViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.login.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class EntryFragment : Fragment() {


    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      /*
        FirebaseAuth.getInstance().signOut()
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut().addOnCompleteListener {
            // Kullanıcıyı başka bir aktiviteye yönlendir veya ek işlemler yap
            // Örneğin, giriş ekranına geri dönme gibi
        }

       */

        binding.btnSignIn.setOnClickListener{
            val action = EntryFragmentDirections.actionEntryFragmentToSignInFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.btnRegister.setOnClickListener{
            val action = EntryFragmentDirections.actionEntryFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null){
            if (user.isEmailVerified){
                gotoHome()
            }else{
                Toast.makeText(requireContext(), "E-postanızı doğrulayın", Toast.LENGTH_SHORT).show()
                auth.signOut()
            }
        }
    }
    private fun gotoHome() {
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }



}