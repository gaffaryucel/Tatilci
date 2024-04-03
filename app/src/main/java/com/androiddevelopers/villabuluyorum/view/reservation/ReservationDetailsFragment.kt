package com.androiddevelopers.villabuluyorum.view.reservation

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.viewmodel.reservation.ReservationDetailsViewModel

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