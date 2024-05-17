package com.androiddevelopers.villabuluyorum.view.user.villa

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.adapter.BestHouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.HouseAdapter
import com.androiddevelopers.villabuluyorum.adapter.MyLocation
import com.androiddevelopers.villabuluyorum.databinding.FragmentHomeBinding
import com.androiddevelopers.villabuluyorum.model.notification.InAppNotificationModel
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.viewmodel.user.villa.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    val viewModel: HomeViewModel by viewModels()

    private val bestHouseAdapter: BestHouseAdapter = BestHouseAdapter()

    private val provinceList = mutableListOf<Province>()

    private var location: MyLocation? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double? = null
    private var longitude: Double? = null

    private val PREFS_FILENAME = "permission"

    private val KEY_VALUE = "location"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val chatId = requireActivity().intent.getStringExtra("chatId") ?: ""
        val reservationHost = requireActivity().intent.getStringExtra("reservation_host") ?: ""

        if (chatId.isNotEmpty()){
            goToChat(chatId)
            requireActivity().intent.removeExtra("chatId")
        }
        if (reservationHost.isNotEmpty()){
            gotoReservation(reservationHost)
            requireActivity().intent.removeExtra("reservation_host")
        }

        binding.rvBest.adapter = bestHouseAdapter

        binding.sv.isClickable = false
        binding.sv.isFocusable = false
        binding.sv.isFocusableInTouchMode = false

        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation) as NavHostFragment?
        val navControl = navHostFragment?.navController

        binding.searchView.setOnClickListener {
            navControl?.let {
                navControl.navigate(R.id.action_global_navigation_search)
            }
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

        if (!isPermissionRequested()) {
            requestGPSPermission(requireContext())
        }
        binding.dropDownCity.setText("İzmir")

    }

    override fun onResume() {
        super.onResume()
        binding.pbHome.visibility = View.VISIBLE
        observeLiveData()
    }
    private fun goToChat(chatId : String){
        val action = HomeFragmentDirections.actionNavigationHomeToMessagesFragment(
            chatId
        )
        Navigation.findNavController(requireView()).navigate(action)
    }
    private fun gotoReservation(reservationId : String){
        val action = HomeFragmentDirections.actionNavigationHomeToReservationDetailsFragment("villa","user",reservationId)
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun observeLiveData() {
        viewModel.firebaseMessage.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.pbHome.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                }

                Status.LOADING -> {
                    binding.pbHome.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.pbHome.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        })
        viewModel.bestVillas.observe(viewLifecycleOwner, Observer { villas ->
            if (villas != null) {
                bestHouseAdapter.villaList = villas
                binding.pbHome.visibility = View.GONE
            }
        })
        viewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                latitude = user.latitude ?: 41.00527
                longitude = user.longitude ?: 28.97696
                location = MyLocation(latitude!!, longitude!!)
                viewModel.closeVillas.observe(viewLifecycleOwner, Observer { villas ->
                    if (villas.isNotEmpty()) {
                        val closeVillasAdapter = HouseAdapter(location!!)
                        binding.rvCloseHomes.adapter = closeVillasAdapter
                        closeVillasAdapter.housesList = villas
                        binding.pbHome.visibility = View.GONE
                        binding.rvCloseHomes.visibility = View.VISIBLE
                        binding.tvEmptyList.visibility = View.GONE
                    } else {
                        binding.pbHome.visibility = View.GONE
                        binding.rvCloseHomes.visibility = View.GONE
                        binding.tvEmptyList.visibility = View.VISIBLE
                    }
                })
            }
        })
        viewModel.liveDataProvinceFromRoom.observe(viewLifecycleOwner) {
            provinceList.clear()
            provinceList.addAll(it.toList())

            val listName = it.map { province -> province.name.toString() }

            binding.dropDownCity.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    listName.toList()
                )
            )
        }
        viewModel.notifyUser.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.resetNotifyMessage()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        bestHouseAdapter.villaList = listOf()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestGPSPermission(context: Context) {
        //İzin istendiğini belirtiyoruz
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isGPSEnabled) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("GPS Ayarları")
            alertDialogBuilder.setMessage("GPS ayarları kapalı. Ayarlara gidip açmak ister misiniz?")
            alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
                // Kullanıcı Evet'i seçti, GPS ayarlarını açmak için ayarlara yönlendir
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            alertDialogBuilder.setNegativeButton("Hayır") { dialog, _ ->
                // Kullanıcı Hayır'ı seçti, işlemi iptal et
                dialog.dismiss()
                setPermissionRequestValue(true)
            }
            val dialog = alertDialogBuilder.create()
            dialog.show()
        } else {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() = lifecycleScope.launch {
        delay(500)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // İzin yoksa izin iste
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return@launch
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                latitude = it.latitude
                longitude = it.longitude
                viewModel.updateUserLocation(
                    latitude,
                    longitude
                )
            }
        }.addOnFailureListener {
            println("error : " + fusedLocationClient)
        }
        setPermissionRequestValue(true)
    }

    private fun setPermissionRequestValue(value: Boolean) {
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPrefs.edit()
        editor.putBoolean(KEY_VALUE, value)
        editor.apply()
    }

    private fun isPermissionRequested(): Boolean {
        val sharedPrefs: SharedPreferences = requireContext().getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(KEY_VALUE, false)
    }
}