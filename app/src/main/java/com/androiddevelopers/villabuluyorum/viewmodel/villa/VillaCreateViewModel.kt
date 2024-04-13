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
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.repo.RoomProvinceRepo
import com.androiddevelopers.villabuluyorum.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private var _liveDataFirebaseProvince = MutableLiveData<List<Province>>()
    val liveDataFirebaseProvince: LiveData<List<Province>>
        get() = _liveDataFirebaseProvince

    private var _liveDataFirebaseDistrict = MutableLiveData<List<District>>()
    val liveDataFirebaseDistrict: LiveData<List<District>>
        get() = _liveDataFirebaseDistrict

    private var _liveDataFirebaseNeighborhood = MutableLiveData<List<Neighborhood>>()
    val liveDataFirebaseNeighborhood: LiveData<List<Neighborhood>>
        get() = _liveDataFirebaseNeighborhood

    private var _liveDataFirebaseVillage = MutableLiveData<List<Village>>()
    val liveDataFirebaseVillage: LiveData<List<Village>>
        get() = _liveDataFirebaseVillage

    private var _liveDataFirebaseUser = MutableLiveData<UserModel>()
    val liveDataFirebaseUser: LiveData<UserModel>
        get() = _liveDataFirebaseUser


    fun getAllProvince() = viewModelScope.launch(Dispatchers.IO) {
        _liveDataFirebaseProvince.value = roomProvinceRepo.getAllProvince()
    }

    fun getAllDistrictById(provinceId: Int) = viewModelScope.launch(Dispatchers.IO) {
        _liveDataFirebaseDistrict.value = roomProvinceRepo.getAllDistrictById(provinceId)
    }

    fun getAllNeighborhoodAndVillageById(districtId: Int) = viewModelScope.launch(Dispatchers.IO) {
        _liveDataFirebaseNeighborhood.value = roomProvinceRepo.getAllNeighborhoodById(districtId)
        _liveDataFirebaseVillage.value = roomProvinceRepo.getAllVillageById(districtId)
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
