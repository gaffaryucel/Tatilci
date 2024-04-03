package com.androiddevelopers.villabuluyorum.view.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentEntryBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentRegisterBinding
import com.androiddevelopers.villabuluyorum.viewmodel.login.EntryViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.login.RegisterViewModel

class EntryFragment : Fragment() {


    private var _binding: FragmentEntryBinding? = null
    private val binding get() = _binding!!


    private lateinit var viewModel: EntryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EntryViewModel::class.java)
        val view = binding.root
        return view
    }


}