package com.androiddevelopers.villabuluyorum.model

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


/*
data class VillaModel(
    val villaName: String,
    val location: String,
    val nightlyRate: Double,
    val capacity: Int,
    val bedroomCount: Int,
    val bedCount: Int,
    val bathroomCount: Int,
    val hasPool: Boolean,
    val gardenArea: Double,
    val interiorDesign: String,
    val isQuietArea: Boolean,
    val host: Host,
    val amenities: List<String>,
    val minStayDuration: Int,
    val reservationFee: Double,
    val airbnbServiceFee: Double,
    val totalExcludingTaxes: Double,
    val region: String,
    val attractions: List<String>
) {
    constructor(
        villaName: String,
        location: String,
        nightlyRate: Double,
        capacity: Int,
        bedroomCount: Int,
        bedCount: Int,
        bathroomCount: Int,
        hasPool: Boolean,
        gardenArea: Double,
        interiorDesign: String,
        isQuietArea: Boolean,
        hostName: String,
        hostExperience: Int,
        hostLanguages: List<String>,
        hostDescription: String,
        amenities: List<String>,
        minStayDuration: Int,
        reservationFee: Double,
        airbnbServiceFee: Double,
        totalExcludingTaxes: Double,
        region: String,
        attractions: List<String>
    ) : this(
        villaName,
        location,
        nightlyRate,
        capacity,
        bedroomCount,
        bedCount,
        bathroomCount,
        hasPool,
        gardenArea,
        interiorDesign,
        isQuietArea,
        Host(hostName, hostExperience, hostLanguages, hostDescription),
        amenities,
        minStayDuration,
        reservationFee,
        airbnbServiceFee,
        totalExcludingTaxes,
        region,
        attractions
    )
}

data class Host(
    val name: String,
    val experience: Int,
    val languages: List<String>,
    val description: String
)

 */