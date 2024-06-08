package com.izmirsoftware.tatilci.view.user.villa

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.izmirsoftware.tatilci.adapter.HostReviewAdapter
import com.izmirsoftware.tatilci.adapter.ViewPagerAdapterForVillaDetail
import com.izmirsoftware.tatilci.databinding.FragmentVillaDetailBinding
import com.izmirsoftware.tatilci.model.UserModel
import com.izmirsoftware.tatilci.model.villa.Villa
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.Status
import com.izmirsoftware.tatilci.util.hideBottomNavigation
import com.izmirsoftware.tatilci.util.setupDialogs
import com.izmirsoftware.tatilci.util.showBottomNavigation
import com.izmirsoftware.tatilci.viewmodel.user.villa.VillaDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VillaDetailFragment : Fragment() {
    private var _binding: FragmentVillaDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VillaDetailViewModel by viewModels()

    private val errorDialog: AlertDialog by lazy {
        AlertDialog.Builder(requireContext())
            .create()
    }

    private val viewPagerAdapter: ViewPagerAdapterForVillaDetail by lazy {
        ViewPagerAdapterForVillaDetail()
    }

    private val reviewAdapter: HostReviewAdapter by lazy {
        HostReviewAdapter()
    }

    private var villaId: String? = null
    private var myUser: UserModel? = null
    private var hostId: String? = null
    private var isChatRoomExists: Boolean? = null
    private var isForSale: Boolean? = null
    private var currentUser: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: VillaDetailFragmentArgs by navArgs()
        villaId = args.villaId

        villaId?.let { id ->
            viewModel.getVillaByIdFromFirestore(id)
            viewModel.getAllReviewsByVillaId(id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVillaDetailBinding.inflate(
            inflater,
            container,
            false
        )

        val view = binding.root

        //geocoder = Geocoder(view.context, Locale.getDefault())

        with(binding) {
            setProgressBarVisibility = false
            setViewPagerVisibility = false
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        with(binding) {
            //viewpager adapter ve indicatoru set ediyoruz
            viewPagerVillaDetail.adapter = viewPagerAdapter
            indicatorVillaDetail.setViewPager(viewPagerVillaDetail)

            if (currentUser != true){
                buttonDetailRent.setOnClickListener {
                    if (isForSale == true){
                        if (isChatRoomExists == true){
                            goToMessagesFragment(hostId.toString())
                        }else{
                            if (myUser != null){
                                viewModel.createChatRoom(myUser!!)
                            }
                        }
                    }else{
                        villaId?.let { id ->
                            gotoReservation(
                                id,
                                it
                            )
                        }
                    }
                }
                buttonDetailChat.setOnClickListener {
                    if (isChatRoomExists == true){
                        goToMessagesFragment(hostId.toString())
                    }else{
                        if (myUser != null){
                            viewModel.createChatRoom(myUser!!)
                        }
                    }
                }
            }

            userInfoLayout.setOnClickListener {
                hostId?.let { id ->
                    goToOwnerProfile(
                        id,
                        it
                    )
                }
            }
        }

    }


//    private fun getGeocoderLocation(villa: Villa) {
//        val address = buildString {
//            append(villa.locationNeighborhoodOrVillage)
//            append(",")
//            append(villa.locationDistrict)
//            append(",")
//            append(villa.locationProvince)
//        }
//
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                geocoder.getFromLocationName(address, 1) {
//                    geocoderLocation = it
//                }
//            } else {
//
//                lifecycleScope.launch {
//                    @Suppress("DEPRECATION") geocoder.getFromLocationName(address, 1)?.let {
//                        geocoderLocation = it
//                    }
//                }
//            }
//
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(binding) {
            with(viewModel) {
                dataStatus.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            if(viewModel.currentUserId != myUser?.userId){
                                println("c : "+viewModel)
                                println("myUser : "+myUser?.userId)
                                goToMessagesFragment(myUser?.userId.toString())
                                viewModel._dataStatus.value = Resource.loading(null)
                            }
                        }

                        Status.LOADING -> {

                        }

                        Status.ERROR -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                liveDataFirebaseStatus.observe(owner) {
                    when (it.status) {
                        Status.SUCCESS -> {

                        }

                        Status.LOADING -> it.data?.let { state ->
                            setProgressBarVisibility = state
                        }

                        Status.ERROR   -> {
                            it.message?.let { message ->
                                setupDialogs(errorDialog)
                                errorDialog.setMessage("Hata mesajı:\n$message")
                                errorDialog.show()
                            }
                        }
                    }
                }

                liveDataFirebaseVilla.observe(owner) {
                    villa = it
                    hostId = it.hostId
                    isForSale = it.forSale
                    val id = FirebaseAuth.getInstance().currentUser?.uid.toString()
                    currentUser = it.hostId.equals(id)
                    if (isForSale == true){
                        binding.buttonDetailRent.text = "Satın Al"
                        isForSale = true
                    }else{
                        isForSale = false
                        binding.time = "Aylık"
                    }

                    setViewsVillaDetail(it)

                    hostId?.let { id ->
                        getUserByIdFromFirestore(id)
                    }

                    // getGeocoderLocation(it)

                    it.otherImages?.toList()
                        ?.let { images ->
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
                _isChatRoomExists.observe(owner) {
                    isChatRoomExists = it
                }

                liveDataFirebaseUserReviews.observe(owner) { reviewList ->
                    recyclerViewComments.adapter = reviewAdapter
                    reviewAdapter.reviewList = reviewList.toList()

                    progressBarComments.visibility = View.GONE
                    if (reviewList.isEmpty()) {
                        textNoComments.visibility = View.VISIBLE
                    } else {
                        recyclerViewComments.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun goToMessagesFragment(id : String){
        val action = VillaDetailFragmentDirections.actionVillaDetailFragmentToMessagesFragment(id)
        Navigation.findNavController(requireView()).navigate(action)
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

    private fun gotoReservation(id: String, view: View) {
        val action = VillaDetailFragmentDirections.actionVillaDetailFragmentToCreateReservationFragment(
                id,
                myUser?.token.toString()
            )
        Navigation.findNavController(view).navigate(action)
    }

    private fun goToOwnerProfile(id: String, view: View) {
        val action =
            VillaDetailFragmentDirections.actionVillaDetailFragmentToUserProfileFragment(id)
        Navigation.findNavController(view)
            .navigate(action)
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