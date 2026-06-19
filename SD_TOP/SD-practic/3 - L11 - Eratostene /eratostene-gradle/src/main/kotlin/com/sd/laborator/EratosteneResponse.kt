package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneResponse {
	private var message: String? = null
    private var result: Double = 0.0

    fun getResult(): Double{
        return result
    }

	fun setResult(result: Double) {
		this.result = result
	}

	fun getMessage(): String? {
		return message
	}

	fun setMessage(message: String?) {
		this.message = message
	}
}


