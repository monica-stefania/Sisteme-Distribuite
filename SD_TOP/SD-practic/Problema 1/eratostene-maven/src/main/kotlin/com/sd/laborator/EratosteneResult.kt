package com.sd.laboratory

import jakarta.inject.Singleton

@Singleton
class EratosteneResult {
    private val results = mutableMapOf<Int, AutomatonMessage>()

    fun save(msg: AutomatonMessage) { results[msg.number] = msg }
    fun get(n: Int): AutomatonMessage? = results[n]
    fun all(): Map<Int, AutomatonMessage> = results.toMap()
}