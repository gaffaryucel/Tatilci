package com.androiddevelopers.villabuluyorum.view.login

import android.app.AlertDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentSignInBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.view.BottomNavigationActivity
import com.androiddevelopers.villabuluyorum.viewmodel.login.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var viewModel: SignInViewModel

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog
    private lateinit var verifiedEmailDialog: AlertDialog
    private lateinit var forgotPasswordDialog: AlertDialog
    private lateinit var forgotPasswordSuccessDialog: AlertDialog
    private lateinit var verificationEmailSentDialog: AlertDialog
    private lateinit var verificationEmailSentErrorDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorDialog = AlertDialog.Builder(context).create()
        verifiedEmailDialog = AlertDialog.Builder(context).create()
        forgotPasswordDialog = AlertDialog.Builder(context).create()
        forgotPasswordSuccessDialog = AlertDialog.Builder(context).create()
        verificationEmailSentDialog = AlertDialog.Builder(context).create()
        verificationEmailSentErrorDialog = AlertDialog.Builder(context).create()

        setProgressBar(false)
        setupDialogs()
        observeLiveData(viewLifecycleOwner)

        with(binding) {
            btnSignIn.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassworad.text.toString()

                //email alanı boş mu?, password alanı 6 karakter ve fazlasımı kontrolünü ypıyoruz
                if (email.isNotEmpty() && password.length > 5) {
                    viewModel.login(email, password)
                } else if (email.isEmpty()) {
                    etEmail.error = "Lütfen bir e-posta adresi giriniz"
                } else {
                    etPassworad.error = "Lütfen bir şifre giriniz"
                }
            }

            // kullanıcı şifresini unuttuysa yeni şifre oluşturmak için
            tvForgotPassword.setOnClickListener {
                forgotPasswordDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "Evet"
                ) { _, _ ->
                    val email = etEmail.text.toString().trim()

                    if (email.isNotEmpty()) {
                        viewModel.forgotPassword(email)
                    } else {
                        etEmail.error = "Hayır"
                    }
                }
                forgotPasswordDialog.show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //kullanıcı daha önce giriş yaptıysa direkt ana sayfaya yönlendirmek için eklendi
        verifyEmail()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun verifyEmail() {
        viewModel.getUser()?.let {
            //kullanıcının email adresini onayladığını kontrol ediyoruz
            if (it.isEmailVerified) {
                gotoHome()
            } else {
                //kullanıcı email adresi doğrulanmadıysa uyarı mesajı görüntüler
                verifiedEmailDialog.show()
            }
        }
    }

    private fun gotoHome() {
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        intent.putExtra("login","login")
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            authState.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        verifyEmail()
                    }

                    Status.ERROR -> {
                        errorDialog.setMessage("Giriş hatası oluştu.\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            forgotPassword.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        forgotPasswordSuccessDialog.show()
                    }

                    Status.ERROR -> {
                        forgotPasswordDialog.setMessage("Giriş hatası oluştu.\n${it.message}")
                    }
                }
            }

            verificationEmailSent.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        verificationEmailSentDialog.show()
                    }

                    Status.ERROR -> {
                        verificationEmailSentErrorDialog.show()
                    }
                }
            }

        }
    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle("Giriş Hatası")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }


        with(verifiedEmailDialog) {
            setTitle("E-Posta Doğrulama")
            setMessage("E-Posta doğrulamasını yapmak istiyor musunuz?")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Evet"
            ) { _, _ ->
                viewModel.sendVerificationEmail()
                viewModel.signOut()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "Hayır"
            ) { dialog, _ ->
                dialog.cancel()
                viewModel.signOut()
            }
        }


// E-Posta doğrulama başarılı olduğunda gösterilen dialog
        with(verificationEmailSentDialog) {
            setTitle("E-Posta Doğrulama")
            setMessage("E-Posta doğrulama e-postası gönderildi.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

// E-Posta doğrulama hatası olduğunda gösterilen dialog
        with(verificationEmailSentErrorDialog) {
            setTitle("E-Posta Doğrulama Hatası")
            setMessage("E-Posta doğrulama e-postası gönderilirken bir hata oluştu. Tekrar denemek ister misiniz?")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Evet"
            ) { _, _ ->
                viewModel.sendVerificationEmail()
                viewModel.signOut()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "Hayır"
            ) { dialog, _ ->
                dialog.cancel()
                viewModel.signOut()
            }
        }

// Şifremi unuttum işlemi başarılı olduğunda gösterilen dialog
        with(forgotPasswordDialog) {
            setTitle("Şifremi Unuttum")
            setMessage("Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_NEGATIVE, "Kapat"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

// Şifremi unuttum işlemi başarılı olduğunda gösterilen dialog
        with(forgotPasswordSuccessDialog) {
            setTitle("Şifremi Unuttum")
            setMessage("Yeni şifreniz e-posta adresinize gönderildi.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

    }

    private fun setProgressBar(isVisible: Boolean) {
        if (isVisible) {
            binding.pbSignIn.visibility = View.VISIBLE
        } else {
            binding.pbSignIn.visibility = View.GONE
        }
    }

}