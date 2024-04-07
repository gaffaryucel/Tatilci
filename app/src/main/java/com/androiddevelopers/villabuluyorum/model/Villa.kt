package com.androiddevelopers.villabuluyorum.model

class Villa(
    var id: String? = null,
    var villaName: String? = null,
    var location: String? = null,
    var nightlyRate: Double? = null,
    var capacity: Int? = null,
    var bedroomCount: Int? = null,
    var bedCount: Int,
    var bathroomCount: Int? = null,
    var hasPool: Boolean? = null,
    var gardenArea: Double? = null,
    var interiorDesign: String? = null,
    var isQuietArea: Boolean? = null,
    var host: Host? = null,
    var amenities: List<String>? = null,
    var minStayDuration: Int? = null,
    var reservationFee: Double? = null,
    var airbnbServiceFee: Double? = null,
    var totalExcludingTaxes: Double? = null,
    var region: String? = null,
    var attractions: List<String>? = null,
    var facilities: Facilities? = null,
)

data class Host(
    var name: String? = null,
    var experience: Int? = null,
    var languages: List<String>? = null,
    var description: String? = null,
)