package com.androiddevelopers.villabuluyorum.model

class VillaModel {
    var id: String? = null
    var title: String? = null
    var imageUrl: String? = null
    var location: String? = null
    var nightlyRate: Double? = null
    var capacity: Int? = null
    var bedroomCount: Int? = null
    var bedCount: Int? = null
    var bathroomCount: Int? = null
    var hasPool: Boolean? = null
    var gardenArea: Double? = null
    var amenities: List<String>? = null
    var minStayDuration: Int? = null
    var reservationFee: Double? = null
    var airbnbServiceFee: Double? = null
    var totalExcludingTaxes: Double? = null
    var region: String? = null

    constructor(
        id: String? = null,
        title: String? = null,
        imageUrl: String? = null,
        location: String? = null,
        nightlyRate: Double? = null,
        capacity: Int? = null,
        bedroomCount: Int? = null,
        bedCount: Int? = null,
        bathroomCount: Int? = null,
        hasPool: Boolean? = null,
        gardenArea: Double? = null,
        amenities: List<String>? = null,
        minStayDuration: Int? = null,
        reservationFee: Double? = null,
        airbnbServiceFee: Double? = null,
        totalExcludingTaxes: Double? = null,
        region: String? = null
    ) {
        this.id = id
        this.title = title
        this.location = location
        this.imageUrl = imageUrl
        this.nightlyRate = nightlyRate
        this.capacity = capacity
        this.bedroomCount = bedroomCount
        this.bedCount = bedCount
        this.bathroomCount = bathroomCount
        this.hasPool = hasPool
        this.gardenArea = gardenArea
        this.amenities = amenities
        this.minStayDuration = minStayDuration
        this.reservationFee = reservationFee
        this.airbnbServiceFee = airbnbServiceFee
        this.totalExcludingTaxes = totalExcludingTaxes
        this.region = region
    }
}


