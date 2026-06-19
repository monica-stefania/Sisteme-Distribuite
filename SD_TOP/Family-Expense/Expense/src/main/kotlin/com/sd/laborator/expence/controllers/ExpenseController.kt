package com.sd.laborator.expence.controllers

import com.sd.laborator.expence.interfaces.InterfaceService
import com.sd.laborator.expence.pojo.Expense
import com.sd.laborator.expence.pojo.ExpenseRequest
import com.sd.laborator.expence.pojo.ExpenseResponse
import com.sd.laborator.expence.services.ExpenseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:5000"])
@RestController
@RequestMapping("/expenses")
class ExpenseController {
    @Autowired
    private lateinit var expenseService: InterfaceService

    @PostMapping
    fun addExpense(@RequestBody expense: ExpenseRequest): ResponseEntity<ExpenseResponse> {
        val response = expenseService.addExpense(expense)
        val status = if (response.success) HttpStatus.CREATED else HttpStatus.BAD_REQUEST
        return ResponseEntity(response, status)
    }

    @GetMapping("/{id}")
    fun getExpenseById(@PathVariable id: Long): ResponseEntity<ExpenseResponse> {
        val response = expenseService.getExpenses(id)
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity(response, status)
    }

    @PutMapping("/{id}")
    fun updateExpense(@PathVariable id: Long, @RequestBody expense: ExpenseRequest): ResponseEntity<ExpenseResponse> {
        val response = expenseService.updateExpense(id, expense)
        val status = if (response.success) HttpStatus.CREATED else HttpStatus.BAD_REQUEST
        return ResponseEntity(response, status)
    }

    @DeleteMapping("/{id}")
    fun deleteExpenseById(@PathVariable id: Long): ResponseEntity<ExpenseResponse> {
        val response = expenseService.deleteExpense(id)
        val status = if (response.success) HttpStatus.OK else HttpStatus.NOT_FOUND
        return ResponseEntity(response, status)
    }

    @GetMapping("/member/{memberId}")
    fun getByMember(@PathVariable memberId: Long): ResponseEntity<ExpenseResponse> {
        val response = expenseService.getExpensesByMemberId(memberId)
        val status = if (response.success) HttpStatus.OK else HttpStatus.NO_CONTENT
        return ResponseEntity(response, status)
    }

}