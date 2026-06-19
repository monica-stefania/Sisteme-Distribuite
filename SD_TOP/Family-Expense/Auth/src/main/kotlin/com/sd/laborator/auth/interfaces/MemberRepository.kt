package com.sd.laborator.auth.interfaces

import com.sd.laborator.auth.pojo.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository: JpaRepository<Member, Long> {
    fun findByUsername(username: String): Member?

    fun existsByUsername(username: String): Boolean
}