package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneResponse {
	private var message: String? = null
	private var response: Set<Int>? = null


	fun setResponse(primes: Set<Int>) {
		this.response = primes
	}

	fun setMessage(message: String?) {
		this.message = message
	}

	fun getMessage(): String?
	{
		return this.message
	}

	fun getResponse(): Set<Int>?
	{
		return this.response
	}
}


