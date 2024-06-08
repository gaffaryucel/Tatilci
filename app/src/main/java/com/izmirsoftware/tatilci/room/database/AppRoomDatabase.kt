package com.izmirsoftware.tatilci.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.izmirsoftware.tatilci.model.provinces.District
import com.izmirsoftware.tatilci.model.provinces.Neighborhood
import com.izmirsoftware.tatilci.model.provinces.Province
import com.izmirsoftware.tatilci.model.provinces.Village
import com.izmirsoftware.tatilci.room.dao.ProvinceDao

@Database(
    entities = [Province::class, District::class, Neighborhood::class, Village::class], version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun provinceDao(): ProvinceDao
}