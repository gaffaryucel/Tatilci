package com.izmirsoftware.tatilci.viewmodel.user.serch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.provinces.Province
import com.izmirsoftware.tatilci.repo.RoomProvinceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject
constructor(
    private val roomProvinceRepo: RoomProvinceRepo
) : ViewModel() {

    private var _liveDataProvinceFromRoom = MutableLiveData<List<Province>>()
    val liveDataProvinceFromRoom: LiveData<List<Province>>
        get() = _liveDataProvinceFromRoom

    init {
        getAllProvince()
    }

    private fun getAllProvince() = viewModelScope.launch {
        roomProvinceRepo.getAllProvince().flowOn(Dispatchers.IO).collect {
            _liveDataProvinceFromRoom.value = it
        }
    }
}