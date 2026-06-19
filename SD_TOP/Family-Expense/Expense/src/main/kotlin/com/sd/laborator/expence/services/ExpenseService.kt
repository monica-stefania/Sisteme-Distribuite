package com.sd.laborator.expence.services

import com.sd.laborator.expence.interfaces.ExpenseRespository
import com.sd.laborator.expence.interfaces.InterfaceService
import com.sd.laborator.expence.pojo.Expense
import com.sd.laborator.expence.pojo.ExpenseRequest
import com.sd.laborator.expence.pojo.ExpenseResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExpenseService: InterfaceService {

    @Autowired
    private lateinit var expenseRepository: ExpenseRespository

    override fun addExpense(request: ExpenseRequest): ExpenseResponse {
       if(request.amount <= 0)
           return ExpenseResponse(false, "The amount of expense must be greater than zero!")
        if(request.category.isBlank())
            return ExpenseResponse(false, "The category can't be empty!")
        if(request.date.isBlank())
            return ExpenseResponse(false, "The date can't be empty!")
        val expense = Expense(
            memberId    = request.memberId,
            amount      = request.amount,
            category    = request.category,
            description = request.description,
            date        = request.date
        )

        expenseRepository.save(expense)

        return ExpenseResponse(true, "The new expense has been added successfully!", expense)
    }
    override fun deleteExpense(id: Long): ExpenseResponse {
        if(!expenseRepository.existsById(id))
            return ExpenseResponse(false, "Expense with id=$id not found!")
        expenseRepository.deleteById(id)
        return ExpenseResponse(true, "Expense with id=$id was deleted!")
    }

    override fun getExpenses(id: Long): ExpenseResponse {
        val expense = expenseRepository.findById(id).orElse(null)
        if(expense == null)
            return ExpenseResponse(false, "Expense with id=$id not found!")
        return ExpenseResponse(true, "Expense with id=$id was found!", expense)
    }

    override fun updateExpense(id: Long, request: ExpenseRequest): ExpenseResponse {
        val expense = expenseRepository.findById(id).orElse(null)
        if(expense == null)
            return ExpenseResponse(false, "Expense with id=$id not found!")
        expense.amount = request.amount
        expense.category = request.category
        expense.description = request.description
        expense.date = request.date
        expenseRepository.save(expense)
        return ExpenseResponse(true, "Expense with id=$id was updated!", expense)
    }

    override fun getExpensesByMemberId(memberId: Long): ExpenseResponse {
        val list = expenseRepository.findByMemberId(memberId)
        if (list.isEmpty()) {
            return ExpenseResponse(false, "Nu exista cheltuieli pentru membrul $memberId.")
        }
        return ExpenseResponse(true, "Lista cheltuielilor.", expenses = list)
    }
}