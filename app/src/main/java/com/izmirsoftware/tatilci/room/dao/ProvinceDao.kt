package com.izmirsoftware.tatilci.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.izmirsoftware.tatilci.model.provinces.District
import com.izmirsoftware.tatilci.model.provinces.Neighborhood
import com.izmirsoftware.tatilci.model.provinces.Province
import com.izmirsoftware.tatilci.model.provinces.Village
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