package com.androiddevelopers.villabuluyorum.model

data class FilterModel(
    var city: String? = "İzmir",
    var maxPrice: Float? = 10000F,
    var minPrice: Float? = 0F,
    var bedrooms: Int? = 99,
    var beds: Int? = 99,
    var bathrooms: Int? = 99,
    var isFavorite: Boolean? = null,
    var propertyType: PropertyType? = null,
    var amenities: ArrayList<String>? = null
)
enum class PropertyType{
    HOUSE,
    APARTMENT,
    GUEST_HOUSE,
    HOTEL
}
