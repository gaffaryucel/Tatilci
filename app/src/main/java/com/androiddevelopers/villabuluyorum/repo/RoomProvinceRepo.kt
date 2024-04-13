package com.androiddevelopers.villabuluyorum.repo

import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Neighborhood
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.provinces.Village
import com.androiddevelopers.villabuluyorum.room.dao.ProvinceDao
import javax.inject.Inject

class RoomProvinceRepo
@Inject
constructor(
    private val provinceDao: ProvinceDao
) {
    suspend fun getAllProvince(): List<Province> =
        provinceDao.getAllProvince()

    suspend fun getAllDistrictById(provinceId: Int): List<District> =
        provinceDao.getAllDistrictById(provinceId)

    suspend fun getAllNeighborhoodById(districtId: Int): List<Neighborhood> =
        provinceDao.getAllNeighborhoodById(districtId)

    suspend fun getAllVillageById(districtId: Int): List<Village> =
        provinceDao.getAllVillageById(districtId)
}