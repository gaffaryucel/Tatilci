package com.androiddevelopers.villabuluyorum.view.reservation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.databinding.FragmentCreateReservationBinding
import com.androiddevelopers.villabuluyorum.databinding.MergeItemCoverImageBinding
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.reservation.CreateReservationViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class CreateReservationFragment : Fragment() {

    private var _binding: FragmentCreateReservationBinding? = null
    private val binding get() = _binding!!

    val viewModel: CreateReservationViewModel by viewModels()

    private var _mergeBinding: MergeItemCoverImageBinding? = null
    private val mergeBinding get() = _mergeBinding!!

    private lateinit var villaId : String

    private var startDate = ""
    private var endDate = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReservationBinding.inflate(inflater, container, false)
        _mergeBinding = MergeItemCoverImageBinding.bind(binding.root)
        villaId = arguments?.getString("villa_id") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (villaId.isNotEmpty()){
            viewModel.getVillaByIdFromFirestore(villaId)
        }
        binding.tvStartReservationDateSelect.setOnClickListener {
            showDatePickerDialog(false)
        }

        binding.tvEndReservationDateSelect.setOnClickListener {
            showDatePickerDialog(true)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        observeLiveData()
    }
    private fun observeLiveData() {
        with(binding) {
            with(viewModel) {
                liveDataFirebaseStatus.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.pbReservation.visibility = View.GONE
                            binding.tvErrorReservation.visibility = View.GONE
                        }

                        Status.LOADING -> {
                            binding.pbReservation.visibility = View.VISIBLE
                            binding.tvErrorReservation.visibility = View.GONE
                        }

                        Status.ERROR -> {
                            binding.pbReservation.visibility = View.GONE
                            binding.tvErrorReservation.visibility = View.VISIBLE
                        }
                    }
                }

                liveDataFirebaseVilla.observe(viewLifecycleOwner) {villa->
                    mergeBinding.textDetailTitle.text =  villa.villaName
                    mergeBinding.textDetailAddress.text =  villa.locationAddress
                    mergeBinding.textDetailBedRoom.text =  villa.bedroomCount.toString()+" Yatak odası"
                    mergeBinding.textDetailBathRoom.text =  villa.bathroomCount.toString()+" Banyo"
                    Glide.with(requireContext()).load(villa.coverImage).into(mergeBinding.imageTitle)
                    val minStay = villa.minStayDuration ?: 4
                    val nightlyRate = villa.nightlyRate?.toInt() ?: 3000
                    val price = minStay * nightlyRate
                    val taxes = nightlyRate / 2
                    val totalPrice = price + taxes
                    binding.tvMainPrice.text = "Minimum ${minStay} gece x ₺${nightlyRate}"
                    binding.tvDeposit.text = "Bu rezervasyon için ₺${nightlyRate} güvenlik depozitosu gereklidir. Bu tutar, varışınızdan önce veya giriş sırasında mülk sahibi tarafından ayrı olarak tahsil edilecektir"
                    binding.tvMainPriceTotal.text = price.toString()
                    binding.tvTexes.text = taxes.toString()
                    binding.tvTotalPrice.text = totalPrice.toString()
                }
            }
        }
    }
    private fun showDatePickerDialog(isEnd : Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog oluştur ve tarih seçimini dinle
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                // Seçilen tarihi butona yaz
                val date = "$dayOfMonth/${monthOfYear + 1}/$year"
                if (isEnd){
                    if (compareDates(startDate,date)){
                        binding.tvSelectedEndDate.text = date
                        endDate = date
                    }else{
                        Toast.makeText(
                            requireContext(),
                            "Lütfen geçerli bir bitiş tarihi seçin ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }else{
                    binding.tvSelectedStartDate.text = date
                    startDate = date
                }
            },
            year,
            month,
            day
        )

        // Min. tarih olarak bugünü ayarla
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
    override fun onResume() {
        super.onResume()
        hideBottomNavigation(requireActivity())
    }
    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }
    private fun compareDates(start: String, end: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startDate = dateFormat.parse(start)
        val endDate = dateFormat.parse(end)

        // endDate > startDate ise true döndür, aksi halde false döndür
        return endDate.after(startDate)
    }
}