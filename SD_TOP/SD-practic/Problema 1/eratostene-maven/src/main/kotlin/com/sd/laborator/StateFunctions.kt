package com.sd.laboratory

import io.micronaut.function.FunctionBean
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.util.function.Function

@Singleton
@Named("state-start")
class StateStartFunction : Function<AutomatonMessage, String> {
    override fun apply(msg: AutomatonMessage): String {
        return if (msg.number >= 2) "INIT_SIEVE" else "REJECT"
    }
}

@Singleton
@Named("state-init-sieve")
class StateInitSieve : Function<AutomatonMessage, String> {
    override fun apply(msg: AutomatonMessage): String {
        msg.primes.clear()
        msg.primes.addAll(2..msg.number)
        return "SIEVE"    }
}

@Singleton
@Named("state-sieve")
class StateSieve : Function<AutomatonMessage, String> {
    override fun apply(msg: AutomatonMessage): String {
        val limit = msg.number
        val sieve = BooleanArray(limit + 1) { true }
        sieve[0] = false; sieve[1] = false
        var p = 2
        while (p * p <= limit) {
            if (sieve[p]) {
                var multiple = p * p
                while (multiple <= limit) { sieve[multiple] = false; multiple += p }
            }
            p++
        }
        msg.primes.clear()
        msg.primes.addAll((2..limit).filter { sieve[it] })
        return "COLLECT"    }
}

@Singleton
@Named("state-collect")
class StateCollect : Function<AutomatonMessage, String> {
    override fun apply(msg: AutomatonMessage): String {
        return if (msg.primes.isNotEmpty()) "ACCEPT" else "REJECT"
    }
}

@Singleton
@Named("state-accept")
class StateAccept : Function<AutomatonMessage, String> {
    override fun apply(msg: AutomatonMessage): String {
        println("ACCEPT: primes up to ${msg.number} → ${msg.primes}")
        return "DONE"    }
}

@Singleton
@Named("state-reject")
class StateReject : Function<AutomatonMessage, String> {
    override fun apply(msg: AutomatonMessage): String {
        println("REJECT: ${msg.number} is invalid or has no primes")
        return "DONE"    }
}