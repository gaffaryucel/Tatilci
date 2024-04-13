package com.androiddevelopers.villabuluyorum.model.provinces

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provinces")
data class Province(
    @PrimaryKey
    val id: Int?,
    val name: String?,
    val region: String?,
)
