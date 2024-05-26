package com.androiddevelopers.villabuluyorum.viewmodel.user.villa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.androiddevelopers.villabuluyorum.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VillaDetailViewModel
@Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : BaseViewModel() {
    val liveDataFirebaseStatus: LiveData<Resource<Boolean>> = MutableLiveData()
    val liveDataFirebaseVilla: LiveData<Villa> = MutableLiveData()
    val liveDataFirebaseUser: LiveData<UserModel> = MutableLiveData()
    
    fun getVillaByIdFromFirestore(villaId: String) {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)
        firebaseRepo.getVillaByIdFromFirestore(villaId).addOnSuccessListener { documentSnapshot ->
            liveDataFirebaseVilla.mutable.value = documentSnapshot.toVilla()
            liveDataFirebaseStatus.mutable.value = Resource.success(true)
            liveDataFirebaseStatus.mutable.value = Resource.loading(false)
        }.addOnFailureListener {
            liveDataFirebaseStatus.mutable.value = it.message?.let { message ->
                Resource.error(message, null)
            }
            liveDataFirebaseStatus.mutable.value = Resource.loading(false)
        }
    }

    fun getUserByIdFromFirestore(userId: String) {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)
        firebaseRepo.getUserDataByDocumentId(userId).addOnSuccessListener { documentSnapshot ->
            liveDataFirebaseUser.mutable.value = documentSnapshot.toUserModel()
            liveDataFirebaseStatus.mutable.value = Resource.success(true)
            liveDataFirebaseStatus.mutable.value = Resource.loading(false)
        }.addOnFailureListener {
            liveDataFirebaseStatus.mutable.value = it.message?.let { message ->
                Resource.error(message, null)
            }
            liveDataFirebaseStatus.mutable.value = Resource.loading(false)
        }
    }
}