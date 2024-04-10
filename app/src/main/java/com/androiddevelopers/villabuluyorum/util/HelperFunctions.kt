package com.androiddevelopers.villabuluyorum.util

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.androiddevelopers.villabuluyorum.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun hideBottomNavigation(act : FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.nav_view)
    bottomNavigationView?.visibility = View.GONE
}

fun showBottomNavigation(act : FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.nav_view)
    bottomNavigationView?.visibility = View.VISIBLE
}