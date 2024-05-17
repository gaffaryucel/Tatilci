package com.androiddevelopers.villabuluyorum.view.host.villa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaBinding
import com.androiddevelopers.villabuluyorum.viewmodel.host.villa.HostVillaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostVillaFragment : Fragment() {
    private val viewModel: HostVillaViewModel by viewModels()
    private var _binding: FragmentHostVillaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickItems()

        val chatId = requireActivity().intent.getStringExtra("chatId") ?: ""
        val reservationObject = requireActivity().intent.getStringExtra("reservation_host") ?: ""
        val homeId = requireActivity().intent.getStringExtra("home_id") ?: ""

        if (chatId.isNotEmpty()){
            goToMessage(reservationObject)
            requireActivity().intent.removeExtra("reservation_host")
        }
        if (reservationObject.isNotEmpty()){
            gotoReservation(reservationObject)
            requireActivity().intent.removeExtra("reservation_host")
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
    private fun goToMessage(chatId : String){
        val action = HostVillaFragmentDirections.actionNavigationHostVillaToNavigationHostMessage(chatId)
        Navigation.findNavController(requireView()).navigate(action)
    }
    private fun goToChat(){
        val action = HostVillaFragmentDirections.actionNavigationHostVillaToHostChatFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }
    private fun gotoCreateEnterPage() {
        val directions = HostVillaFragmentDirections.actionNavigationHostVillaToNavigationHostVillaCreateEnter()
        Navigation.findNavController(binding.root).navigate(directions)
    }


    private fun gotoReservation(reservationId: String) {
        val action = HostVillaFragmentDirections.actionNavigationHostVillaToHostReservationDetailsFragment(reservationId)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}