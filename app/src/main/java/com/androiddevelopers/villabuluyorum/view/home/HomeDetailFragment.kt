package com.androiddevelopers.villabuluyorum.view.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.navArgs
import com.androiddevelopers.villabuluyorum.databinding.FragmentHomeDetailBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.home.HomeDetailViewModel

class HomeDetailFragment : Fragment() {
    private var _binding: FragmentHomeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeDetailViewModel by viewModels()

    private lateinit var errorDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args: HomeDetailFragmentArgs by navArgs()
        val id = args.villaId

        viewModel.getVillaByIdFromFirestore(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeDetailBinding.inflate(inflater, container, false)

        binding.setProgressBar = false
        errorDialog = AlertDialog.Builder(requireContext()).create()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData(viewLifecycleOwner)
        setupDialogs()
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            liveDataFirebaseStatus.observe(owner) {
                when (it.status) {
                    Status.SUCCESS -> {

                    }

                    Status.LOADING -> it.data?.let { state -> binding.setProgressBar = state }

                    Status.ERROR -> {
                        errorDialog.setMessage("Hata mesajı:\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            liveDataFirebaseVilla.observe(owner) {
                binding.villa = it
            }

            liveDataFirebaseUser.observe(owner) {
                binding.user = it
            }


        }
    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle("Veriler alınırken hata oluştu.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}