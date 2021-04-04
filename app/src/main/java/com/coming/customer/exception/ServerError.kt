package com.throdle.exception

/**
 * Created by hlink on 6/2/18.
 */

class ServerError : RuntimeException {

    val errorBody: String


    constructor(message: String) : super(message) {
        errorBody = ""
    }

    constructor(message: String, errorBody: String) : super(message) {
        this.errorBody = errorBody
    }
}
