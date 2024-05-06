package com.androiddevelopers.villabuluyorum.view.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.databinding.FragmentEntryBinding
import com.androiddevelopers.villabuluyorum.view.host.HostBottomNavigationActivity
import com.androiddevelopers.villabuluyorum.view.user.BottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.login.EntryViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntryFragment : Fragment() {
    private val viewModel: EntryViewModel by viewModels()
    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!
    private var selectionStartMode: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectionStartMode = viewModel.getStartMode()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        return binding.root
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

        binding.btnSignIn.setOnClickListener {
            val action = EntryFragmentDirections.actionEntryFragmentToSignInFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.btnRegister.setOnClickListener {
            val action = EntryFragmentDirections.actionEntryFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                if (selectionStartMode == "host") {
                    gotoHostHome()
                } else {
                    gotoHome()
                }
            } else {
                if (user.phoneNumber!!.isNotEmpty()) {
                    if (selectionStartMode == "host") {
                        gotoHostHome()
                    } else {
                        gotoHome()
                    }
                }else{
                    auth.signOut()
                    Toast.makeText(requireContext(), "E-postanızı doğrulayın", Toast.LENGTH_SHORT)
                        .show() 
                }
            }
        }
    }


    private fun gotoHome() {
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }

    private fun gotoHostHome() {
        val intent = Intent(requireContext(), HostBottomNavigationActivity::class.java)
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }


}