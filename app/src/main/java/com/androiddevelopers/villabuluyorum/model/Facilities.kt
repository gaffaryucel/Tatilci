package com.androiddevelopers.villabuluyorum.model

data class Facilities(
    var id: Int,
    var landscape: Landscape,
    var bath: Bath,
    var bedroom: Bedroom,
    var entertainment: Entertainment,
    var heatingCooling: HeatingCooling,
    var kitchenFood: KitchenFood,
    var locationFeatures: LocationFeatures,
    var outdoor: Outdoor,
    var services: Services
)

data class Landscape(
    var id: Int,
    var features: Map<String, String>
)

data class Bath(
    var id: Int,
    var features: Map<String, String>
)

data class Bedroom(
    var id: Int,
    var features: Map<String, String>
)

data class Entertainment(
    var id: Int,
    var features: Map<String, String>
)

data class HeatingCooling(
    var id: Int,
    var features: Map<String, String>
)

data class KitchenFood(
    var id: Int,
    var features: Map<String, String>
)

data class LocationFeatures(
    var id: Int,
    var features: Map<String, String>
)

data class Outdoor(
    var id: Int,
    var features: Map<String, String>
)

data class Services(
    var id: Int,
    var features: Map<String, String>
)