package com.androiddevelopers.villabuluyorum.view.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.profile.EditProfileDetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditProfileDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = EditProfileDetailsFragment()
    }

    private lateinit var viewModel: EditProfileDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditProfileDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(activity)
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(activity)
    }

}
