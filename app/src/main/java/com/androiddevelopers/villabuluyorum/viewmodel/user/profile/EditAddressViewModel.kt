package com.androiddevelopers.villabuluyorum.viewmodel.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Neighborhood
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.provinces.Village
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.repo.RoomProvinceRepo
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAddressViewModel @Inject
constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
    private val roomProvinceRepo: RoomProvinceRepo
) : ViewModel() {
    private val currentUserId = auth.currentUser?.uid.toString()

    private var _liveDataProvinceFromRoom = MutableLiveData<List<Province>>()
    val liveDataProvinceFromRoom: LiveData<List<Province>>
        get() = _liveDataProvinceFromRoom

    private var _liveDataDistrictFromRoom = MutableLiveData<List<District>>()
    val liveDataDistrictFromRoom: LiveData<List<District>>
        get() = _liveDataDistrictFromRoom

    private var _liveDataNeighborhoodFromRoom = MutableLiveData<List<Neighborhood>>()
    val liveDataNeighborhoodFromRoom: LiveData<List<Neighborhood>>
        get() = _liveDataNeighborhoodFromRoom

    private var _liveDataVillageFromRoom = MutableLiveData<List<Village>>()
    val liveDataVillageFromRoom: LiveData<List<Village>>
        get() = _liveDataVillageFromRoom


    private var _uploadMessage = MutableLiveData<Resource<Boolean>>()
    val uploadMessage: LiveData<Resource<Boolean>>
        get() = _uploadMessage

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData


    init {
        getAllProvince()
        getUserData()
    }

    private fun getAllProvince() = viewModelScope.launch {
        roomProvinceRepo.getAllProvince().flowOn(Dispatchers.IO).collect {
            _liveDataProvinceFromRoom.value = it
        }
    }

    fun getAllDistrictById(provinceId: Int) = viewModelScope.launch {
        roomProvinceRepo.getAllDistrictById(provinceId).flowOn(Dispatchers.IO).collect {
            _liveDataDistrictFromRoom.value = it
        }
    }

    fun getAllNeighborhoodById(districtId: Int) = viewModelScope.launch {
        roomProvinceRepo.getAllNeighborhoodById(districtId).flowOn(Dispatchers.IO).collect {
            _liveDataNeighborhoodFromRoom.value = it
        }
    }

    fun getAllVillageById(districtId: Int) = viewModelScope.launch {
        roomProvinceRepo.getAllVillageById(districtId).flowOn(Dispatchers.IO).collect {
            _liveDataVillageFromRoom.value = it
        }
    }

    fun updateUserData(updateMap: HashMap<String, Any?>) {
        _uploadMessage.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateUserData(currentUserId, updateMap).addOnSuccessListener {
                _uploadMessage.value = Resource.success(null)
            }.addOnFailureListener {
                _uploadMessage.value = Resource.error(it.localizedMessage, null)
            }
        }
    }

    private fun getUserData() = viewModelScope.launch {
        repo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { document ->
                document.toUserModel()?.let { user ->
                    _userData.value = user
                }
            }
    }

}