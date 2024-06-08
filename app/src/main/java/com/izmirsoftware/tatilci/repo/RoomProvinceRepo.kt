package com.izmirsoftware.tatilci.repo

import com.izmirsoftware.tatilci.model.provinces.District
import com.izmirsoftware.tatilci.model.provinces.Neighborhood
import com.izmirsoftware.tatilci.model.provinces.Province
import com.izmirsoftware.tatilci.model.provinces.Village
import com.izmirsoftware.tatilci.room.dao.ProvinceDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomProvinceRepo
@Inject
constructor(
    private val provinceDao: ProvinceDao
) {
    fun getAllProvince(): Flow<List<Province>> =
        provinceDao.getAllProvince()

    fun getAllDistrictById(provinceId: Int): Flow<List<District>> =
        provinceDao.getAllDistrictById(provinceId)

    fun getAllNeighborhoodById(districtId: Int): Flow<List<Neighborhood>> =
        provinceDao.getAllNeighborhoodById(districtId)

    fun getAllVillageById(districtId: Int): Flow<List<Village>> =
        provinceDao.getAllVillageById(districtId)
}