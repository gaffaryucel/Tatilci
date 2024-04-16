package com.androiddevelopers.villabuluyorum.viewmodel.serch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.villabuluyorum.model.villa.Villa
import com.androiddevelopers.villabuluyorum.repo.FirebaseRepoInterFace
import com.androiddevelopers.villabuluyorum.util.Resource
import com.androiddevelopers.villabuluyorum.util.toVilla
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject
constructor(
    val repo : FirebaseRepoInterFace,
    val auth : FirebaseAuth
): ViewModel() {
    private var _searchResult = MutableLiveData<List<Villa>>()
    val searchResult : LiveData<List<Villa>>
        get() = _searchResult

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage : LiveData<Resource<Boolean>>
        get() = _firebaseMessage


    init {
        searchVilla("izmir","",20)
    }

    fun searchVilla(city : String,price : String,limit : Long) = viewModelScope.launch{
        println("searchVilla")
        _firebaseMessage.value = Resource.loading(null)
        repo.getVillasByCity(city,limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                println("addOnSuccessListener")
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let {
                        villa -> villaList.add(villa)
                    }
                    println("document")
                }
                _searchResult.value = villaList
                _firebaseMessage.value = Resource.success( null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                println("error : "+exception)
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
                _searchResult.value = villaList
                _firebaseMessage.value = Resource.success( null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
}