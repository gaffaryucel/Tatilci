package com.androiddevelopers.villabuluyorum.view.host.villa

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.androiddevelopers.villabuluyorum.adapter.HostReviewAdapter
import com.androiddevelopers.villabuluyorum.adapter.ViewPagerAdapterForVillaDetail
import com.androiddevelopers.villabuluyorum.databinding.FragmentHostVillaDetailBinding
import com.androiddevelopers.villabuluyorum.model.PropertyType
import com.androiddevelopers.villabuluyorum.model.VillaPageArgumentsModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideHostBottomNavigation
import com.androiddevelopers.villabuluyorum.util.setupDialogs
import com.androiddevelopers.villabuluyorum.util.showHostBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.user.villa.VillaDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
@AndroidEntryPoint
class HostVillaDetailFragment : Fragment() {
    private val viewModel: VillaDetailViewModel by viewModels()
    private var _binding: FragmentHostVillaDetailBinding? = null
    private val binding get() = _binding!!

    private val errorDialog: AlertDialog by lazy {
        AlertDialog.Builder(requireContext())
            .create()
    }

    private val alertDialog: AlertDialog by lazy {
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
    private var villaFromDB: Villa? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: HostVillaDetailFragmentArgs by navArgs()
        villaId = args.villaId

        villaId?.let { id ->
            viewModel.getVillaByIdFromFirestore(id)
            viewModel.getAllReviewsByVillaId(id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHostVillaDetailBinding.inflate(
            inflater, container, false
        )
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view, savedInstanceState
        )
        with(binding) { //viewpager adapter ve indicatoru set ediyoruz
            viewPagerVillaDetail.adapter = viewPagerAdapter
            indicatorVillaDetail.setViewPager(viewPagerVillaDetail)
        }

        //TODO: ilan silmek için butonu aktif et
        setClickItems()
        observeLiveData(viewLifecycleOwner)
    }

    private fun setClickItems() {
        with(binding) {
            imageEdit.setOnClickListener {
                villaId?.let { id ->
                    val directions =
                        HostVillaDetailFragmentDirections.actionHostVillaDetailFragmentToHostVillaCreateEnterFragment(
                            createVillaPageArguments = VillaPageArgumentsModel(
                                villa = Villa(
                                    villaId = id
                                )
                            ), id
                        )
                    Navigation.findNavController(binding.root)
                        .navigate(directions)
                }

            }

            imageDelete.setOnClickListener {
                with(alertDialog) {
                    setTitle("Uyarı!")
                    setMessage("Sistemde kayıtlı ilan bilgileri ve ilana ait resimler silinecek.\nBu işlem geri alınamaz silmek istediğinize emin misiniz?")
                    setCancelable(false)
                    setButton(
                        AlertDialog.BUTTON_POSITIVE, "Evet"
                    ) { dialog, which ->
                        villaFromDB?.let { villa ->
                            viewModel.deleteVillaAndImages(villa)
                        }
                    }
                    setButton(
                        AlertDialog.BUTTON_NEGATIVE, "Hayır"
                    ) { dialog, which ->
                        dialog.cancel()
                    }
                    show()
                }
            }
        }
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(binding) {
            with(viewModel) {
                liveDataFirebaseStatus.observe(owner) {
                    when (it.status) {
                        Status.SUCCESS -> it.data?.let{state ->
                            if(state) {
                                Navigation.findNavController(binding.root)
                                    .popBackStack()
                            }
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
                    villaFromDB = it

                    it.propertyType?.let { type ->
                        textPropertyType.visibility = View.VISIBLE
                        when (type) {
                            PropertyType.HOUSE       -> {
                                textPropertyType.text = buildString {
                                    append("Villa")
                                }
                            }

                            PropertyType.APARTMENT   -> {
                                textPropertyType.text = buildString {
                                    append("Apartman")
                                }
                            }

                            PropertyType.GUEST_HOUSE -> {
                                textPropertyType.text = buildString {
                                    append("Misafir Evi")
                                }
                            }

                            PropertyType.HOTEL       -> {
                                textPropertyType.text = buildString {
                                    append("Otel")
                                }
                            }
                        }
                    } ?: run {
                        textPropertyType.visibility = View.GONE
                    }

                    setViewsVillaDetail(it)

                    it.otherImages?.toList()
                        ?.let { images ->
                            if (images.isNotEmpty()) {
                                viewPagerAdapter.refreshList(images) //indicatoru viewpager yeni liste ile set ediyoruz
                                binding.indicatorVillaDetail.setViewPager(binding.viewPagerVillaDetail)

                                binding.setViewPagerVisibility = true
                            } else {
                                binding.setViewPagerVisibility = false
                            }
                        } ?: run {
                        binding.setViewPagerVisibility = false
                    }
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

    override fun onResume() {
        super.onResume()
        hideHostBottomNavigation(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        showHostBottomNavigation(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}