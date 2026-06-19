package com.sd.laborator

import io.micronaut.core.annotation.Introspected
import java.util.*

@Introspected
class EratosteneResponse {
	private lateinit var message: String
	private lateinit var response: Set<Int>


	fun setResponse(response: Set<Int>) {
		this.response = response
	}

	fun setMessage(message: String) {
		this.message = message
	}

	fun getMessage(): String
	{
		return this.message
	}

	fun getResponse(): Set<Int>
	{
		return this.response
	}
}

