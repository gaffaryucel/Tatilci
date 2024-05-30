package com.androiddevelopers.villabuluyorum.view.user.villa

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.BestHouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.MyLocation
import com.androiddevelopers.villabuluyorum.databinding.FragmentHomeBinding
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.util.NotificationType
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.view.host.profile.HostProfileFragmentDirections
import com.androiddevelopers.villabuluyorum.view.login.RegisterFragmentDirections
import com.androiddevelopers.villabuluyorum.view.user.profile.ProfileFragmentDirections
import com.androiddevelopers.villabuluyorum.view.user.review.ReviewDialogFragment
import com.androiddevelopers.villabuluyorum.viewmodel.user.villa.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val viewModel: HomeViewModel by viewModels()

    private lateinit var bestHouseAdapter: BestHouseAdapter
    private val provinceList = mutableListOf<Province>()

    private var location: MyLocation? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    private var isStarted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        bestHouseAdapter = BestHouseAdapter()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val item = requireActivity().intent.getStringExtra("item") ?: ""
        val type = requireActivity().intent.getStringExtra("type") ?: ""

        val chatId = requireActivity().intent.getStringExtra("chatId") ?: ""
        val reservationHost = requireActivity().intent.getStringExtra("reservation_host") ?: ""

        if (item.isNotEmpty()) {
            when (type) {
                NotificationType.RESERVATION_STATUS_CHANGE.toString() -> {
                    gotoReservation(item)
                    requireActivity().intent.removeExtra("item")
                }

                else                                                  -> {
                    Toast.makeText(requireContext(), "Hat oluştu", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (chatId.isNotEmpty()) {
            goToChat(chatId)
            requireActivity().intent.removeExtra("chatId")
        }
        if (reservationHost.isNotEmpty()) {
            gotoReservation(reservationHost)
            requireActivity().intent.removeExtra("reservation_host")
        }

        setupBindingItems()
    }

    private fun observeLiveData() {
        viewModel.firebaseMessage.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.layoutHome.visibility = View.VISIBLE
                    binding.pbHome.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }

                Status.LOADING -> {
                    binding.layoutHome.visibility = View.GONE
                    binding.pbHome.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }

                Status.ERROR   -> {
                    binding.layoutHome.visibility = View.GONE
                    binding.pbHome.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }

        viewModel.bestVillas.observe(viewLifecycleOwner) { villas ->
            if (villas != null) {
                bestHouseAdapter.villaList = villas
                binding.pbHome.visibility = View.GONE
            }
        }

        viewModel.rateReservations.observe(viewLifecycleOwner) {
            if (it) {
                if (!isStarted) {
                    val reviewDialog = ReviewDialogFragment()
                    reviewDialog.isCancelable = false
                    reviewDialog.show(childFragmentManager, "ReviewDialog")
                    reviewDialog.onClick = { c ->
                        if (c) {
                            val action =
                                HomeFragmentDirections.actionNavigationHomeToReviewFragment()
                            Navigation.findNavController(requireView()).navigate(action)
                        } else {
                            viewModel.notReviewReservations()
                        }
                    }
                    isStarted = true
                }
            }
        }

        viewModel.currentUserData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                latitude = user.latitude
                longitude = user.longitude
                if (latitude != null && longitude != null){
                    println("if (latitude != null")
                    binding.tvNoLocation.visibility= View.GONE
                    location = MyLocation(latitude!!, longitude!!)
                    val closeVillasAdapter = HouseAdapter(location!!)
                    viewModel.closeVillas.observe(viewLifecycleOwner) { villas ->
                        if (villas.isNotEmpty()) {
                            binding.rvCloseHomes.adapter = closeVillasAdapter
                            closeVillasAdapter.housesList = villas
                            binding.pbHome.visibility = View.GONE
                        } else {
                            binding.pbHome.visibility = View.GONE
                            binding.rvCloseHomes.visibility = View.GONE
                            binding.tvEmptyList.visibility = View.VISIBLE
                        }
                    }
                }else{
                    lifecycleScope.launch {
                        binding.tvNoLocation.visibility = View.VISIBLE
                        println("delay(500)")
                        delay(200)
                        showPopup()
                    }
                }

                if (user.notificationRead == false) {
                    binding.ivNotReadNotification.visibility = View.VISIBLE
                } else {
                    binding.ivNotReadNotification.visibility = View.INVISIBLE
                }
            }
        }

        viewModel.liveDataProvinceFromRoom.observe(viewLifecycleOwner) {
            provinceList.clear()
            provinceList.addAll(it.toList())

            val listName = it.map { province -> province.name.toString() }

            binding.dropDownCity.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listName
                )
            )
        }

        viewModel.notifyUser.observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS -> {
                    println("notifyUser SUCCESS")
                    Toast.makeText(requireContext(), "Konum Güncellendi", Toast.LENGTH_SHORT).show()
                    viewModel.getCurrentUserData()
                    viewModel.resetNotifyMessage()
                }
                Status.ERROR -> {
                    println("ERROR")
                    Toast.makeText(requireContext(), "Bir hata oluştu", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {

                }
            }

        }
    }

    private fun setupBindingItems() {
        binding.rvBest.adapter = bestHouseAdapter

        binding.sv.isClickable = false
        binding.sv.isFocusable = false
        binding.sv.isFocusableInTouchMode = false

        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation) as NavHostFragment?
        val navControl = navHostFragment?.navController

        binding.searchView.setOnClickListener {
            navControl?.navigate(R.id.action_global_navigation_search)
        }

        binding.ivNotifications.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationNotifications()
            Navigation.findNavController(it).navigate(action)
        }

        binding.ivMessages.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToChatsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.dropDownCity.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = binding.dropDownCity.adapter.getItem(position).toString()
            viewModel.getCloseVillas(selectedCity, 20)
        }

        binding.dropDownCity.setText(buildString {
            append("Izmir")
        })

    }

    private fun goToChat(chatId: String) {
        val action = HomeFragmentDirections.actionNavigationHomeToMessagesFragment(chatId)
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun gotoReservation(reservationId: String) {
        val action =
            HomeFragmentDirections.actionNavigationHomeToReservationDetailsFragment(reservationId)
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onResume() {
        super.onResume()
        binding.pbHome.visibility = View.VISIBLE
        observeLiveData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.fragment_location_selection, null)
        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.popup_anim)
        popupWindow.animationStyle = android.R.style.Animation_Dialog
        popupWindow.contentView.startAnimation(animation)

        popupWindow.showAtLocation(
            requireActivity().findViewById(R.id.nav_host_fragment_activity_bottom_navigation),
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
            0,
            0
        )
        val option1 = popupView.findViewById<Button>(R.id.enableLocationButton)
        val option2 = popupView.findViewById<TextView>(R.id.noThanks)
        option1.setOnClickListener {
            checkAndRequestGPSPermission()
            popupWindow.dismiss()
        }
        option2.setOnClickListener {
            setDefaultLocation()
            popupWindow.dismiss()
        }

    }



    private fun setDefaultLocation() {
        viewModel.updateUserLocation(41.0369, 28.9858)
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Konum bilgisi")
        alertDialogBuilder.setMessage("Konum verinize ulaşamadık, Konumunuz geçici bir süre için İstanbul olarak ayarlandı.\nİstediğiniz zaman profile sayfanızdan değiştirebilirsiniz.")
        alertDialogBuilder.setPositiveButton("Kapat") { _, _ ->

        }
        val dialog = alertDialogBuilder.create()
        dialog.show()
    }
    private fun checkAndRequestGPSPermission() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGPSEnabled) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("GPS Ayarları")
            alertDialogBuilder.setMessage("GPS ayarları kapalı. Ayarlara gidip açmak ister misiniz?")
            alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
                // Kullanıcı Evet'i seçti, GPS ayarlarını açmak için ayarlara yönlendir
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(intent)
            }
            alertDialogBuilder.setNegativeButton("Hayır") { dialog, _ ->
                // Kullanıcı Hayır'ı seçti, işlemi iptal et
                Toast.makeText(requireContext(), "konum izni verilmedi", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            val dialog = alertDialogBuilder.create()
            dialog.show()
        } else {
            lifecycleScope.launch {
                delay(200)
                getLastKnownLocation()
            }
        }
    }
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

// get latitude and longitude
        val location = fusedLocationProviderClient.lastLocation
        location.addOnSuccessListener {
            if (it != null) {
                val lat = it.latitude
                val long = it.longitude
                viewModel.updateUserLocation(lat,long)
            }else{
                setDefaultLocation()
            }
        }.addOnFailureListener {
            setDefaultLocation()
        }

    }
}
