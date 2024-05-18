package com.androiddevelopers.villabuluyorum.view.user.review

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import com.androiddevelopers.villabuluyorum.databinding.FragmentReviewDialogBinding
import com.androiddevelopers.villabuluyorum.view.user.villa.HomeFragmentDirections

class ReviewDialogFragment : DialogFragment() {

    private var _binding: FragmentReviewDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().applicationContext.getSharedPreferences("review", Context.MODE_PRIVATE)

        binding.buttonRateNow.setOnClickListener {
            // Puanlama ve yorum yapma ekranına yönlendirme kodu buraya gelecek
            onClick?.invoke("click")
            sharedPref.edit().putBoolean("is_reviewed", true).apply()
            dismiss()
        }

        binding.buttonRateLater.setOnClickListener {
            sharedPref.edit().putBoolean("is_reviewed", true).apply()
            dismiss()
        }
    }
    var onClick: ((String) -> Unit)? = null

}

