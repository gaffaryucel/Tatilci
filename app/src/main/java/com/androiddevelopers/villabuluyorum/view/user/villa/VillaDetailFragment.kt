package com.androiddevelopers.villabuluyorum.view.user.villa

import android.app.AlertDialog
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.androiddevelopers.villabuluyorum.adapter.ViewPagerAdapterForVillaDetail
import com.androiddevelopers.villabuluyorum.databinding.FragmentVillaDetailBinding
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.user.villa.VillaDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class VillaDetailFragment : Fragment() {
    private var _binding: FragmentVillaDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VillaDetailViewModel by viewModels()

    private lateinit var errorDialog: AlertDialog

    private var viewPagerAdapter = ViewPagerAdapterForVillaDetail()

    private lateinit var geocoder: Geocoder

    private var geocoderLocation: List<Address> = listOf()

    private var villaId: String? = null
    private var myUser: UserModel? = null
    private var hostId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: VillaDetailFragmentArgs by navArgs()
        val id = args.villaId

        viewModel.getVillaByIdFromFirestore(id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVillaDetailBinding.inflate(inflater, container, false)

        val view = binding.root

        geocoder = Geocoder(view.context, Locale.getDefault())

        with(binding) {
            setProgressBarVisibility = false
            setViewPagerVisibility = false
        }

        errorDialog = AlertDialog.Builder(requireContext()).create()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialogs()

        with(binding) {
            //viewpager adapter ve indicatoru set ediyoruz
            viewPagerVillaDetail.adapter = viewPagerAdapter
            indicatorVillaDetail.setViewPager(viewPagerVillaDetail)

            buttonDetailRent.setOnClickListener {
                villaId?.let { id -> gotoReservation(id, it) }
            }

            userInfoLayout.setOnClickListener {
                hostId?.let { id -> goToOwnerProfile(id, it) }
            }
        }
    }

    private fun getGeocoderLocation(villa: Villa) {
        val address = buildString {
            append(villa.locationNeighborhoodOrVillage)
            append(",")
            append(villa.locationDistrict)
            append(",")
            append(villa.locationProvince)
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(address, 1) {
                    geocoderLocation = it
                }
            } else {

                lifecycleScope.launch {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(address, 1)?.let {
                        geocoderLocation = it
                    }
                }
            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(binding) {
            with(viewModel) {
                liveDataFirebaseStatus.observe(owner) {
                    when (it.status) {
                        Status.SUCCESS -> {

                        }

                        Status.LOADING -> it.data?.let { state ->
                            setProgressBarVisibility = state
                        }

                        Status.ERROR -> {
                            errorDialog.setMessage("Hata mesajı:\n${it.message}")
                            errorDialog.show()
                        }
                    }
                }

                liveDataFirebaseVilla.observe(owner) {
                    villa = it
                    villaId = it.villaId
                    hostId = it.hostId

                    setViewsVillaDetail(it)

                    hostId?.let { id ->
                        getUserByIdFromFirestore(id)
                    }

                    getGeocoderLocation(it)

                    it.otherImages?.toList()?.let { images ->
                        if (images.isNotEmpty()) {
                            viewPagerAdapter.refreshList(images)
                            //indicatoru viewpager yeni liste ile set ediyoruz
                            binding.indicatorVillaDetail.setViewPager(binding.viewPagerVillaDetail)

                            binding.setViewPagerVisibility = true
                        } else {
                            binding.setViewPagerVisibility = false
                        }
                    } ?: run {
                        binding.setViewPagerVisibility = false
                    }
                }

                liveDataFirebaseUser.observe(owner) {
                    user = it
                    myUser = it
                }
            }
        }
    }

    private fun setViewsVillaDetail(villaModel: Villa) {
        with(binding) {
            textMinStayDurationVillaDetail.visibility = View.GONE
            textWifiVillaDetail.visibility = View.GONE
            textPoolVillaDetail.visibility = View.GONE
            textQuietAreaVillaDetail.visibility = View.GONE
            textInteriorDesignVillaDetail.visibility = View.GONE
            textGardenAreaDesignVillaDetail.visibility = View.GONE
            textAttractionsVillaDetail.visibility = View.GONE
            textNoVillaDetail.visibility = View.VISIBLE

            villaModel.minStayDuration?.let {
                textMinStayDurationVillaDetail.visibility = View.VISIBLE
                textNoVillaDetail.visibility = View.GONE
            }

            villaModel.hasWifi?.let {
                if (it) {
                    textWifiVillaDetail.visibility = View.VISIBLE
                    textNoVillaDetail.visibility = View.GONE
                }
            }

            villaModel.hasPool?.let {
                if (it) {
                    textPoolVillaDetail.visibility = View.VISIBLE
                    textNoVillaDetail.visibility = View.GONE
                }
            }

            villaModel.isQuietArea?.let {
                if (it) {
                    textQuietAreaVillaDetail.visibility = View.VISIBLE
                    textNoVillaDetail.visibility = View.GONE
                }
            }

            villaModel.interiorDesign?.let {
                textInteriorDesignVillaDetail.visibility = View.VISIBLE
                textNoVillaDetail.visibility = View.GONE
            }

            villaModel.gardenArea?.let {
                textGardenAreaDesignVillaDetail.visibility = View.VISIBLE
                textNoVillaDetail.visibility = View.GONE
            }

            //TODO: Turistik yerler için create sayfasına ekleme yap
            villaModel.attractions?.let {
                if (it.isNotEmpty()) {
                    textAttractionsListVillaDetail.text = buildString {
                        append("Turistik yerler:\n")
                        for (s in it) {
                            append("$s\n")
                        }
                    }
                    textAttractionsVillaDetail.visibility = View.VISIBLE
                    textNoVillaDetail.visibility = View.GONE
                }
            }
        }

    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle("Veriler alınırken hata oluştu.")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, "Tamam"
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    private fun gotoReservation(id: String, view: View) {
        val action = VillaDetailFragmentDirections.actionVillaDetailFragmentToCreateReservationFragment(
            id,
            myUser?.token.toString()
        )
        Navigation.findNavController(view).navigate(action)
    }

    private fun goToOwnerProfile(id: String, view: View) {
        val action = VillaDetailFragmentDirections.actionVillaDetailFragmentToUserProfileFragment(id)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onResume() {
        super.onResume()
        observeLiveData(viewLifecycleOwner)
        hideBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
