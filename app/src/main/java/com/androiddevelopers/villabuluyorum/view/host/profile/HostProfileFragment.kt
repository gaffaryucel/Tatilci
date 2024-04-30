package com.androiddevelopers.villabuluyorum.view.host.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostProfileBinding
import com.androiddevelopers.villabuluyorum.view.user.BottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.host.profile.HostProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostProfileFragment : Fragment() {
    private val viewModel: HostProfileViewModel by viewModels()
    private var _binding: FragmentHostProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonHostProfileChangeMode.setOnClickListener {
            gotoUserHome()
            viewModel.setStartModeUser()
        }
    }

    private fun gotoUserHome() {
        val activity = requireActivity()
        val intent = Intent(activity, BottomNavigationActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}