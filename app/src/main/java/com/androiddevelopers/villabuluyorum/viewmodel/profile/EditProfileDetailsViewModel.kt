package com.androiddevelopers.villabuluyorum.viewmodel.profile

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditProfileDetailsViewModel @Inject
constructor(
    private val repo : FirebaseRepoInterFace,
    private val auth : FirebaseAuth
):ViewModel(){
    private val currentUserId = auth.currentUser?.uid.toString()

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    private var _userInfoMessage = MutableLiveData<Resource<Boolean>>()
    val userInfoMessage : LiveData<Resource<Boolean>>
        get() = _userInfoMessage

    private var _uploadMessage = MutableLiveData<Resource<Boolean>>()
    val uploadMessage : LiveData<Resource<Boolean>>
        get() = _uploadMessage

    init {
        getUserData()
    }
    private fun getUserData() = viewModelScope.launch{
        _userInfoMessage.value = Resource.loading(null)
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
                _userInfoMessage.value = Resource.success( null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _userInfoMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
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
    fun getMapIfDataChanged(oldUser: UserModel, newUser: UserModel) :HashMap<String, Any?>{
        val updateMap = HashMap<String, Any?>()

        if (newUser.username != null && newUser.username!!.isNotEmpty() && oldUser.username != newUser.username) {
            updateMap["username"] = newUser.username!!
        }
        if (newUser.firstName != null && newUser.firstName!!.isNotEmpty() && oldUser.firstName != newUser.firstName) {
            updateMap["firstName"] = newUser.firstName!!
        }
        if (newUser.lastName != null && newUser.lastName!!.isNotEmpty() && oldUser.lastName != newUser.lastName) {
            updateMap["lastName"] = newUser.lastName!!
        }
        if (newUser.email != null && newUser.email!!.isNotEmpty() && oldUser.email != newUser.email) {
            updateMap["email"] = newUser.email!!
        }
        if (newUser.phoneNumber != null && newUser.phoneNumber!!.isNotEmpty() && oldUser.phoneNumber != newUser.phoneNumber) {
            updateMap["phoneNumber"] = newUser.phoneNumber!!
        }

        return updateMap
    }
    fun updateUserData(updateMap: HashMap<String, Any?>) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateUserData(currentUserId, updateMap).addOnSuccessListener {
                _uploadMessage.value = Resource.success(null)
            }.addOnFailureListener{
                _uploadMessage.value = Resource.error(it.localizedMessage,null)
            }
        }
    }
    fun uploadUserPhoto(image : Uri,key : String,map :HashMap<String, Any?>?){
        repo.uploadUserProfilePhoto(image, currentUserId, key) // kapak resmini yüklüyoruz
            .addOnSuccessListener { task ->
                task.storage.downloadUrl
                    .addOnSuccessListener { uri ->
                        if (map != null){
                            map[key] = uri.toString()
                            updateUserData(map)
                        }else{
                            val updateMap = HashMap<String, Any?>()
                            updateMap[key] = uri.toString()
                            updateUserData(updateMap)
                        }
                    }.addOnFailureListener {
                        _uploadMessage.value = Resource.error(it.localizedMessage,null)
                    }
            }.addOnFailureListener{
                _uploadMessage.value = Resource.error(it.localizedMessage,null)
            }
    }
    fun startLoading(){
        _uploadMessage.value = Resource.loading(null)
    }
}

