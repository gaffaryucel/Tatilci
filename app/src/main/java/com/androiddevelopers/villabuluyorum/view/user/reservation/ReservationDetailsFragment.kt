package com.androiddevelopers.villabuluyorum.view.user.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.viewmodel.user.reservation.ReservationDetailsViewModel

class ReservationDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ReservationDetailsFragment()
    }

    private lateinit var viewModel: ReservationDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reservation_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReservationDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}