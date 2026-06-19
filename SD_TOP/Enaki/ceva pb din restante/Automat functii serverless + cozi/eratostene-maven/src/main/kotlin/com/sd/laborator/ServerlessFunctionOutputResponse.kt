package com.sd.laborator

import io.micronaut.core.annotation.Introspected

class ServerlessFunctionOutputResponse: OutputResponse() {
	private var newState: Int? = null
	fun getNewState(): Int? = newState
	fun setNewState(newState: Int?) {
		this.newState = newState
	}
}


