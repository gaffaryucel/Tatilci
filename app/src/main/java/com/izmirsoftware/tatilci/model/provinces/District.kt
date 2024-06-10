package com.izmirsoftware.tatilci.model.provinces

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "districts")
data class District(
    @PrimaryKey
    val id: Int?,
    val name: String?,
    val provinceId: Int?,
    val province: String?,
)
