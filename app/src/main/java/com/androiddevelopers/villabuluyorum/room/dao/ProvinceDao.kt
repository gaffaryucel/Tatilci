package com.androiddevelopers.villabuluyorum.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Neighborhood
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.provinces.Village

@Dao
interface ProvinceDao {
    @Query("SELECT * FROM provinces")
    suspend fun getAllProvince(): List<Province>

    @Query("SELECT * FROM districts WHERE provinceId = :provinceId")
    suspend fun getAllDistrictById(provinceId: Int): List<District>

    @Query("SELECT * FROM neighborhoods WHERE districtId = :districtId")
    suspend fun getAllNeighborhoodById(districtId: Int): List<Neighborhood>

    @Query("SELECT * FROM villages WHERE districtId = :districtId")
    suspend fun getAllVillageById(districtId: Int): List<Village>
}