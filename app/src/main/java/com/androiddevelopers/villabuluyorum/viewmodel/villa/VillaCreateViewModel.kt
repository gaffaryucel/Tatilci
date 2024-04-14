package com.androiddevelopers.villabuluyorum.viewmodel.villa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Neighborhood
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.provinces.Village
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.repo.RoomProvinceRepo
import com.androiddevelopers.villabuluyorum.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VillaCreateViewModel
@Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
    private val roomProvinceRepo: RoomProvinceRepo
) : ViewModel() {

    private var _liveDataFirebaseStatus = MutableLiveData<Resource<Boolean>>()
    val liveDataFirebaseStatus: LiveData<Resource<Boolean>>
        get() = _liveDataFirebaseStatus

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

    private var _liveDataFirebaseUser = MutableLiveData<UserModel>()
    val liveDataFirebaseUser: LiveData<UserModel>
        get() = _liveDataFirebaseUser


    fun getAllProvince() = viewModelScope.launch {
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

    fun addVillaToFirestore(
        villaId: String,
        villa: Villa
    ) = viewModelScope.launch {
        _liveDataFirebaseStatus.value = Resource.loading(true)

        firebaseRepo.addVillaToFirestore(villaId, villa)
            .addOnSuccessListener {
                _liveDataFirebaseStatus.value = Resource.loading(false)
                _liveDataFirebaseStatus.value = Resource.success(true)
            }.addOnFailureListener {
                _liveDataFirebaseStatus.value = Resource.loading(false)
                it.message?.let { message ->
                    _liveDataFirebaseStatus.value = Resource.error(message)
                }
            }
    }


    /*

        private val userId = firebaseAuth.currentUser?.uid.toString()

        private val firestoreDB = FirebaseFirestore.getInstance()
        private val storageRef = FirebaseStorage.getInstance().reference

        fun addVillaToFirestore(home: VillaModel): Task<Void> {
            return firestoreDB.collection("villas").document().set(home)
        }

        fun uploadImageToStorage(imageUri: Uri): Task<Uri> {
            val imageName = "image_${System.currentTimeMillis()}.jpg"
            val imageRef: StorageReference = storageRef.child("images/$imageName")

            return imageRef.putFile(imageUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    imageRef.downloadUrl
                }
        }

        fun saveVillaWithImage(home: VillaModel, imageUri: Uri): Task<Void> {
            return uploadImageToStorage(imageUri).onSuccessTask { downloadUri ->
                home.imageUrl = downloadUri.toString()
                addVillaToFirestore(home)
            }
        }
     */
}
