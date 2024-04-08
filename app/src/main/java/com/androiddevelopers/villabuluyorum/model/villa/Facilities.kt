package com.androiddevelopers.villabuluyorum.model.villa

import java.io.Serializable

data class Facilities(
    var id: Int? = null,
    var landscape: Landscape? = null,
    var bath: Bath? = null,
    var bedroom: Bedroom? = null,
    var entertainment: Entertainment? = null,
    var heatingCooling: HeatingCooling? = null,
    var kitchenFood: KitchenFood? = null,
    var locationFeatures: LocationFeatures? = null,
    var outdoor: Outdoor? = null,
    var services: Services? = null
) : Serializable

data class Landscape(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)

data class Bath(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)

data class Bedroom(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)

data class Entertainment(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)

data class HeatingCooling(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)

data class KitchenFood(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)

data class LocationFeatures(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)

data class Outdoor(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)

data class Services(
    var imageResourceId: Int? = null,
    var feature: String? = null,
)