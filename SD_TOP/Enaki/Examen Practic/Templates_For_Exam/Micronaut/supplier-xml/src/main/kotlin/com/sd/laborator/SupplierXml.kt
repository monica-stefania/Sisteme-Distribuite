package com.sd.laborator

import io.micronaut.core.annotation.*

@Introspected
class SupplierXml {
	private var xml: String? = null
	fun getXml(): String? {
		return xml
	}

	fun setXml(xml: String) {
		this.xml = xml
	}
}


