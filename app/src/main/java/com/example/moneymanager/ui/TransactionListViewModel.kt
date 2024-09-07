package com.example.moneymanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.moneymanager.data.MonthlyTransactions
import com.example.moneymanager.data.Transaction
import com.example.moneymanager.data.TransactionListRepository

class TransactionListViewModel(application: Application): AndroidViewModel(application) {
    private val repo : TransactionListRepository = TransactionListRepository(application)

    val transactions: LiveData<List<Transaction>>
        get() = repo.getTransactions()

    val month: LiveData<List<MonthlyTransactions>>
        get() = repo.getMonth()

    val expense: LiveData<Float>
        get() = repo.getAmount()

    val cash: LiveData<Float>
        get() = repo.getCashAmount()
    val credit: LiveData<Float>
        get() = repo.getCreditAmount()
    val bank: LiveData<Float>
        get() = repo.getBankAmount()
}