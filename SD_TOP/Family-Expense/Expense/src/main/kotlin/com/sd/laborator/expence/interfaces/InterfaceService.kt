package com.sd.laborator.expence.interfaces

import com.sd.laborator.expence.pojo.Expense
import com.sd.laborator.expence.pojo.ExpenseRequest
import com.sd.laborator.expence.pojo.ExpenseResponse

interface InterfaceService {
    fun addExpense(request: ExpenseRequest): ExpenseResponse
    fun getExpenses(id: Long): ExpenseResponse
    fun updateExpense(id: Long, request: ExpenseRequest): ExpenseResponse
    fun deleteExpense(id: Long): ExpenseResponse
    fun getExpensesByMemberId(memberId: Long): ExpenseResponse
}