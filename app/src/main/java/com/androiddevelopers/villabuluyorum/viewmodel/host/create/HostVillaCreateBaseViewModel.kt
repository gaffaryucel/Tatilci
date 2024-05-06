package com.androiddevelopers.villabuluyorum.viewmodel.host.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.CreateVillaPageArguments
import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Neighborhood
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.provinces.Village
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.repo.RoomProvinceRepo
import com.androiddevelopers.villabuluyorum.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HostVillaCreateBaseViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
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

    private var _liveDataVilla = MutableLiveData<Villa>()
    val liveDataVilla: LiveData<Villa>
        get() = _liveDataVilla

    private var _liveDataImageCover = MutableLiveData<Uri>()
    val liveDataImageCover: LiveData<Uri>
        get() = _liveDataImageCover

    private var _liveDataImageUriList = MutableLiveData<List<Uri>>()
    val liveDataImageUriList: LiveData<List<Uri>>
        get() = _liveDataImageUriList

    private var _imageSize = MutableLiveData<Int>()
    val imageSize: LiveData<Int>
        get() = _imageSize


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

    fun setCreateVillaPageArguments(createVillaPageArguments: CreateVillaPageArguments) =
        viewModelScope.launch {
            with(createVillaPageArguments) {
                setVilla(villa)

                coverImage?.let { image ->
                    setImageCover(image)
                }

                setImageUriList(otherImages.toList())
            }
        }

    private fun setVilla(villa: Villa) = viewModelScope.launch {
        _liveDataVilla.value = villa
    }

    private fun setImageCover(uri: Uri) = viewModelScope.launch {
        _liveDataImageCover.value = uri
    }

    fun setImageUriList(newList: List<Uri>) = viewModelScope.launch {
        _liveDataImageUriList.value = newList
        _imageSize.value = newList.size
    }


    fun addImagesAndVillaToFirebase(
        coverImage: Uri?,
        images: MutableList<Uri>,
        villa: Villa,
        uploadedCoverImage: String? = null,
        uploadedOtherImages: MutableList<String> = mutableListOf()
    ) {

        if (villa.villaId.isNullOrBlank()) {
            villa.villaId = UUID.randomUUID().toString()
        }

        val villaId = villa.villaId.toString()
        val userId = villa.hostId.toString()

        coverImage?.let { // kapak resmi varsa önce onu yüklüyoruz
            _liveDataFirebaseStatus.value = Resource.loading(true)
            if (it.toString()
                    .contains("firebasestorage")
            ) { // kapak resmi daha önce yüklendiyse yüklemeden pas geçiyoruz
                addImagesAndVillaToFirebase(null, images, villa, it.toString(), uploadedOtherImages)
            } else {
                firebaseRepo.addVillaImage(it, userId, villaId) // kapak resmini yüklüyoruz
                    .addOnSuccessListener { task ->
                        task.storage.downloadUrl
                            .addOnSuccessListener { uri ->
                                addImagesAndVillaToFirebase( // kapak resmi yüklenince kapak resmini null yapıp diğer resimlere geçiyoruz
                                    null,
                                    images,
                                    villa,
                                    uri.toString(),
                                    uploadedOtherImages
                                )
                            }.addOnFailureListener { error ->
                                error.message?.let { message ->
                                    _liveDataFirebaseStatus.value = Resource.error(message)
                                    _liveDataFirebaseStatus.value = Resource.loading(false)
                                }
                            }
                    }.addOnFailureListener { error ->
                        error.message?.let { message ->
                            _liveDataFirebaseStatus.value = Resource.error(message)
                            _liveDataFirebaseStatus.value = Resource.loading(false)
                        }
                    }
            }
        } ?: run {
            if (images.size > 0) {
                val uri = images[0]
                if (uri.toString().contains("firebasestorage")) {
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
                    _liveDataFirebaseStatus.value = Resource.loading(true)
                    firebaseRepo.addVillaImage(uri, userId, villaId) // kapak resmini yüklüyoruz
                        .addOnSuccessListener { task ->
                            task.storage.downloadUrl
                                .addOnSuccessListener { uri ->
                                    images.removeAt(0)
                                    uploadedOtherImages.add(uri.toString())
                                    addImagesAndVillaToFirebase( // kapak resmi yüklenince kapak resmini null yapıp diğer resimlere geçiyoruz
                                        null,
                                        images,
                                        villa,
                                        uploadedCoverImage,
                                        uploadedOtherImages
                                    )
                                }.addOnFailureListener { error ->
                                    error.message?.let { message ->
                                        _liveDataFirebaseStatus.value = Resource.error(message)
                                        _liveDataFirebaseStatus.value = Resource.loading(false)
                                    }
                                }
                        }.addOnFailureListener { error ->
                            error.message?.let { message ->
                                _liveDataFirebaseStatus.value = Resource.error(message)
                                _liveDataFirebaseStatus.value = Resource.loading(false)
                            }
                        }
                }
            } else {
                villa.coverImage = uploadedCoverImage
                villa.otherImages = uploadedOtherImages.toList()
                addVillaToFirestore(villaId, villa)
            }
        }
    }

    private fun addVillaToFirestore(
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
}
