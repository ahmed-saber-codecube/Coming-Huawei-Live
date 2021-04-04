package com.coming.customer.ui.payment.pojo

import com.google.gson.annotations.SerializedName

data class CheckPaymentStatus(

    @field:SerializedName("amount")
    val amount: String? = null,

    @field:SerializedName("paymentBrand")
    val paymentBrand: String? = null,

    @field:SerializedName("descriptor")
    val descriptor: String? = null,

    @field:SerializedName("ndc")
    val ndc: String? = null,

    @field:SerializedName("buildNumber")
    val buildNumber: String? = null,

    @field:SerializedName("paymentType")
    val paymentType: String? = null,

    @field:SerializedName("billing")
    val billing: Billing? = null,

    @field:SerializedName("result")
    val result: CheckPaymentStatusResult? = null,

    @field:SerializedName("resultDetails")
    val resultDetails: ResultDetails? = null,

    @field:SerializedName("merchantTransactionId")
    val merchantTransactionId: String? = null,

    @field:SerializedName("currency")
    val currency: String? = null,

    @field:SerializedName("customParameters")
    val customParameters: CustomParameters? = null,

    @field:SerializedName("risk")
    val risk: Risk? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("card")
    val card: Card? = null,

    @field:SerializedName("customer")
    val customer: Customer? = null,

    @field:SerializedName("timestamp")
    val timestamp: String? = null
)

data class Card(

    @field:SerializedName("bin")
    val bin: String? = null,

    @field:SerializedName("expiryMonth")
    val expiryMonth: String? = null,

    @field:SerializedName("holder")
    val holder: String? = null,

    @field:SerializedName("expiryYear")
    val expiryYear: String? = null,

    @field:SerializedName("binCountry")
    val binCountry: String? = null,

    @field:SerializedName("last4Digits")
    val last4Digits: String? = null
)

data class Customer(

    @field:SerializedName("givenName")
    val givenName: String? = null,

    @field:SerializedName("ip")
    val ip: String? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("ipCountry")
    val ipCountry: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)

data class CheckPaymentStatusResult(

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("description")
    val description: String? = null
)

data class Billing(

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("postcode")
    val postcode: String? = null,

    @field:SerializedName("street1")
    val street1: String? = null,

    @field:SerializedName("state")
    val state: String? = null
)

data class CustomParameters(

    @field:SerializedName("SHOPPER_MSDKIntegrationType")
    val sHOPPERMSDKIntegrationType: String? = null,

    @field:SerializedName("SHOPPER_device")
    val sHOPPERDevice: String? = null,

    @field:SerializedName("CTPE_DESCRIPTOR_TEMPLATE")
    val cTPEDESCRIPTORTEMPLATE: String? = null,

    @field:SerializedName("SHOPPER_OS")
    val sHOPPEROS: String? = null,

    @field:SerializedName("SHOPPER_MSDKVersion")
    val sHOPPERMSDKVersion: String? = null
)

data class ResultDetails(

    @field:SerializedName("CscResultCode")
    val cscResultCode: String? = null,

    @field:SerializedName("TransactionIdentfier")
    val transactionIdentfier: String? = null,

    @field:SerializedName("ConnectorTxID1")
    val connectorTxID1: String? = null,

    @field:SerializedName("connectorId")
    val connectorId: String? = null,

    @field:SerializedName("VerStatus")
    val verStatus: String? = null,

    @field:SerializedName("BatchNo")
    val batchNo: String? = null,

    @field:SerializedName("endToEndId")
    val endToEndId: String? = null,

    @field:SerializedName("AuthorizeId")
    val authorizeId: String? = null,

    @field:SerializedName("AvsResultCode")
    val avsResultCode: String? = null
)

data class Risk(

    @field:SerializedName("score")
    val score: String? = null
)
