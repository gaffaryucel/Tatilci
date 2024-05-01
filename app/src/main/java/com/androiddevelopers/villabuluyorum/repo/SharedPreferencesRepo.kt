package com.androiddevelopers.villabuluyorum.repo

import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesRepo
@Inject
constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun setStartModeUser() {
        sharedPreferences.edit().putString("bottom_navigation", "user").apply()
    }

    fun setStartModeHost() {
        sharedPreferences.edit().putString("bottom_navigation", "host").apply()
    }

    fun getStartMode(): String {
        val selection = sharedPreferences.getString("bottom_navigation", "user")

        selection?.let {
            if (it == "host") {
                return "host"
            } else {
                return "user"
            }
        } ?: run {
            return "user"
        }
    }
}