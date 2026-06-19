package com.sd.laboratory

import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.util.function.Function

@Singleton
class AutomatonProcessor(
    @Named("state-start")      private val stateStart: Function<AutomatonMessage, String>,
    @Named("state-init-sieve") private val stateInitSieve: Function<AutomatonMessage, String>,
    @Named("state-sieve")      private val stateSieve: Function<AutomatonMessage, String>,
    @Named("state-collect")    private val stateCollect: Function<AutomatonMessage, String>,
    @Named("state-accept")     private val stateAccept: Function<AutomatonMessage, String>,
    @Named("state-reject")     private val stateReject: Function<AutomatonMessage, String>,
    private val queue: AutomatonMessageQueue,
    private val resultStore: EratosteneResult
) {
    @Scheduled(fixedDelay = "500ms")
    fun process() {
        val msg = queue.receive() ?: return
        var state = msg.state

        while (state != "DONE") {
            state = when (state) {
                "START"      -> stateStart.apply(msg)
                "INIT_SIEVE" -> stateInitSieve.apply(msg)
                "SIEVE"      -> stateSieve.apply(msg)
                "COLLECT"    -> stateCollect.apply(msg)
                "ACCEPT"     -> stateAccept.apply(msg)
                "REJECT"     -> stateReject.apply(msg)
                else         -> "DONE"
            }
            msg.state = state
        }

        resultStore.save(msg)
    }
}