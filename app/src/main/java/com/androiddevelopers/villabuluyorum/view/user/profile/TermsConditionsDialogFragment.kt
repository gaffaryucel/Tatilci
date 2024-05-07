package com.androiddevelopers.villabuluyorum.view.user.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.view.host.HostBottomNavigationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsConditionsDialogFragment : DialogFragment() {
    var onClick: ((Boolean) -> Unit)? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.terms_conditions_title))
        builder.setMessage(getString(R.string.terms_conditions_description) +
                "\n\n" +
                getString(R.string.condition_1) + "\n" +
                getString(R.string.condition_2) + "\n" +
                getString(R.string.condition_3) + "\n" +
                getString(R.string.condition_4))
        builder.setPositiveButton("Tamam") { dialog, which ->
            // Kullanıcı tüm şartları kabul ettiğinde burası çalışacak
            val intent = Intent(requireActivity(), HostBottomNavigationActivity::class.java)
            requireActivity().finish()
            onClick?.invoke(true)
            startActivity(intent)
        }
        builder.setNegativeButton("İptal") { dialog, which ->
            // Kullanıcı şartları kabul etmediğinde veya iptal ettiğinde burası çalışacak
            dialog.dismiss()
        }
        return builder.create()
    }
}
