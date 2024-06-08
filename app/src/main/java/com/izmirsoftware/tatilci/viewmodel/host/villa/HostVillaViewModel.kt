package com.izmirsoftware.tatilci.viewmodel.host.villa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.izmirsoftware.tatilci.model.UserModel
import com.izmirsoftware.tatilci.model.villa.Villa
import com.izmirsoftware.tatilci.repo.FirebaseRepoInterFace
import com.izmirsoftware.tatilci.util.Resource
import com.izmirsoftware.tatilci.util.toUserModel
import com.izmirsoftware.tatilci.util.toVilla
import com.izmirsoftware.tatilci.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostVillaViewModel
@Inject constructor(
    private val repo: FirebaseRepoInterFace
) : BaseViewModel() {
    val liveDataFirebaseStatus: LiveData<Resource<Boolean>> = MutableLiveData()
    val liveDataVillaList: LiveData<List<Villa>> = MutableLiveData()
    val liveDataVilla: LiveData<UserModel> = MutableLiveData()

    fun getVillasByUserId(userId: String) = viewModelScope.launch {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)
        repo.getVillasByUserId(userId).addOnSuccessListener {
            val list = it.map { queryDocumentSnapshot -> queryDocumentSnapshot.toVilla() }

            liveDataVillaList.mutable.value = list.toList()
            liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            liveDataFirebaseStatus.mutable.value = Resource.success(false)
        }.addOnFailureListener { error ->
            error.message?.let { message ->
                liveDataFirebaseStatus.mutable.value = Resource.error(message)
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
        }
    }

    fun getUserDataByDocumentId(userId: String) = viewModelScope.launch {
        liveDataFirebaseStatus.mutable.value = Resource.loading(true)
        repo.getUserDataByDocumentId(userId).addOnSuccessListener {
            val villa = it.toUserModel()
            villa?.let { data ->
                liveDataVilla.mutable.value = data
            }

            liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            liveDataFirebaseStatus.mutable.value = Resource.success(false)
        }.addOnFailureListener { error ->
            error.message?.let { message ->
                liveDataFirebaseStatus.mutable.value = Resource.error(message)
                liveDataFirebaseStatus.mutable.value = Resource.loading(false)
            }
        }
    }
}