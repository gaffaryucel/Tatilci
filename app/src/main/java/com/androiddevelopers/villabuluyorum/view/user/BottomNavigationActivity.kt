package com.androiddevelopers.villabuluyorum.view.user

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.ActivityBottomNavigationBinding
import com.androiddevelopers.villabuluyorum.viewmodel.chat.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavigationActivity : AppCompatActivity() {

    val viewModel: MessagesViewModel by viewModels()

    private lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //host navigation fragmente erişiyoruz
        val navHostFragment =
            supportFragmentManager
                .findFragmentById(binding.navHostFragmentActivityBottomNavigation.id) as NavHostFragment?
        val navControl = navHostFragment?.navController

        navControl?.let {
            //bottom navigatiton ile host navigation fragment bağlantısı yapıldı
            //çalışması için bottom menü item idleri ile fragment idleri aynı olması lazım
            NavigationUI.setupWithNavController(binding.navView, navControl)

            //Bottom Navigation item'leri tekrar seçildiğinde sayfayı yenilemesi için eklendi
            binding.navView.setOnItemReselectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> navControl.navigate(R.id.action_global_navigation_home)
                    R.id.navigation_search -> navControl.navigate(R.id.action_global_navigation_search)
                    R.id.navigation_reservation -> navControl.navigate(R.id.action_global_navigation_reservation)
                    R.id.navigation_profile -> navControl.navigate(R.id.action_global_navigation_profile)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.setUserOnline()
    }

    override fun onStop() {
        super.onStop()
        viewModel.setUserOffline()
    }
}