package com.androiddevelopers.villabuluyorum.viewmodel.host.profile

import androidx.lifecycle.ViewModel
import com.androiddevelopers.villabuluyorum.repo.SharedPreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HostProfileViewModel
@Inject
constructor(
    private val sharedPreferencesRepo: SharedPreferencesRepo
) : ViewModel() {
    fun setStartModeUser() {
        sharedPreferencesRepo.setStartModeUser()
    }
}