package com.izmirsoftware.tatilci.view.host.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.izmirsoftware.tatilci.adapter.NotificationAdapter
import com.izmirsoftware.tatilci.databinding.FragmentHostNotificationBinding
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideBottomNavigation
import com.izmirsoftware.tatilci.util.showBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.host.notification.HostNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HostNotificationFragment : Fragment() {
    private val viewModel: HostNotificationViewModel by viewModels()
    private var _binding: FragmentHostNotificationBinding? = null
    private val binding get() = _binding!!

    private val notificationAdapter: NotificationAdapter = NotificationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNotifications.adapter = notificationAdapter
        notificationAdapter.isHostMode = true
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.notificationMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbNotifications.visibility = View.GONE
                    if (it.data == true) {
                        binding.layoutEmptyList.visibility = View.GONE
                    } else {
                        binding.layoutEmptyList.visibility = View.VISIBLE
                    }
                }

                Status.LOADING -> {
                    binding.pbNotifications.visibility = View.VISIBLE
                    binding.layoutEmptyList.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbNotifications.visibility = View.GONE
                    binding.layoutEmptyList.visibility = View.VISIBLE
                }
            }
        })
        viewModel.notifications.observe(viewLifecycleOwner, Observer { notifications ->
            if (notifications != null && notifications.isNotEmpty()) {
                notificationAdapter.notificationList = notifications
                binding.layoutEmptyList.visibility = View.GONE
            } else {
                binding.layoutEmptyList.visibility = View.VISIBLE
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}