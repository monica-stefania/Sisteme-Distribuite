package com.sd.laborator

class StreamProcessor(
    private val clientRegistry: ClientRegistry,
    private val groupService: GroupService
) {
    fun process(message: ChatMessage) {
        println("Procesez: $message")
        when (message.type) {

            MessageType.PRIVATE -> sendToOne(message)
            MessageType.PUBLIC -> sendToAll(message)
            MessageType.GROUP -> sendToGroup(message)
            MessageType.JOIN -> joinGroup(message)
            MessageType.LEAVE -> leaveGroup(message)
        }
    }

    private fun sendToOne(message: ChatMessage) {
        val target = message.target ?: return
        val socket = clientRegistry.getClient(target) ?: return

        socket.getOutputStream().write(
            "[PRIVATE from ${message.senderId}] ${message.content}\n".toByteArray()
        )
    }

    private fun sendToAll(message: ChatMessage) {
        clientRegistry.allClients()
            .filterKeys { it != message.senderId }
            .values
            .forEach { socket ->
                socket.getOutputStream().write(
                    "[PUBLIC from ${message.senderId}] ${message.content}\n".toByteArray()
                )
            }
    }

    private fun sendToGroup(message: ChatMessage) {
        val group = message.target ?: return

        groupService.members(group).forEach { studentId ->
            val socket = clientRegistry.getClient(studentId)
            socket?.getOutputStream()?.write(
                "[GROUP $group from ${message.senderId}] ${message.content}\n".toByteArray()
            )
        }
    }

    private fun joinGroup(message: ChatMessage) {
        val group = message.target ?: return
        groupService.join(group, message.senderId)
    }

    private fun leaveGroup(message: ChatMessage) {
        val group = message.target ?: return
        groupService.leave(group, message.senderId)
    }
}