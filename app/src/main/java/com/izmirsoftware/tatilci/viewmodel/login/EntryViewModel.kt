package com.izmirsoftware.tatilci.viewmodel.login

import androidx.lifecycle.ViewModel
import com.izmirsoftware.tatilci.repo.SharedPreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EntryViewModel
@Inject
constructor(
    private val sharedPreferencesRepo: SharedPreferencesRepo
) : ViewModel() {
    fun getStartMode(): String {
        return sharedPreferencesRepo.getStartMode()
    }
}