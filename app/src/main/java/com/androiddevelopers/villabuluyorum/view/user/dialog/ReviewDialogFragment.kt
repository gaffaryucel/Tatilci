package com.androiddevelopers.villabuluyorum.view.user.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.androiddevelopers.villabuluyorum.databinding.FragmentReviewDialogBinding

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

        binding.buttonRateNow.setOnClickListener {
            // Puanlama ve yorum yapma ekranına yönlendirme kodu buraya gelecek
            dismiss()
        }

        binding.buttonRateLater.setOnClickListener {
            dismiss()
        }
    }
}

