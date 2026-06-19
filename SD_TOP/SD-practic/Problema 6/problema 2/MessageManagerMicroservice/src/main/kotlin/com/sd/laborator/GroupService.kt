package com.sd.laborator

import java.util.concurrent.ConcurrentHashMap

class GroupService {
    private val groups = ConcurrentHashMap<String, MutableSet<String>>()

    fun join(groupName: String, studentId: String) {
        groups.getOrPut(groupName) { mutableSetOf() }.add(studentId)
        println("$studentId joined group $groupName")
    }

    fun leave(groupName: String, studentId: String) {
        groups[groupName]?.remove(studentId)
        println("$studentId left group $groupName")
    }

    fun members(groupName: String): Set<String> {
        return groups[groupName] ?: emptySet()
    }
}