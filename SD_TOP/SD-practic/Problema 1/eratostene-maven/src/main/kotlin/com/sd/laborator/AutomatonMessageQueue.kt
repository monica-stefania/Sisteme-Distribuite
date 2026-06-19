package com.sd.laboratory

import jakarta.inject.Singleton
import java.util.concurrent.LinkedBlockingQueue

@Singleton
class AutomatonMessageQueue {
    val queue = LinkedBlockingQueue<AutomatonMessage>()

    fun send(msg: AutomatonMessage) = queue.offer(msg)
    fun receive(): AutomatonMessage? = queue.poll()
}