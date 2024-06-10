package com.izmirsoftware.tatilci.viewmodel.host.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.VillaPageArgumentsModel
import com.izmirsoftware.tatilci.model.provinces.District
import com.izmirsoftware.tatilci.model.provinces.Neighborhood
import com.izmirsoftware.tatilci.model.provinces.Province
import com.izmirsoftware.tatilci.model.provinces.Village
import com.izmirsoftware.tatilci.model.villa.Villa
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.repo.RoomProvinceRepo
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.toVilla
import com.izmirsoftware.tatilci.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HostVillaCreateBaseViewModel
@Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace, private val roomProvinceRepo: RoomProvinceRepo
) : BaseViewModel() {
    val liveDataFirebaseStatus: LiveData<Resource<Boolean>> = MutableLiveData()
    val liveDataProvinceFromRoom: LiveData<List<Province>> = MutableLiveData()
    val liveDataDistrictFromRoom: LiveData<List<District>> = MutableLiveData()
    val liveDataNeighborhoodFromRoom: LiveData<List<Neighborhood>> = MutableLiveData()
    val liveDataVillageFromRoom: LiveData<List<Village>> = MutableLiveData()
    val liveDataVilla: LiveData<Villa> = MutableLiveData()
    val liveDataImageCover: LiveData<Uri> = MutableLiveData()
    val liveDataImageUriList: LiveData<List<Uri>> = MutableLiveData()
    val imageSize: LiveData<Int> = MutableLiveData()

    fun getAllProvince() = viewModelScope.launch {
        roomProvinceRepo.getAllProvince()
            .flowOn(Dispatchers.IO)
            .collect {
                liveDataProvinceFromRoom.mutable.value = it
            }
    }

    fun getAllDistrictById(provinceId: Int) = viewModelScope.launch {
        roomProvinceRepo.getAllDistrictById(provinceId)
            .flowOn(Dispatchers.IO)
            .collect {
                liveDataDistrictFromRoom.mutable.value = it
            }
    }

    fun getAllNeighborhoodById(districtId: Int) = viewModelScope.launch {
        roomProvinceRepo.getAllNeighborhoodById(districtId)
            .flowOn(Dispatchers.IO)
            .collect {
                liveDataNeighborhoodFromRoom.mutable.value = it
            }
    }

    fun getAllVillageById(districtId: Int) = viewModelScope.launch {
        roomProvinceRepo.getAllVillageById(districtId)
            .flowOn(Dispatchers.IO)
            .collect {
                liveDataVillageFromRoom.mutable.value = it
            }
    }

    fun setCreateVillaPageArguments(villaPageArgumentsModel: VillaPageArgumentsModel) =
        viewModelScope.launch {
            with(villaPageArgumentsModel) {
                setVilla(villa)

                coverImage?.let { image ->
                    setImageCover(image)
                }

                setImageUriList(otherImages.toList())
            }
        }

    private fun setVilla(villa: Villa) = viewModelScope.launch {
        liveDataVilla.mutable.value = villa
    }

    private fun setImageCover(uri: Uri) = viewModelScope.launch {
        liveDataImageCover.mutable.value = uri
    }

    fun setImageUriList(newList: List<Uri>) = viewModelScope.launch {
        liveDataImageUriList.mutable.value = newList
        imageSize.mutable.value = newList.size
    }


    fun addImagesAndVillaToFirebase(
        coverImage: Uri?,
        images: MutableList<Uri>,
        villa: Villa,
        uploadedCoverImage: String? = null,
        uploadedOtherImages: MutableList<String> = mutableListOf()
    ) {

        if (villa.villaId.isNullOrBlank()) {
            villa.villaId = UUID.randomUUID()
                .toString()
        }

        val villaId = villa.villaId.toString()
        val userId = villa.hostId.toString()

        coverImage?.let { // kapak resmi varsa önce onu yüklüyoruz
            liveDataFirebaseStatus.mutable.value = Resource.loading(true)
            if (it.toString()
                    .contains("firebasestorage")
            ) { // kapak resmi daha önce yüklendiyse yüklemeden pas geçiyoruz
                addImagesAndVillaToFirebase(
                    null,
                    images,
                    villa,
                    it.toString(),
                    uploadedOtherImages
                )
            } else {
                firebaseRepo.addVillaImage(
                    it,
                    userId,
                    villaId
                ) // kapak resmini yüklüyoruz
                    .addOnSuccessListener { task ->
                        task.storage.downloadUrl.addOnSuccessListener { uri ->
                            addImagesAndVillaToFirebase( // kapak resmi yüklenince kapak resmini null yapıp diğer resimlere geçiyoruz
                                null,
                                images,
                                villa,
                                uri.toString(),
                                uploadedOtherImages
                            )
                        }
                            .addOnFailureListener { error ->
                                error.message?.let { message ->
                                    liveDataFirebaseStatus.mutable.value = Resource.error(message)
                                    liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                                }
                            }
                    }
                    .addOnFailureListener { error ->
                        error.message?.let { message ->
                            liveDataFirebaseStatus.mutable.value = Resource.error(message)
                            liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                        }
                    }
            }
        } ?: run {
            if (images.size > 0) {
                val uri = images[0]
                if (uri.toString()
                        .contains("firebasestorage")
                ) {
                    images.removeAt(0)
                    uploadedOtherImages.add(uri.toString())
                    addImagesAndVillaToFirebase(
                        null,
                        images,
                        villa,
                        uploadedCoverImage,
                        uploadedOtherImages
                    )
                } else {
                    liveDataFirebaseStatus.mutable.value = Resource.loading(true)
                    firebaseRepo.addVillaImage(
                        uri,
                        userId,
                        villaId
                    ) // kapak resmini yüklüyoruz
                        .addOnSuccessListener { task ->
                            task.storage.downloadUrl.addOnSuccessListener { uri ->
                                images.removeAt(0)
                                uploadedOtherImages.add(uri.toString())
                                addImagesAndVillaToFirebase( // kapak resmi yüklenince kapak resmini null yapıp diğer resimlere geçiyoruz
                                    null,
                                    images,
                                    villa,
                                    uploadedCoverImage,
                                    uploadedOtherImages
                                )
                            }
                                .addOnFailureListener { error ->
                                    error.message?.let { message ->
                                        liveDataFirebaseStatus.mutable.value =
                                            Resource.error(message)
                                        liveDataFirebaseStatus.mutable.value =
                                            Resource.loading(false)
                                    }
                                }
                        }
                        .addOnFailureListener { error ->
                            error.message?.let { message ->
                                liveDataFirebaseStatus.mutable.value = Resource.error(message)
                                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                            }
                        }
                }
            } else {
                villa.coverImage = uploadedCoverImage
                villa.otherImages = uploadedOtherImages.toList()
                addVillaToFirestore(
                    villaId,
                    villa
                )
            }
        }
    }

    private fun addVillaToFirestore(
        villaId: String, villa: Villa
    ) = viewModelScope.launch {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)

        firebaseRepo.addVillaToFirestore(
            villaId,
            villa
        )
            .addOnSuccessListener {
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                liveDataFirebaseStatus.mutable.value = Resource.success(true)
            }
            .addOnFailureListener {
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
                it.message?.let { message ->
                    liveDataFirebaseStatus.mutable.value = Resource.error(message)
                }
            }
    }

    fun getVillaByIdFromFirestore(villaId: String) {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)
        firebaseRepo.getVillaByIdFromFirestore(villaId)
            .addOnSuccessListener { documentSnapshot ->
                liveDataVilla.mutable.value = documentSnapshot.toVilla()
                liveDataFirebaseStatus.mutable.value = Resource.success(false)
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
            .addOnFailureListener {
                liveDataFirebaseStatus.mutable.value = it.message?.let { message ->
                    Resource.error(
                        message,
                        null
                    )
                }
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
    }
}
