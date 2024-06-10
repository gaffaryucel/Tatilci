package com.izmirsoftware.tatilci.view.host.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.izmirsoftware.tatilci.adapter.ChatAdapter
import com.izmirsoftware.tatilci.databinding.FragmentHostChatBinding
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideHostBottomNavigation
import com.izmirsoftware.tatilci.util.showHostBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.host.chat.HostChatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostChatFragment : Fragment() {

    private var _binding: FragmentHostChatBinding? = null
    private val binding get() = _binding!!

    val viewModel: HostChatViewModel by viewModels()

    private val chatAdapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostChatBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBinding()
        observeLiveData()
    }


    private fun setupBinding(){
        chatAdapter.isHost = true
        binding.rvChat.adapter = chatAdapter

        binding.svChat.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
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
        viewModel.chatRooms.observe(viewLifecycleOwner, Observer {
            chatAdapter.chatsList = it
            chatAdapter.notifyDataSetChanged()
        })
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
        viewModel.chatSearchResult.observe(viewLifecycleOwner, Observer {searchResult ->
            if (searchResult != null){
                chatAdapter.chatsList = searchResult
                chatAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        hideHostBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showHostBottomNavigation(requireActivity())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}