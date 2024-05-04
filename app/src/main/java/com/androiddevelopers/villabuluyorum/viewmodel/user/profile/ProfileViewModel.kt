package com.androiddevelopers.villabuluyorum.viewmodel.user.profile

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.repo.SharedPreferencesRepo
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
    private val sharedPreferencesRepo: SharedPreferencesRepo
) : ViewModel() {

    private val currentUserId = auth.currentUser?.uid.toString()


    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _userInfoMessage = MutableLiveData<Resource<Boolean>>()
    val userInfoMessage: LiveData<Resource<Boolean>>
        get() = _userInfoMessage

    private var _userVillas = MutableLiveData<List<Villa>>()
    val userVillas: LiveData<List<Villa>>
        get() = _userVillas

    private var _villaInfoMessage = MutableLiveData<Resource<Boolean>>()
    val villaInfoMessage: LiveData<Resource<Boolean>>
        get() = _villaInfoMessage

    private var _exitMessage = MutableLiveData<Resource<Boolean>>()
    val exitMessage: LiveData<Resource<Boolean>>
        get() = _exitMessage

    init {
        getUserData()
        getUserVillas(20)
    }

    private fun getUserData() = viewModelScope.launch {
        _userInfoMessage.value = Resource.loading(null)
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
                _userInfoMessage.value = Resource.success(null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _userInfoMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }


    private fun getUserVillas(limit: Long) = viewModelScope.launch {
        _villaInfoMessage.value = Resource.loading(null)
        repo.getVillasByUserId(currentUserId, limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa -> villaList.add(villa) }
                }
                _userVillas.value = villaList
                if (villaList.isNotEmpty()) {
                    _villaInfoMessage.value = Resource.success(true)
                } else {
                    _villaInfoMessage.value = Resource.success(false)
                }
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _villaInfoMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }


    suspend fun getCityFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double
    ): String? {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context)
            var city: String? = null

            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    city = address.locality ?: address.subAdminArea
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            city
        }
    }
    fun signOutAndExit(context: Context) {
        _exitMessage.value = Resource.loading(null)
        val googleSignInClient =
            GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut()
        auth.signOut()
        _exitMessage.value = Resource.success(null)
    }
    fun setStartModeHost() {
        sharedPreferencesRepo.setStartModeHost()
    }
}