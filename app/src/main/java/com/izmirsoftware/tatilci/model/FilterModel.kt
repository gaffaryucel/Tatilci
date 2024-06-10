package com.izmirsoftware.tatilci.model

data class FilterModel(
    var city: String? = "İzmir",
    var maxPrice: Float? = 10000F,
    var minPrice: Float? = 0F,
    var bedrooms: Int? = 99,
    var beds: Int? = 99,
    var bathrooms: Int? = 99,
    var propertyType: PropertyType? = null,
    var hasWifi : Boolean? = null,
    var hasPool : Boolean? = null,
    var quitePlace : Boolean? = null,
    var isForSale: Boolean? = null
)
enum class PropertyType{
    HOUSE,
    APARTMENT,
    GUEST_HOUSE,
    HOTEL
}
