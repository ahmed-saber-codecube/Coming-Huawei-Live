package com.coming.customer.exception

/**
 * Created by hlink21 on 28/4/16.
 */
class ApplicationException(override val message: String) : Throwable() {
    var type: Type? = null


    enum class Type {
        NO_INTERNET, NO_DATA, VALIDATION
    }
}
