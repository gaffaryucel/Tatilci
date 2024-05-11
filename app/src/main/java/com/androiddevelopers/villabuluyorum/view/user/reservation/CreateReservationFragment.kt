package com.androiddevelopers.villabuluyorum.view.user.reservation

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.villabuluyorum.databinding.FragmentCreateReservationBinding
import com.androiddevelopers.villabuluyorum.databinding.MergeItemCoverImageBinding
import com.androiddevelopers.villabuluyorum.model.PaymentMethod
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.NotificationType
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.getCurrentTime
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.util.startLoadingProcess
import com.androiddevelopers.villabuluyorum.viewmodel.user.reservation.CreateReservationViewModel
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CreateReservationFragment : Fragment() {

    private var _binding: FragmentCreateReservationBinding? = null
    private val binding get() = _binding!!

    val viewModel: CreateReservationViewModel by viewModels()

    private var _mergeBinding: MergeItemCoverImageBinding? = null
    private val mergeBinding get() = _mergeBinding!!

    private lateinit var villaId: String
    private lateinit var token: String
    private var paymentMethod = PaymentMethod.CASH

    private var progressDialog: ProgressDialog? = null

    private var nightlyRate = 0
    private var nightCount = 0
    private var price = 0
    private var nyVilla = Villa()

    private var startDate : String = ""
    private var endDate : String = ""

    private var number = 1
    private var currentUser = UserModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReservationBinding.inflate(inflater, container, false)
        _mergeBinding = MergeItemCoverImageBinding.bind(binding.root)
        villaId = arguments?.getString("villa_id") ?: ""
        token = arguments?.getString("token") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        setRadioButtonClickListener()
        progressDialog = ProgressDialog(requireContext())

        if (villaId.isNotEmpty()) {
            viewModel.getVillaByIdFromFirestore(villaId)
        }
        binding.tvSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnReserve.setOnClickListener {
            saveAndReserve()
        }
        binding.btnPlus.setOnClickListener {
            incrementNumber()
        }
        binding.btnMinus.setOnClickListener {
            decrementNumber()
        }

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
                liveDataFirebaseVilla.observe(viewLifecycleOwner) { villa ->
                    nyVilla = villa
                    mergeBinding.textDetailTitle.text = villa.villaName
                    mergeBinding.textDetailAddress.text = villa.locationAddress
                    mergeBinding.textDetailBedRoom.text = villa.bedroomCount.toString() + " Yatak odası"
                    mergeBinding.textDetailBathRoom.text = villa.bathroomCount.toString() + " Banyo"
                    Glide.with(requireContext()).load(villa.coverImage)
                        .into(mergeBinding.imageTitle)
                    val minStay = villa.minStayDuration ?: 1
                    nightlyRate = villa.nightlyRate?.toInt() ?: 3000
                    if (nightCount != 0 && nightCount >= minStay) {
                        price = nightCount * nightlyRate
                    } else {
                        price = minStay * nightlyRate
                    }
                    binding.tvMainPrice.text = "Minimum ${minStay} gece x ₺${nightlyRate}"
                    binding.tvMainPriceTotal.text = "₺$price"
                }
                currentUserData.observe(viewLifecycleOwner) { user ->
                    currentUser = user
                }
                liveDataReserveStatus.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            progressDialog?.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Rezervasyon Talep Edildi",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }

                        Status.LOADING -> {
                            startLoadingProcess(progressDialog)
                        }

                        Status.ERROR -> {
                            Toast.makeText(
                                requireContext(),
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                            progressDialog?.dismiss()
                        }

                    }
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        // dateRangePicker oluştur ve tarih seçimini dinle
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Rezervasyon süresini belirleyin")
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { dateRange ->
            val startDateMillis = dateRange.first
            val endDateMillis = dateRange.second

            startDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(startDateMillis))
            endDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(endDateMillis))

            binding.tvSelectedStartDate.text = startDate
            binding.tvSelectedEndDate.text = endDate
            binding.tvSelectDate.text = "Düzenle"

            // Gece sayısını hesaplayalım
            val startDateCal = Calendar.getInstance().apply { timeInMillis = startDateMillis }
            val endDateCal = Calendar.getInstance().apply { timeInMillis = endDateMillis }
            val diffInMillis = endDateCal.timeInMillis - startDateCal.timeInMillis
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
            nightCount = diffInDays.toInt() + 1 // Başlangıç ve bitiş tarihi arasındaki gün sayısı
            price = nightCount * nightlyRate
            // Gece sayısını kullanabiliriz
            binding.tvMainPrice.text = "${nightCount} gece x ₺${nightlyRate}"
            binding.tvMainPriceTotal.text = "₺${nightCount*nightlyRate}"
            binding.btnReserve.text = "${nightCount} gecelik Rezervasyon yap"
        }
        dateRangePicker.show(parentFragmentManager, "DATE_RANGE_PICKER_TAG")
    }

    private fun saveAndReserve() {
        val reservationId = UUID.randomUUID().toString()
        if (startDate.isNotEmpty() && endDate.isNotEmpty()){
            if (binding.cb1.isChecked && binding.cb2.isChecked){
                viewModel.createReservationInstance(
                    reservationId = reservationId,
                    villaId = villaId ?: "",
                    hostId = nyVilla.hostId ?: "",
                    startDate = binding.tvSelectedStartDate.text.toString(),
                    endDate = binding.tvSelectedEndDate.text.toString(),
                    nights = nightCount,
                    totalPrice = price,
                    guestCount = number,
                    paymentMethod = paymentMethod,
                    nightlyRate,
                    nyVilla.propertyType ?: PropertyType.HOUSE,
                    nyVilla.coverImage ?: "",
                    nyVilla.bedroomCount ?: 0,
                    nyVilla.bathroomCount ?: 0,
                    nyVilla.villaName ?: "",
                ).also {
                    viewModel.makeReservation(it)
                    InAppNotificationModel(
                        userId = currentUser.userId,
                        notificationType = NotificationType.HOST_RESERVATION,
                        notificationId = UUID.randomUUID().toString(),
                        userName =  currentUser.firstName+" "+currentUser.lastName,
                        title =  "Rezervasyon Talebi Var",
                        message = "${currentUser.firstName+" "+currentUser.lastName} isimli kullanıcı size rezervasyon talebinde bulundu",
                        userImage = currentUser.profileImageUrl,
                        imageUrl = nyVilla.coverImage,
                        userToken = token,
                        time = getCurrentTime()
                    ).also { notification->
                        viewModel.sendNotification(
                            notification = notification,
                            reservationId = reservationId,
                            type = NotificationTypeForActions.HOST_RESERVATION,
                        )
                    }
                }
            }else{
                Toast.makeText(requireContext(), "Lütfen şartları kabul edin", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(requireContext(), "Lütfen rezervasyon süresini belirtin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRadioButtonClickListener() {
        binding.radioCash.setOnClickListener {
            paymentMethod = PaymentMethod.CASH
        }
        binding.radioBankTransfer.setOnClickListener{
            paymentMethod = PaymentMethod.BANK_TRANSFER

        }
        binding.radioCreditCard.setOnClickListener{
            paymentMethod = PaymentMethod.CREDIT_OR_DEBIT_CARD
        }
    }
    private fun incrementNumber() {
        number++
        binding.tvNumber.text = number.toString()
    }

    private fun decrementNumber() {
        if (number > 1) {
            number--
            binding.tvNumber.text = number.toString()
        }
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