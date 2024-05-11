package com.androiddevelopers.villabuluyorum.view.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.androiddevelopers.villabuluyorum.adapter.NotificationAdapter
import com.androiddevelopers.villabuluyorum.adapter.ReservationAdapter
import com.androiddevelopers.villabuluyorum.databinding.FragmentNotificationsBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentReservationBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.notification.NotificationsViewModel
import com.androiddevelopers.villabuluyorum.viewmodel.user.reservation.ReservationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private val notificationAdapter: NotificationAdapter = NotificationAdapter()

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNotifications.adapter = notificationAdapter
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
}