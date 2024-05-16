package com.androiddevelopers.villabuluyorum.view.host

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.ActivityHostBottomNavigationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostBottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHostBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //host navigation fragmente erişiyoruz
        val navHostFragment =
            supportFragmentManager
                .findFragmentById(binding.navHostFragmentActivityHostBottomNavigation.id) as NavHostFragment?
        val navControl = navHostFragment?.navController

        navControl?.let {
            //bottom navigatiton ile host navigation fragment bağlantısı yapıldı
            //çalışması için bottom menü item idleri ile fragment idleri aynı olması lazım
            NavigationUI.setupWithNavController(binding.hostNavView, navControl)

            //Bottom Navigation item'leri tekrar seçildiğinde sayfayı yenilemesi için eklendi
            binding.hostNavView.setOnItemReselectedListener {
                when (it.itemId) {
                    R.id.navigation_host_villa -> navControl.navigate(R.id.action_global_navigation_host_villa)
                    R.id.navigation_host_reservation -> navControl.navigate(R.id.action_global_navigation_host_reservation)
                    R.id.navigation_host_notification -> navControl.navigate(R.id.action_global_navigation_host_notification)
                    R.id.navigation_host_profile -> navControl.navigate(R.id.action_global_navigation_host_profile)
                }
            }
        }
    }
}