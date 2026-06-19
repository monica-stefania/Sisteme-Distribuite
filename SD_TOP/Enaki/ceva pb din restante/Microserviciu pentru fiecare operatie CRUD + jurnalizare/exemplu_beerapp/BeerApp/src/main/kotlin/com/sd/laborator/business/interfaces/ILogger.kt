package com.sd.laborator.business.interfaces

interface ILogger {
    fun log(operationType: String)
    fun setFileName(fileName: String)
}