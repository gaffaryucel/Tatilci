package com.izmirsoftware.tatilci.view.user.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.izmirsoftware.tatilci.databinding.FragmentResetPasswordBinding
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.startLoadingProcess
import com.izmirsoftware.tatilci.viewmodel.user.profile.ResetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : DialogFragment() {


    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    val viewModel: ResetPasswordViewModel by viewModels()

    private var progressDialog: ProgressDialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())

        binding.resetPasswordButton.setOnClickListener {
            val eMail = binding.emailEditText.text.toString()
            showConfirmationDialog(eMail)
        }
        observeLiveData()
    }

    private fun showConfirmationDialog(email: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Şifre sırıfrlama")
            .setMessage("$email mail adresine şifre sırıfrlama maili göndermek istediğinizden emin misiniz?\n(Mail gönderildiğinde hesabınızdan çıkış yapılacak)")
            .setPositiveButton("Evet") { dialog, _ ->
                viewModel.sendPasswordResetEmail(email)
                dialog.dismiss()
            }
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    private fun observeLiveData(){
        viewModel.resetPasswordMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    progressDialog?.dismiss()
                    viewModel.signOutAndExit(requireContext())
                }
                Status.LOADING -> {
                    startLoadingProcess(progressDialog)
                }
                Status.ERROR -> {
                    progressDialog?.dismiss()
                    if (it.message.equals("mail")){
                        Toast.makeText(requireContext(), "Hatalı mail adresi", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}