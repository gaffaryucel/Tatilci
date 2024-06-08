package com.izmirsoftware.tatilci.viewmodel.user.serch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.FilterModel
import com.izmirsoftware.tatilci.model.villa.Villa
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.toVilla
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNREACHABLE_CODE")
@HiltViewModel
class SearchViewModel @Inject
constructor(
    val repo: FirebaseRepoInterFace,
    val auth: FirebaseAuth
) : ViewModel() {
    private var _searchResult = MutableLiveData<List<Villa>>()
    val searchResult: LiveData<List<Villa>>
        get() = _searchResult

    private var _filterResult = MutableLiveData<List<Villa>>()
    val filterResult: LiveData<List<Villa>>
        get() = _filterResult

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage


    init {
        getVillas(20)
    }

    fun getVillas(limit: Long) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(null)
        repo.getAllVillasFromFirestore(limit)
            .addOnSuccessListener {
                val villaList = mutableListOf<Villa>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toVilla()?.let { villa ->
                        villaList.add(villa)
                    }
                }
                _searchResult.value = villaList
                _firebaseMessage.value = Resource.success(null)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                println("getVillas")
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun searchVillasByCity(filter: FilterModel, limit: Long) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(null)
        if (filter.city.equals("Hepsi")) {
            if (searchResult.value != null) {
                filterVillas(searchResult.value!!, filter)
            }
        } else {
            repo.getVillasByCity(filter.city ?: "Ankara", limit)
                .addOnSuccessListener {
                    println("searchVillasByCity")
                    val villaList = mutableListOf<Villa>()
                    for (document in it.documents) {
                        // Belgeden her bir videoyu çek
                        document.toVilla()?.let { villa ->
                            villaList.add(villa)
                        }
                    }
                    filterVillas(villaList, filter)
                }.addOnFailureListener { exception ->
                    // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                    _firebaseMessage.value =
                        Resource.error("Belge alınamadı. Hata: $exception", null)
                }
        }

    }

    private fun filterVillas(villaList: List<Villa>, filters: FilterModel) = viewModelScope.launch {
        try {
            val villas = ArrayList<Villa>()
            if (villaList.isNotEmpty()) {
                for (villa in villaList) {
                    if (villa.nightlyRate!!.toInt() in filters.minPrice!!.toInt()..filters.maxPrice!!.toInt() &&
                        (filters.bathrooms == 99 || filters.bathrooms == villa.bathroomCount) &&
                        (filters.bedrooms == 99 || filters.bedrooms == villa.bedroomCount) &&
                        (filters.beds == 99 || filters.beds == villa.bedCount))
                    {
                        var addVilla = true

                        if (filters.hasWifi == true && villa.hasWifi != true) {
                            addVilla = false
                        }

                        if (filters.hasPool == true && villa.hasPool != true) {
                            addVilla = false
                        }

                        if (filters.quitePlace == true && villa.isQuietArea != true) {
                            addVilla = false
                        }

                        if (filters.isForSale !=  villa.forSale) {
                            addVilla = false
                        }

                        if (addVilla) {
                            villas.add(villa)
                        }
                    }
                }
            }
            if (villas.isNotEmpty()) {
                _filterResult.value = villas
                _firebaseMessage.value = Resource.success(null)
            } else {
                _firebaseMessage.value = Resource.error("Hata : Sonuç bulunamadı", null)
            }
        } catch (e: Exception) {
            _firebaseMessage.value = Resource.error("Hata : " + e.localizedMessage, null)
        }
    }

    fun searchInList(query: String?) {
        try {
            if (!query.isNullOrEmpty()) {
                val newList = searchResult.value?.let { list ->
                    list.filter {
                        it.villaName!!.contains(query, ignoreCase = true)
                    }
                }
                if (newList != null) {
                    _filterResult.value = newList!!
                }
            }
        } catch (e: Exception) {
            _filterResult.value = searchResult.value
        }
    }
}