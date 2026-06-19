package com.sd.laborator

import com.sd.laborator.business.services.Logger
import com.sd.laborator.models.LogData
import org.junit.Ignore
import org.junit.Test

class HelloTest {
    @Test
    @Ignore
    fun test() {
        val logger = Logger("logs.json")
        logger.log("test")
    }
}
