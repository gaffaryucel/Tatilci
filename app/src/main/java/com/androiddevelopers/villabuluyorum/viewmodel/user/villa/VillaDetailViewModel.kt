package com.androiddevelopers.villabuluyorum.viewmodel.user.villa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.ReviewModel
import com.androiddevelopers.villabuluyorum.model.UserModel
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toReview
import com.androiddevelopers.villabuluyorum.util.toUserModel
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.androiddevelopers.villabuluyorum.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VillaDetailViewModel
@Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : BaseViewModel() {
    val liveDataFirebaseStatus: LiveData<Resource<Boolean>> = MutableLiveData()
    val liveDataFirebaseVilla: LiveData<Villa> = MutableLiveData()
    val liveDataFirebaseUser: LiveData<UserModel> = MutableLiveData()
    val liveDataFirebaseUserReviews: LiveData<List<ReviewModel>> = MutableLiveData()

    fun getVillaByIdFromFirestore(villaId: String) {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)
        firebaseRepo.getVillaByIdFromFirestore(villaId)
            .addOnSuccessListener { documentSnapshot ->
                liveDataFirebaseVilla.mutable.value = documentSnapshot.toVilla()
                liveDataFirebaseStatus.mutable.value = Resource.success(false)
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
            .addOnFailureListener {
                liveDataFirebaseStatus.mutable.value = it.message?.let { message ->
                    Resource.error(
                        message, null
                    )
                }
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
    }

    fun getUserByIdFromFirestore(userId: String) {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { documentSnapshot ->
                liveDataFirebaseUser.mutable.value = documentSnapshot.toUserModel()
                liveDataFirebaseStatus.mutable.value = Resource.success(false)
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
            .addOnFailureListener {
                liveDataFirebaseStatus.mutable.value = it.message?.let { message ->
                    Resource.error(
                        message
                    )
                }
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
    }

    fun getAllReviewsByVillaId(villaId: String) = viewModelScope.launch {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)
        firebaseRepo.getAllReviewsByVillaId(villaId)
            .addOnSuccessListener {
                liveDataFirebaseUserReviews.mutable.value =
                    it.map { queryDocumentSnapshot -> queryDocumentSnapshot.toReview() }
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                liveDataFirebaseStatus.mutable.value = Resource.success(false)
            }
            .addOnFailureListener {
                liveDataFirebaseStatus.mutable.value = it.message?.let { message ->
                    Resource.error(
                        message
                    )
                }
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
    }

    fun deleteVillaAndImages(villa: Villa){
        val images = mutableListOf<String>()
        villa.otherImages?.let {imageList->
            images.addAll(imageList)
        }
        villa.coverImage?.let {image->
            images.add(image)
        }

        villa.villaId?.let {id->
            deleteImageFromFirebaseStorage(id,images)
        }
    }

    private fun deleteVillaFromFirestore(villaId: String){
        firebaseRepo.deleteVillaFromFirestore(villaId)
            .addOnSuccessListener {
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                liveDataFirebaseStatus.mutable.value = Resource.success(true)
            }
            .addOnFailureListener {
                liveDataFirebaseStatus.mutable.value = it.message?.let { message ->
                    Resource.error(
                        message
                    )
                }
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
    }

    private fun deleteImageFromFirebaseStorage(villaId: String, images: MutableList<String>) {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)

        if (images.isNotEmpty()){
            firebaseRepo.deleteImageFromFirebaseStorage(images[0])
                .addOnSuccessListener {
                    liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                    liveDataFirebaseStatus.mutable.value = Resource.success(false)

                    //silinen resmi listeden kaldırıyoruz
                    images.removeAt(0)

                    //listenin son halini tekrar işleme alıyoruz, liste bitene kadar
                    deleteImageFromFirebaseStorage(villaId, images)
                }
                .addOnFailureListener {
                    liveDataFirebaseStatus.mutable.value = it.message?.let { message ->
                        Resource.error(
                            message
                        )
                    }
                    liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                }
        }else{
            deleteVillaFromFirestore(villaId)
        }

    }
}