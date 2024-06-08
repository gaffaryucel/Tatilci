package com.izmirsoftware.tatilci.view.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.izmirsoftware.tatilci.adapter.ChatAdapter
import com.izmirsoftware.tatilci.databinding.FragmentChatsBinding
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideBottomNavigation
import com.izmirsoftware.tatilci.util.showBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.chat.ChatsViewModel
import dagger.hilt.android.AndroidEntryPoint

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
