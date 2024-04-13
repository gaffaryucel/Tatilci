package com.androiddevelopers.villabuluyorum.model.provinces

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "neighborhoods")
data class Neighborhood(
    @PrimaryKey
    val id: Int?,
    val name: String?,
    val districtId: Int?,
    val district: String?,
    val provinceId: Int?,
    val province: String?,
)
