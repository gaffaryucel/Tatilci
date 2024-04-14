package com.androiddevelopers.villabuluyorum.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androiddevelopers.villabuluyorum.model.provinces.District
import com.androiddevelopers.villabuluyorum.model.provinces.Neighborhood
import com.androiddevelopers.villabuluyorum.model.provinces.Province
import com.androiddevelopers.villabuluyorum.model.provinces.Village
import com.androiddevelopers.villabuluyorum.room.dao.ProvinceDao

@Database(
    entities = [Province::class, District::class, Neighborhood::class, Village::class], version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun provinceDao(): ProvinceDao
}