package com.sd.laborator

import io.micronaut.core.annotation.Introspected
import java.util.*

@Introspected
class EratosteneResponse {
	private lateinit var message: String
	private lateinit var response: Vector<Pair<Int, Int>>


	fun setResponse(response: Vector<Pair<Int, Int>>) {
		this.response = response
	}

	fun setMessage(message: String) {
		this.message = message
	}

	fun getMessage(): String
	{
		return this.message
	}

	fun getResponse(): Vector<Pair<Int, Int>>
	{
		return this.response
	}
}


