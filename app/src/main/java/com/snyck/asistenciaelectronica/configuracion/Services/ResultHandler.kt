package com.snyck.personal.config.Services

class ResultHandler<T> {
    var result : T? = null
    var success = false
    var error = ""

    constructor(success: Boolean, error: String, result: T?) {
        this.success = success
        this.error = error
        this.result = result
    }
}

