package com.androiddevelopers.villabuluyorum.viewmodel.user.reservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toVilla
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateReservationViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : ViewModel() {
    private var _liveDataFirebaseStatus = MutableLiveData<Resource<Boolean>>()
    val liveDataFirebaseStatus: LiveData<Resource<Boolean>>
        get() = _liveDataFirebaseStatus

    private var _liveDataFirebaseVilla = MutableLiveData<Villa>()
    val liveDataFirebaseVilla: LiveData<Villa>
        get() = _liveDataFirebaseVilla

    private var _liveDataFirebaseUser = MutableLiveData<UserModel>()
    val liveDataFirebaseUser: LiveData<UserModel>
        get() = _liveDataFirebaseUser

    fun getVillaByIdFromFirestore(villaId: String) {
        _liveDataFirebaseStatus.value = Resource.loading(true)
        firebaseRepo.getVillaByIdFromFirestore(villaId)
            .addOnSuccessListener { documentSnapshot ->
                _liveDataFirebaseVilla.value = documentSnapshot.toVilla()
                _liveDataFirebaseStatus.value = Resource.success(true)
            }.addOnFailureListener {
                _liveDataFirebaseStatus.value = it.message?.let { message ->
                    Resource.error(message, null)
                }
            }
    }
}