package com.androiddevelopers.villabuluyorum.view.villa

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
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.util.Status
import com.androiddevelopers.villabuluyorum.util.hideBottomNavigation
import com.androiddevelopers.villabuluyorum.util.showBottomNavigation
import com.androiddevelopers.villabuluyorum.viewmodel.villa.VillaDetailViewModel
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
        observeLiveData(viewLifecycleOwner)
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
        val action =
            VillaDetailFragmentDirections.actionVillaDetailFragmentToCreateReservationFragment(
                id
            )
        Navigation.findNavController(view).navigate(action)
    }

    private fun goToOwnerProfile(id: String, view: View) {
        val action =
            VillaDetailFragmentDirections.actionVillaDetailFragmentToUserProfileFragment(
                id
            )
        Navigation.findNavController(view).navigate(action)
    }

    override fun onResume() {
        super.onResume()
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
//
//    private fun createDemoUsers(): List<UserModel> {
//        val user1 = UserModel(
//            username = "Ahmet D.",
//            profileImageUrl = "data:image/jpeg;base64,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",
//            host = Host(
//                role = "Emlakçı"
//            )
//        )
//
//        val user2 = UserModel(
//            username = "Gizem P.",
//            profileImageUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxASEhUQDxIWFhUVFRUVFRUVFRAVFRcQFxYXFhUVFRUYHSkgGBolGxUVITEhJSkrLi4uFx8zODMuOCgtLi0BCgoKDg0OGhAQGCsdFR0rKystLSsrLS0tKysrKy0tLSstKy03Ny0tKy0rLS0tKy0tLS0rLSstLSsrLS0rLSsrLf/AABEIALcBEwMBIgACEQEDEQH/xAAcAAAABwEBAAAAAAAAAAAAAAAAAQIDBAUGBwj/xAA/EAACAgECAwYDBQcCBAcAAAABAgADEQQSBSExBhMiQVFhMnGBByNCkaEUM1JigrHBcvAkwtHxFUNTY3OS4v/EABkBAAMBAQEAAAAAAAAAAAAAAAABAgMEBf/EACIRAQEAAgMBAAICAwAAAAAAAAABAhEDEiExIkEEYTJRcf/aAAwDAQACEQMRAD8A1wEcUQlEdUTNQARWIYEBgRJESRFEQowRiDEURBiAJxDAhhYoCMgAiwIYWKAgAAjiiACGzBQWYgKASSSAAB1JJ6CAGBFEgdZy7tX9qYBNXDgGxyN7Dwk/+2nmP5jj5Ec5zzWcb1Nx3X32OT6sQPoo5D8oxp6RNkd0rAmefuBcd1FJHdWMBn1O0/Q8jOhaD7Q0qCtqlJGcFq8HGfUef0hsadWUSQglLwLj2l1ab9ParjzAPiH+pTzU+xl0hjgJIhqIZENRAibOhmI4oPvj9JurByMw/FB98fpFTXfDR4Y9bG+HDwx60Rwg0o8UsRK/S/FLERgqKEKGIgBhGGYDAGWghmDEYGBBFYggGMUR1REqIsTJQ4UViEYwSRCIioWIyJxBiKxDAgCYoCGBFAQAwIsCEIsCMgE4n9pfbY6pzpdM3/DocMR/5zjz/wBA8h59fSar7W+1JoqGioOLLlzYwPNKOmPYseXyB9ZxYwVIPdFq8ZJlhw7hz2HpgesVsn05NpHDVJ5nn6DrF8VyfDgKcg4Gf1mu4L2eUKCRzjfGuzBI3J1/3yMzvJK2nFZFJ2Z11untWyp2Rh0Zeh9mHmJ6I7HdpU1lfPC2oBvT/mX+U/p+WfOehUo+xxg+h6fOavgvE7NNYt1J8S+R6FfMfIjMvGsso9A4gAkTg/Eq9TSmoqPhcZx5q3RlPuCCPpJglswfoZieJj74/Sbd+hmK4l++MVNdcPHhjtsToR4Yu2P9ELTfF+UsRK7TfFLICAKihExQgBNAYGgMAbMOFFRgqCCHAMasUIQhyFFQYgEOAJMKKIgxAiYYEEEAMCKEIRQjBQjPEdclFT32HC1qWP0HT69PrH1E5t9tPGtlVejU87D3jj+RT4R/9v7QDlnG+KWam+zUWnxWMT8l/Co9gMCV7GHLLgehFjbm+FcfVvIRW6VJu6OcJ4QX8dnIeQ8zNnwzSKvQSLSvpLbRrObLK12ceEi50glxp6lbqJSrkYMu+HEHnM41U/aLsity7q+TjmCOuZh6LWqbubhtKk4PT/fynaO9rHxMB8yBM/207LJqaWupA7wDIK45ke48/Kb4Wxz54ynfsn4sUsfRuRtcd7V/rAG9R8xg/Qzp4nmjsrxo03V2HP3brZ/SD48emV3DE9LqQeY6Hp8pvK5bAfpMZxEffGbR+kx3ER98YUlzoh4Yu0QtH8MVbH+iJ0w8UssSv0w8UsYAIoQocAJoDA0BgCIcKHAFwQYhwDGiHE5hFpKjgMG6R2sjPf8AixDYWOIkyJbrdsYPEYtjqsIcrP8AxGH/AOIw2fVaCKEqhxGLHEY+xaWyzzx9onETfr7mzyRu7X02pyncruJgKT6AmebNTcXZnPVmZvqxJ/zCUaMbpreDqtdILHGfEfrMf5zb6ZVNa5GRiRyfGvF9Nnib5+7qJ9zylhoOMWg+Ooj5Ssu1tvSmrl/E3IfQdY7XdqxguKyPMAkEf3zM9eNt3bZUa4WYx+URrL7gpAYovr0MqeB3brFyMcxNX214W7Inc8uXPl+v95n+2mtqHhNWl3Zusy38zYx9J03s0NOR9yylSMEBgRn3HrOOcM7OVq33yMxb+I4GZ1XslwGisB6q0Q9fCPF9W/FK7JuPnxzPtpwJ9NrdQtS5DfeVjwjCuNzDn5Z3cp23sFru/wBBprM5PdhCf5k8B/tMZ9qdVdZXUt/6eDyz8Df/ALlj9i3EO80llfTZaxUeiPzH65nRhdxy5x0F+kx/EB98ZsW6TIa/98ZVZrnRjww7Yek+GC2Mh6f4pPkGj4pOgCocKHACIhGGYRgCPOKERFiMFwQQRBiSYhoqFiQs2REJXzzHwsAWGgj6ioGRzSPSWDiMusLBKidyPSF3I9JJ2wwsWj2jdyPSKFI9JI2wwsei2qe0JCaW9/4abD+SGef2E7/2zcLor8+aEfmQP8zgLCANY5zbcGG6pT7TE5wczXdl7spj0Mnl+NOG+tJodHWTggGPcR4eicwBiFpORzEcfuLgVg9Ovz9JzuyInC2HeAj+ITrlab9OCF3EDp9JyCi3DDC4+v8AYYnROC8RY1Ktbc/Pln6QCj1NqO+MbHU80PrN32ZsygU+XQzF9oeFuxNy8myTn1PvLPsRxLd4TyIOCPeE8GU3Dn2zXKvDmDbdzOiIDnJJI3Bf6Qx+kofsK1e2+ysnk68h7rzH6bo/9u2o/wCH0qet7t7+Gvb+XjlB9jt+3Xp7kgfUbf7MZ04uLP2vQbdJkNd++M17dJj+IjF8usl7pB4Ydog0g8MF0ZFUdZMEh0dZNEAOHBBACMIw4TQBEUIkRUAXBBBAMRDxEwFpCyxC840zxlbjuxAJTRl2kfX2kYxK9tQ8VyOYrG5vSGvIc5VjUNmE+rYxbPqtu+Eh2cTAbbIJsaRrKCTuj7FcEP7TuIY0grBGbGUf0g5P9v0nH7Bzmw7c64vaEzyQY/qPX/Ex7/EY5djWoYaXPZjWbX7s+Y5fMSmaFWxUhgcEHIPvHZuaLHLrdupaViRkdR5esq+Ka/uubAnJ+mT6nyjHBOLCxRzww6j3/wCkseIKtgyRkHkR7zl1q+u6Zbng9DprmAcAY5+/ln+02/Z3hNrcmcryQnbgeFiRmc/0Isq8NbuFznG44HLH+Zp+H6+4+FncjGOu3ly5cvr+cdOdln2o4be9tdWlvcADN53FgowAF9MnxHHy9YjsRpir2E55PjJ8yMTScOrXugiADPkBgfP5yt43xGrhume9sbyT3afx2noPl5k+gk32yQ99Zdsh9rGu7/UrSp5aevB/+VyGYfkEET9kSf8AHJ/V+mG/5Zk9Le75sc7nZi7Mepctlm+pOZvvsm04GrLY6LyPud2f0B/WdM8cV99dxbpMfxP9/NcTymV1y5uMusl1pvhhXw9N0hXyiL0/WTBIen6iTYgOCCCACJaGYTQBAiokRQgC4IIIBh4hjDJhGQs2YVS847tgRecAa1aZkNqpZXiRLIWCVFNchapSGGJYkw66gzDMWj2YqpyBIHHtamnqZz1xyA9ZoLUAxiUPHeHiwc+fz9P8Q0JXG9bqGssLt1JzIB6n3zLHjFey6wDoGIlYI4dN2LER5xGSJURS6LCrBlJBHpNhw3iRYAP+frMaBLzhJ3YWZ8s8a8V1dNfUvmJe6FAACRMppSycszR8Mu3YBP5TnrrxrbcN1KqNx6Afr6f2nKPtX4i92or3dFU7V8lBI/U46zpRwtRJ6dZxrtfqu81DH05S+KbrPnv4k8Ht5Y9P+/8AidP+z3UCuwn3U/Qbgf0YzknDrwjKff8AzOi8A1QQo6nkMKfl+EzTO6rHCbjvGnuDLkekzus/fGT+z+tV6xg88c+YMga399NdsFzpukLUQ9N0haiUQ9P1EnSBp+ok+IDggggBGE0MwmgCBFCJEUIAuCCCAYPMPdGcxJeQtIDQxYM4kCy0xqhyXENhYaq8L1kGzVL6xnjCnliVLI0m5VUxi2OqX1jmn1S56yk7swBWHSLtVdcf9tDqNYvLnIN2tXMrWDHrENUephbRJi5x2x0+24kdGzk/zAkH/EoqFJOB5mbDtq9RHdh0yvixlc5xgAAev/SZfRjHM+n6eseO9eput+I2pTBkaT9WBn59DnkB7yCwweufcdJcTS0WWvBG22DPn/eVlJljoLAGB94svYrD62qafK7pO4TqFUgnHLr85H4farLtHPlGG7P2Ox2PgH13f4M5nX/xO7U9pvDsrPlzx5fP3nOtWDjvD+I4+s3+j7Ej4r7CwHMKMgZ9zKbtnoQFwuAEGAByA+k0wuMvjLkmVm6x9RycTVcA4gy+BgcEfX5j1mSQTVcHKmtSeoOM+Y95ec2zwumlXtbq9MVFFgVT0VlQr82OOn1k3V9vdYbxXWtTvgAhR3is+M/dlWHLHkc+cyj3sTZqEFbLUBuSwg7g2VBVCeZHtKau0KrPZW+WH3TglVDg+I9PF9JvhjJj6xzvvjpjfatqgTWqVADkXaq4lccmJrFnkfeavs326TUqVuAVkGTYvKtlzjdtY7kz6c8Z6zhmm1WypnrvK2NmtqwrDNRHMl+hGfKT9EHqWl7qyUcB1543155gMOnL6jM01Ebel9MckEefT5SfOU/Z/wBqUWw0WM4rZwtKudxTJwFLcseQ6YnVZnlNHKVBBBJMRhNAYDAECKEQsWIAuCCCAc1OpX1iGvHrMA/F7M5zH9Nxhyw5zLse22JitKPFKXT684yZbcP1Ac8jHD2ka9c4kB6pY6rrIbmOnEUpCIjzSp13EQjYiV6d4lra6ENtrYUfmW54VR5nlOa8f4zdq1ZzWRVWy4wx2oTyG7yZz6+Q6Dzj3aniz6jUhEUOFXZWnxDvG6vj+Lp64wPeUWkoQlhbZ3e1WIyrMTYOQTA6EnzM0xxiLS6dP92Wr2uSrGxO7JapFYAWbscgc9R084G7tNjbu9BTxIRZXsY58O7zwcHI5GOaC6tVtDq5dk21sj7QpOd+8fiUjliS6r0oNN+msJtCsbA6DalhBQqMjDqVJ/P8r1tKD3RCI9hG1iyjBUt4cZDL1XqCMjzjHdAyz02mRFq1Fnd2KbGV6N5WzaoBy2Oaqc8iI1Xoc4tKslBt7svguEzhtufxMEOffEVwPsiUaXLBcjJO0Dn1xyzJ+k4Nbu2ty+YOCIlNLp/2go15FW44uFb7sD4W7v4hzxOmdltVVqaVc43r4X8vEPxY8sjB+sx5ZcZuNuKTK6Z7hXB7KhlnbHpgY/PrNTwdsmXA0akbcDEj6bhvdvkdJy2uuTSVYnKZXj/AUu+NmH+nHP8AMTbd3K6/S7jH8KzbDcL7EV2NjngdSSecotfwzaLraN3cLcaQwORvHT3OcdZ1PjNw0ultsHIhDj/URynJ6NFcKqnY7K7SzLYzYQumdxODyYHIGfXlOngnb65uf8fhjWd1sRAjraGYWMx8JX8JC9QRzzC1WktdxpKLTqFXLV7CduSMsVB6Hrn6yV+1WMx1mqqN6uDXufcq95t8PiA+IAdPnIVKVis2i5luVwFQKwzWV5uLB0PUYnU5dm7LkttXeq0r4Ufu0+EDkz7fNvWOaeovZ3FLGzL7K+o3AthSAfhzkfnHLKXoRqrqBm1UsR3HjVOZDIfRhkRFOlKYIvQd5S9hCsSQazvWtsdHLKMe4iDRLxe7v7Dql327WqcvgFXA2K2V5blIGD549537snxRdRpa7A24gBXPQ71Azmea6LdTTWd2VTVKGydp7xUfIYHqPF8szsn2T8SRzeiYRWO9KdxYoFCqTk+R3D8jHl7BHSIcSIYmKhNCMNoUAQsWIgRwRgqCCCIPMjMMRqqwqeQhuOclJUNs5fh6Pji/h2kSy7McRJs25mcrrLdBLzszw9hbulS3Ya3iWrK4MqbNa2ZYcVr6SsNUd22x0N9YZnO0NhCM+CT5D3P+yfpL81zG9udX466FbBHibyA3eEZPy3fQwxltO5STxlO9AKtXuVgOZzzL5bJXHQYKj6GOXaZk2F8YsUWDaQTsJPX0bkeUJm2CyoBWywG8DJ8BbBQ+hzD0mkDFw7rWURmw+QWZfwAfxGdLCpn7Tp69SLKazZSjqwrvx41GMq+PInP0/KTaRZpWo1IFTb1axEPjXadyFbV5YPM/kPTEqVvqFJTuvvd+7vdx5V4xs2dOvPMk8OZEeu2+pnq3Hcoyu8Ac1D+oyPP8sypUpNOkZa01TKrVd8Kyu7BYgd4UwOYUqCMxVgdxa1KOtCuHZFZnSsMStZb1OAQCR5SMlDur211t3SNzPUIGPgUt69BmTdTaGe0aJLVpZQWryznYoBYvjOVDZPPpmWkWrvrrFuno221OyMttlZW0ELnw8/DzJU+Rxnzk7svxGnTlG7xt1jlLUK+BV57LFb54BHufTnGW+ukK1DhzZQUuWysYR2+JVz16AhvKRX0AWuu92Uo7lWRWBtUKfEShGBkdDnHSTljLNKxy63cdo0tnKTkTMxnY/jdTltMrse7/AHTWYDvSAPi/mXp7jB9ZtdOZ52WFxuq9HHKZTcLFcSKeckpDMRsB9qGqxSKx+I/pOf6/TXVba7gy5AZVJyNjjO4AHAzj9JrvtFJsvrqQZYkBQPNicAfnM3Wz1Mz6invQoagraWwlmOQB8ivUD/vO3+PPxcf8i+6M6nRWM50ultOoT96BWGA3bfEdjHqoODDrto1GoXvQunrKhWNSFgCqnx7fc4zI6KqVC1LiLt5Q1qGUivafGLAfPpj3ki5LKEbS3UKr7ls3MPvVUrgKGB+E9cfObucrQggi+6prqUzV4i4TO0lU3fhwDuAkLScJctV3jLWl27ba5G0AHBLY6c/7y6rRK7TUtr36VStlndh0BJQDJBPhYZ25J8vpM01Fi2d3blCQnx5AVXIIb2HnCiLTh1uopK6pBuVGatHYF69xVsqAfYk4m7+yzUouro7vIPdst5ZhhizbV7sefxV8vYzAGqytmVW72qi1WYqWagtkKCfLxfD78xNL2V1Kvqa7kGLjqg5qRQK1oA3sy56Y5jHoIG9F5ihEwxMFCaCBoUASscEbWLBjBcEKHEHno8NDcxyk7R8NAGDIGj1NmOkvNDYT1Ew8a9SaOFqOeBLLQUBW5Q0Ee03xStEHEk6SuZJZcTsAxmVb6hfWFOEss5RxnUPffbtG7c28EDJCIpxg+QwefuJ0njGu2UWMvNthCj+dvCv6kTk5cLt7ssPBhjkjJJIOMeW3aPzl4FkPciqjVlu8BJbONoIIKFf1zmOPVdYr6lgWUMA7+EeNunL6joItAKLFLrXcNobbk7TuXkCfUZ/SRcMF89pPvtLD9CRn9ZozTTdTU9NlObCqq1i2qNnfc9ygDqvSG/EHeuumx8UrYzBVVcJvbxlQBk8ugJMN1qotdfBqV2FQwLKm5lHiHqVJIkWrUgUtV3akl1YWH41Cg5VfQEnJ+X5OA9bftNldNjGot7gOqnws6+xPnLSjWLWVOist3vTsuyBne37ytMDmnIe/v6VSal6d6VWgrdWFfaBgq3VDnoRzH1j2j1J05p1FF33oZjtA51leSkk8jkE+UcpWLPSuKBTqarVNosbNJQkKq/CzE8mDA9PeJr029X1OasJYpaonaW3tnCIOqeRweQiNI62I97Wp3iOjd2wybSzZZl8iAeZX0MfdH1Vl9wWuvajXMq4RdgwD3a+Z88ef1lpJ0mtpS57jW6HDNQKn/d3fhJ3fEvUEehIxOsdkeOprKt4wLFwLa/4W9R/KfI/TynKXpOqsRNJpwrd2AUVs7nRSzv4jyJA6e3rFcG48dLhqqwLg+e8LNhqceKl06FSQDnqDz8pnycfef214uS43+ndMyPdeADIvBeN06ugXUn2dD8SP5q3+D5jBlR2l4kK0bn5Tg1ZdO6WWbYPtPdZdqnaoMe6G4lQcqF5lsjpjlzlRWl92/bvs27rXGSwGPisYE9ffrDN16ZvUugt3pvG5VfPJk3dD1GRAEQV1Gi2zvrC9dlYDLhSwCAMPiDA8xPQ451xkefyXeRYai+9y+zTIUJUIrMgsVBhceQYg/wCzHeGvYpGqup72t1asG3cy79mAA2cgryI+Rx7R9SdijT2UBLEc77CCLMEfAw9BnMmfsxWw1aextRUhFuFFmwjA3MyA8sA7Sf1loStBX3YTF2V1CFbkrALqqvyVgR1JAIxMo+oVkLWl2uLDBJBUVAYwc8855D2E2iEZt1FO2mux2p2Biz1oyh8rnnjlyPkZj9SlNZtqILspVa7FJVcD4jtPUHyiyOJduntrNlFVneIUV7DSWavaPEC3+knqekvOF3Jbb3taipw9K10Vhju5Yc7vLkuT67pRUVWV4XT27zdV41p3MQh5tXYB7DmJa8PdHO+rbQ9dVaqqly11xYKzKfJiGz9IQV6dqbKg+oB/SLzIHA9R3mnpfGM1pkeYOMEH6gydMqqCMEBhmIGxFxAMWIwWIcKCIOPVadcdIjcFMKCceP115fDv7WohaLXA2YEEE2ZaI7QX4xKFtYPeCCZ5X1pipu0+u+4OCc7l9uhDdf6Zkr81myjCnJQbscwV5jaT0zuwYIJvxfGXJ/kTSlY7xbiwYKQgXBHeg9G9vlHdM913daVWyN+K1OAA7nmc/UwQTVlR93XUb6r6y1i7q0KthUtViGY/xDlI+m1IVs2ILAFKhWJAGeh5emYIICF6N2q237VYEsuHGRnGDkfWLo0rir9pG3CWquDzO7kwOMYI6QQQNO0/e6qy64bFYI1zAAqCFAyFAzzOPOTqKzrXopoprFmxawFwveOgJ7xyeW8gczyyYIJeKKSbFC1pVWyXqzrZYLD4yWwoAzhSOYyOsa1FdaVPU6OL1s5MGXYqAYZCB1Oc8x+fkRBKJP0XHtTpbKl2IjVVhXVQAbam8S94VJDEDoeoyfUx/tHxfvyNhOG58+R5+RggmOeM3K1wzurELQ6y9VS0YerT2AhHwUFlgb8Pvg/KR6BU4usss7tx461VCVZy3wDHwYyMfP2ggmzIrS6zDtbfX3+9HX7xmzvIAFm7rkcpK4ZS1dP7TXftbeaSihw21kJJ39CCARiCCAWK1jY1lVYASkV27juHeWb03qPLlj5ETF1vsz3qBy9Y2kscqWA2vkdSPQwQRZHE7TrZQtV1VuHtVxhdysq/CyluhyD5S14UEsKLWBS1NVlj2Zdt7plwcD4T0AggixDun2ecRNmirJzlSysScktnJOf6pqi0EEjL6cRtRqCDiSEfK5ggiMlWiw0EEAczBBBAP//Z",
//            host = Host(
//                role = "Villa sahibi"
//            )
//        )
//
//        val user3 = UserModel(
//            username = "Eylül T.",
//            profileImageUrl = "data:image/jpeg;base64,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",
//            host = Host(
//                role = "Villa sahibinin yakın akrabası"
//            )
//        )
//
//        val user4 = UserModel(
//            username = "Kasım R.",
//            profileImageUrl = "data:image/jpeg;base64,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",
//            host = Host(
//                role = "Villa ortağı"
//            )
//        )
//
//        return listOf(user1, user2, user3, user4)
//    }

}
