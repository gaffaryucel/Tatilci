package com.androiddevelopers.villabuluyorum.model.villa

import java.io.Serializable

data class Facilities(
    val landscape: MutableList<String> = mutableListOf(),
    val bath: MutableList<String> = mutableListOf(),
    val bedroom: MutableList<String> = mutableListOf(),
    val entertainment: MutableList<String> = mutableListOf(),
    val heatingCooling: MutableList<String> = mutableListOf(),
    val kitchenFood: MutableList<String> = mutableListOf(),
    val locationFeatures: MutableList<String> = mutableListOf(),
    val outdoor: MutableList<String> = mutableListOf(),
    val services: MutableList<String> = mutableListOf()
) : Serializable