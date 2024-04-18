package com.androiddevelopers.villabuluyorum.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.R
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject
constructor(
    val repo : FirebaseRepoInterFace,
    val auth : FirebaseAuth
): ViewModel() {
    private var _bestVillas = MutableLiveData<List<Villa>>()
    val bestVillas : LiveData<List<Villa>>
        get() = _bestVillas

    private var _closeVillas = MutableLiveData<List<Villa>>()
    val closeVillas : LiveData<List<Villa>>
        get() = _closeVillas

    private var userToken = MutableLiveData<Resource<String>>()

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage : LiveData<Resource<Boolean>>
        get() = _firebaseMessage


    init {
        getCloseVillas("toket",20)
        getBestVillas(20)
    }

    fun getCloseVillas(city : String, limit : Long) = viewModelScope.launch{
        _firebaseMessage.value = Resource.loading(null)
        repo.getAllVillasFromFirestore(limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa -> villaList.add(villa) }
                }
                _bestVillas.value = villaList
                _firebaseMessage.value = Resource.success( null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    fun getBestVillas(limit : Long) = viewModelScope.launch{
        _firebaseMessage.value = Resource.loading(null)
        repo.getAllVillasFromFirestore(limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa -> villaList.add(villa) }
                }
                _closeVillas.value = villaList
                _firebaseMessage.value = Resource.success( null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
}