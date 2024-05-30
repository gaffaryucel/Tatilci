package com.androiddevelopers.villabuluyorum.view.user.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.databinding.FragmentContactUsBinding
import com.androiddevelopers.villabuluyorum.databinding.FragmentProfileBinding
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation

class ContactUsFragment : Fragment() {
    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
    }
    private fun setupWebView() {
        val webView: WebView = binding.webView
        webView.webViewClient = WebViewClient()
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true

        webView.loadUrl("https://soomasgrubu.com/") // Buraya istediğiniz URL'yi ekleyin
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }
}