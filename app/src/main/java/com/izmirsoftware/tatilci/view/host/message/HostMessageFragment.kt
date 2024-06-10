package com.izmirsoftware.tatilci.view.host.message

import androidx.fragment.app.viewModels
import com.izmirsoftware.tatilci.view.chat.MessagesFragmentDirections
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.izmirsoftware.tatilci.adapter.MessageAdapter
import com.izmirsoftware.tatilci.databinding.FragmentMessagesBinding
import com.izmirsoftware.tatilci.util.NotificationTypeForActions
import com.izmirsoftware.tatilci.util.hideHostBottomNavigation
import com.izmirsoftware.tatilci.util.showHostBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.host.message.HostMessageViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HostMessageFragment : Fragment() {

    val viewModel: HostMessageViewModel by viewModels()

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter : MessageAdapter

    private var isFirst = true

    private var receiverId : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        receiverId = arguments?.getString("id")
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (receiverId == null){
            findNavController().popBackStack()
            Toast.makeText(
                requireContext(),
                "Hata daha sonra tekrar deneyin",
                Toast.LENGTH_SHORT)
                .show()
           return
        }
        viewModel.getUserData(receiverId!!)
        viewModel.getMessages(receiverId!!)

        binding.layoutUserInfo.setOnClickListener {
            goToUserProfile(receiverId)
        }

        binding.buttonSend.setOnClickListener{
            val message = binding.editTextMessage.text.toString()
            val title = "yeni mesajın var"
            if (message.isNotEmpty()){
                viewModel.sendMessage(
                    message,
                    receiverId!!
                )
                binding.editTextMessage.setText("")
                val itemCount = adapter.itemCount ?: 0
                scrollToMessage(itemCount+1)
                try {
                    viewModel.sendNotificationToReceiver(
                        title,
                        message,
                        NotificationTypeForActions.MESSAGE_RECEIVER_USER
                    )
                }catch (e : Exception){
                    Toast.makeText(requireContext(), "Hata", Toast.LENGTH_SHORT).show()
                }

            }
        }

//Data Binding
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = layoutManager
        binding.messageRecyclerView.adapter = adapter
        observeLiveData()
    }

    private fun goToUserProfile(messageReceiver: String?) {
        if (messageReceiver != null){
            val action = MessagesFragmentDirections.actionMessagesFragmentToUserProfileFragment(messageReceiver)
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    private fun observeLiveData(){
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            if (isFirst){
                adapter.messageList = it
                isFirst = false
            }else{
                adapter.messageList = it
                adapter.notifyItemInserted(it.size)
                lifecycleScope.launch {
                    delay(100)
                    binding.messageRecyclerView.scrollToPosition(adapter.messageList.size-1)
                }
            }
        })
        viewModel.receiverData.observe(viewLifecycleOwner, Observer {
            if (it.online != null){
                if (it.online!!){
                    binding.tvOnline.visibility = View.VISIBLE
                }else{
                    binding.tvOnline.visibility = View.GONE
                }
            }else{
                binding.tvOnline.visibility = View.GONE
            }
            Glide.with(requireContext()).load(it.profileImageUrl).into(binding.ivUser)
            binding.tvUserName.text = it.username
        })
    }
    override fun onResume() {
        super.onResume()
        hideHostBottomNavigation(requireActivity())
        saveUserIdInSharedPref()
    }

    override fun onPause() {
        super.onPause()
        showHostBottomNavigation(requireActivity())
        deleteUserIdInSharedPref()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveUserIdInSharedPref(){
// Veriyi kaydetmek için
        val sharedPreferences = requireContext().getSharedPreferences("chatPage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("current_chat_page_id", receiverId) // chatPageId, kullanıcının bulunduğu sayfa kimliğidir
        editor.apply()
    }
    private fun deleteUserIdInSharedPref(){
// Kayıtlı veriyi silmek için
        val sharedPreferences = requireContext().getSharedPreferences("chatPage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("current_chat_page_id")
        editor.apply()
    }

    private fun scrollToMessage(itemCount : Int) {
        if (itemCount > 0) {
            binding.messageRecyclerView.smoothScrollToPosition(itemCount)
        }
    }

}