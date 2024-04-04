package com.androiddevelopers.villabuluyorum.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.androiddevelopers.villabuluyorum.data.MySqlDbHelper
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.viewmodel.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            val list = mutableListOf<String>()
            try {
                val connection = MySqlDbHelper.connect(requireContext())
                connection?.let {
                    val statement = it.createStatement()
                    val resultSet = statement.executeQuery("SELECT * FROM `users`")
                    while (resultSet.next()) {
                        val firstName = resultSet.getString("first_name")
                        val lastName = resultSet.getString("last_name")
                        list.add(firstName)
                        list.add(lastName)
                    }
                }
            } catch (e: Exception) {
                e.localizedMessage?.let { message ->
//                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
//                        .show()
                }
            }

            requireActivity().runOnUiThread {
                Snackbar.make(binding.root, list.toString(), Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}