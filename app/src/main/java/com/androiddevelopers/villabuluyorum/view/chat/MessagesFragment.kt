package com.androiddevelopers.villabuluyorum.view.chat

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.ChatAdapter
import com.androiddevelopers.villabuluyorum.adapter.MessageAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentChatsBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentMessagesBinding
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.util.getCurrentTime
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.chat.ChatsViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.chat.MessagesViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    val viewModel: MessagesViewModel by viewModels()

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter : MessageAdapter

    private var isFirst = true

    private lateinit var receiverId : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        receiverId = arguments?.getString("id") ?: ""
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getUserData(receiverId)
        viewModel.getMessages(receiverId)

//Click Listeners
        binding.layoutUserInfo.setOnClickListener {
            goToUserProfile(receiverId)
        }
        binding.buttonSend.setOnClickListener{
            val message = binding.editTextMessage.text.toString()
            val title = "yeni mesajın var"
            if (message.isNotEmpty()){
                viewModel.sendMessage(
                    message,
                    receiverId
                )
                binding.editTextMessage.setText("")
                val itemCount = adapter.itemCount ?: 0
                scrollToMessage(itemCount+1)
                try {
                    viewModel.sendNotificationToReceiver(
                        title,
                        message
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
          /*
            val action = MessagesFragmentDirections.actionMessageToProfile(messageReceiver)
            Navigation.findNavController(requireView()).navigate(action)

          */
        }
    }

    private fun observeLiveData(){
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            if (isFirst){
                adapter.messageList = it
                adapter.notifyDataSetChanged()
                isFirst = false
                println("size : "+it.size)
            }else{
                adapter.messageList = it
                adapter.notifyItemInserted(adapter.itemCount+1)
            }
        })
        viewModel.receiverData.observe(viewLifecycleOwner, Observer {
            if (it.isOnline != null){
                println("if1")
                if (it.isOnline!!){
                    binding.tvOnline.visibility = View.VISIBLE
                    println("if2")
                }else{
                    binding.tvOnline.visibility = View.GONE
                    println("else2")
                }
            }else{
                println("else1")
                binding.tvOnline.visibility = View.GONE
            }

            Glide.with(requireContext()).load(it.profileImageUrl).into(binding.ivUser)
            binding.tvUserName.text = it.username
        })
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
        saveUserIdInSharedPref()
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
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
    private fun scrollLast() {
        val itemCount = adapter?.itemCount ?: 0
        if (itemCount > 0) {
            binding.messageRecyclerView.scrollToPosition(itemCount)
        }
    }
    private fun scrollToMessage(itemCount : Int) {
        if (itemCount > 0) {
            binding.messageRecyclerView.smoothScrollToPosition(itemCount)
        }
    }

}