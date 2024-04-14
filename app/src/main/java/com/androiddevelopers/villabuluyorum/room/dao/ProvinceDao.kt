package com.androiddevelopers.villabuluyorum.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Neighborhood
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.provinces.Village
import kotlinx.coroutines.flow.Flow

@Dao
interface ProvinceDao {
    @Query("SELECT * FROM provinces ORDER BY name COLLATE LOCALIZED ASC")
    fun getAllProvince(): Flow<List<Province>>

    @Query("SELECT * FROM districts WHERE provinceId = :provinceId ORDER BY name COLLATE LOCALIZED ASC")
    fun getAllDistrictById(provinceId: Int): Flow<List<District>>

    @Query("SELECT * FROM neighborhoods WHERE districtId = :districtId ORDER BY name COLLATE LOCALIZED ASC")
    fun getAllNeighborhoodById(districtId: Int): Flow<List<Neighborhood>>

    @Query("SELECT * FROM villages WHERE districtId = :districtId ORDER BY name COLLATE LOCALIZED ASC")
    fun getAllVillageById(districtId: Int): Flow<List<Village>>
}