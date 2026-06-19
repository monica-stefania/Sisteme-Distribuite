package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
open class OutputResponse {
	private var message: String? = null
	private var output: Int? = null

	fun getOutput(): Int? {
		return output
	}

	fun setOutput(output: Int?) {
		this.output = output
	}

	fun getMessage(): String? {
		return message
	}

	fun setMessage(message: String?) {
		this.message = message
	}
}


