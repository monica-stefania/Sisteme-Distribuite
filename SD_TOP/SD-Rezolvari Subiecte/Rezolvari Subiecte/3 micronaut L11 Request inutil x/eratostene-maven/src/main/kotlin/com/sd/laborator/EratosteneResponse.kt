package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneResponse {
	private var message: String? = null
	private var response: List<Int>? = null


	fun setResponse(primes: List<Int>?) {
		this.response = primes
	}

	fun setMessage(message: String?) {
		this.message = message
	}

	fun getMessage(): String?
	{
		return this.message
	}

	fun getResponse(): List<Int>?
	{
		return this.response
	}
}


