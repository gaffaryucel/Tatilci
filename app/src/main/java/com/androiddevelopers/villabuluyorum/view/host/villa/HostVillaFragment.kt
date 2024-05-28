package com.androiddevelopers.villabuluyorum.view.host.villa

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.adapter.HostHouseAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaBinding
import com.androiddevelopers.villabuluyorum.model.CreateVillaPageArguments
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.NotificationType
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.setupDialogs
import com.androiddevelopers.villabuluyorum.viewmodel.host.villa.HostVillaViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostVillaFragment : Fragment() {
    private val viewModel: HostVillaViewModel by viewModels()
    private var _binding: FragmentHostVillaBinding? = null
    private val binding get() = _binding!!

    private val userId: String? by lazy {
        Firebase.auth.currentUser?.uid
    }

    private val errorDialog: AlertDialog by lazy {
        AlertDialog
            .Builder(requireContext())
            .create()
    }

    private val houseAdapter: HostHouseAdapter by lazy {
        HostHouseAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaBinding.inflate(inflater, container, false)
        val view = binding.root

        userId?.let {
            viewModel.getVillasByUserId(it)
            viewModel.getUserDataByDocumentId(it)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickItems()
        observeLiveData(viewLifecycleOwner)
        binding.recyclerViewHostHouse.adapter = houseAdapter


        val chatId = requireActivity().intent.getStringExtra("chatId") ?: ""
        val reservationObject = requireActivity().intent.getStringExtra("reservation_host") ?: ""
        val homeId = requireActivity().intent.getStringExtra("home_id") ?: ""

        if (chatId.isNotEmpty()) {
            goToMessage(reservationObject)
            requireActivity().intent.removeExtra("reservation_host")
        }
        if (reservationObject.isNotEmpty()) {
            gotoReservation(reservationObject)
            requireActivity().intent.removeExtra("reservation_host")
        }
        if (homeId.isNotEmpty()) {
            goToHomeDetails(homeId)
            requireActivity().intent.removeExtra("home_id")
        }

        val item = requireActivity().intent.getStringExtra("item") ?: ""
        val type = requireActivity().intent.getStringExtra("type") ?: ""
        if (item.isNotEmpty()) {
            when (type) {
                NotificationType.HOST_RESERVATION.toString() -> {
                    gotoReservation(item)
                    requireActivity().intent.removeExtra("item")
                }

                NotificationType.COMMENT.toString()          -> {
                    goToHomeDetails(item)
                    requireActivity().intent.removeExtra("item")
                }

                else                                         -> {
                    Toast
                        .makeText(requireContext(), "Hat oluştu", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setClickItems() {
        with(binding) {
            fabHostVillaAdd.setOnClickListener {
                gotoCreateEnterPage()
            }
            ivMessages.setOnClickListener {
                goToChat()
            }
        }
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            liveDataFirebaseStatus.observe(owner) {
                when (it.status) {
                    Status.SUCCESS -> {

                    }

                    Status.LOADING -> it.data?.let { status ->
                        binding.setProgressBarVisibility = status
                    }

                    Status.ERROR   -> {
                        setupDialogs(errorDialog)
                        errorDialog.setMessage("Hata mesajı:\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            liveDataVillaList.observe(owner) { villas ->
                houseAdapter.housesList = villas.toList()
            }

            liveDataVilla.observe(owner) {
                binding.textHostUserName.text = buildString {
                    append(it.firstName)
                    append(" ")
                    append(it.lastName)
                }
            }
        }
    }

    private fun goToMessage(chatId: String) {
        val action =
            HostVillaFragmentDirections.actionNavigationHostVillaToNavigationHostMessage(chatId)
        Navigation
            .findNavController(requireView())
            .navigate(action)
    }

    private fun goToChat() {
        val action = HostVillaFragmentDirections.actionNavigationHostVillaToHostChatFragment()
        Navigation
            .findNavController(requireView())
            .navigate(action)
    }

    private fun goToHomeDetails(homeId: String) {
        // TODO: Yorum bildirimi geldiğinde gerektiği şekilde detaylarına ulaşabilmek için gerekli olan action yapılmalı
    }

    private fun gotoCreateEnterPage() {
        val directions =
            HostVillaFragmentDirections.actionNavigationHostVillaToHostVillaCreateEnterFragment(
                CreateVillaPageArguments(villa = Villa()), null
            )
        Navigation
            .findNavController(binding.root)
            .navigate(directions)
    }


    private fun gotoReservation(reservationId: String) {
        val action =
            HostVillaFragmentDirections.actionNavigationHostVillaToHostReservationDetailsFragment(
                reservationId
            )
        Navigation
            .findNavController(requireView())
            .navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}