package com.androiddevelopers.villabuluyorum.view.user

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.ActivityBottomNavigationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavigationActivity : AppCompatActivity() {

    private val PREFS_FILENAME = "permission"

    private val KEY_VALUE = "location"


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
        setPermissionRequestValue(false)
    }

    private fun setPermissionRequestValue(value: Boolean) {
        val sharedPrefs: SharedPreferences =
            baseContext.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putBoolean(KEY_VALUE, value)
        editor.apply()
    }
}