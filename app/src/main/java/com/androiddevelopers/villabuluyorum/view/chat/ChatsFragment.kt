package com.androiddevelopers.villabuluyorum.view.chat

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.ChatAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentChatsBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentReservationDetailsBinding
import com.androiddevelopers.villabuluyorum.databinding.MergeItemCoverImageBinding
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.chat.ChatsViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.user.reservation.ReservationDetailsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatsFragment : Fragment() {


    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    val viewModel: ChatsViewModel by viewModels()

    private val chatAdapter = ChatAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupBinding()
        observeLiveData()


    }


    private fun setupBinding(){
        binding.rvChat.adapter = chatAdapter

        binding.svChat.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchChatByUsername(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.searchChatByUsername(it)
                }
                return true
            }
        })

    }

    private fun observeLiveData(){
        viewModel.firebaseMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbHostChat.visibility = View.GONE
                    if (it.data == true) {
                        binding.layoutEmptyHostChat.visibility = View.GONE
                    } else {
                        binding.layoutEmptyHostChat.visibility = View.VISIBLE
                    }
                }

                Status.LOADING -> {
                    binding.pbHostChat.visibility = View.VISIBLE
                    binding.layoutEmptyHostChat.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbHostChat.visibility = View.GONE
                    binding.layoutEmptyHostChat.visibility = View.VISIBLE
                }
            }
        })
        viewModel.chatRooms.observe(viewLifecycleOwner, Observer {
            chatAdapter.chatsList = it
            chatAdapter.notifyDataSetChanged()
        })

        viewModel.chatSearchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                chatAdapter.chatsList = searchResult
                chatAdapter.notifyDataSetChanged()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
