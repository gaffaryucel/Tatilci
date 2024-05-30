package com.androiddevelopers.villabuluyorum.util

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.model.chat.MessageModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun hideBottomNavigation(act: FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.nav_view)
    bottomNavigationView?.visibility = View.GONE
}

fun showBottomNavigation(act: FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.nav_view)
    bottomNavigationView?.visibility = View.VISIBLE
}

fun startLoadingProcess(progressDialog: ProgressDialog?) {
    progressDialog?.setMessage("Lütfen bekleyin...")
    progressDialog?.setCancelable(false)
    progressDialog?.show()
}

fun hideHostBottomNavigation(act: FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.host_nav_view)
    bottomNavigationView?.visibility = View.GONE
}

fun showHostBottomNavigation(act: FragmentActivity?) {
    val bottomNavigationView = act?.findViewById<BottomNavigationView>(R.id.host_nav_view)
    bottomNavigationView?.visibility = View.VISIBLE
}

fun setupDialogs(errorDialog: AlertDialog, activity: Activity? = null) {
    with(errorDialog) {
        setTitle("Bilgiler alınırken sorun oluştu.")
        setCancelable(false)
        setButton(
            AlertDialog.BUTTON_POSITIVE, "Tamam"
        ) { dialog, _ ->
            activity?.finish()
            dialog.cancel()
        }
    }
}
fun getCurrentTime(): String {
    val currentTime = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    val date = Date(currentTime)
    return dateFormat.format(date)
}

fun sortListByDate(yourList: List<MessageModel>): List<MessageModel> {
    return yourList.sortedBy { it.timestamp }
}
fun getCurrentDate(): String {
    val currentDate = Calendar.getInstance().time
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentDate)
}
