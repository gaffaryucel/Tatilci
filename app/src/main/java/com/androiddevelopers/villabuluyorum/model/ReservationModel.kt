package com.androiddevelopers.villabuluyorum.model

data class ReservationModel(
    val reservationId: String? = null,
    val userId: String? = null,
    val villaId: String? = null,
    val hostId: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val nights: Int? = null,
    val totalPrice: Int? = null,
    val nightlyRate: Int? = null,
    val guestCount: Int? = null,
    val paymentMethod: PaymentMethod? = null,
    val propertyType: PropertyType? = null,
    val villaImage: String? = null,
    val bedroomCount: Int? = null,
    val bathCount: Int? = null,
    val title: String? = null,
    val approvalStatus: ApprovalStatus? = null,
)
enum class PaymentMethod {
    CASH,
    CREDIT_OR_DEBIT_CARD,
    BANK_TRANSFER
}
enum class ApprovalStatus {
    WAITING_FOR_APPROVAL,
    APPROVED,
    NOT_APPROVED
}
