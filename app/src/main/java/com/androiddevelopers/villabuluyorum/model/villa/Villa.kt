package com.androiddevelopers.villabuluyorum.model.villa

data class Villa(
    var villaId: String? = null, // villanın database id si
    var hostId: String? = null, // ilanı oluşturtan kullanıcı database id si
    var villaName: String? = null, // ilan başlığı
    var description: String? = null, // ilan açıklaması
    var locationProvince: String? = null, // ilan ili
    var locationDistrict: String? = null, // ilan ilçesi
    var locationNeighborhoodOrVillage: String? = null, // ilan mahalle/köy
    var locationAddress: String? = null, // ilan adresi
    var nightlyRate: Double? = null, // gecelik ücret
    var capacity: Int? = null,  // konaklayabilecek kişi sayısı
    var bedroomCount: Int? = null, // yatak odası  sayısı
    var bedCount: Int? = null, // yatak sayısı
    var bathroomCount: Int? = null, // banyo sayısı
    var restroom: Int? = null, // tuvalet sayısı
    var hasPool: Boolean? = null, // havyz var mı?
    var gardenArea: Double? = null,
    var interiorDesign: String? = null,
    var isQuietArea: Boolean? = null,
    var amenities: List<String>? = null,
    var minStayDuration: Int? = null,
    var reservationFee: Double? = null,
    var airbnbServiceFee: Double? = null,
    var totalExcludingTaxes: Double? = null,
    var region: String? = null,
    var attractions: List<String>? = null,
    var facilities: Facilities? = null,
    var currency: String? = null, // ilan para birimi
    var coverImage: String? = null, // kapak resmi
    var otherImages: List<String>? = null, // ilan için eklenen diğer resimler
    var rating: Double? = null,

    )