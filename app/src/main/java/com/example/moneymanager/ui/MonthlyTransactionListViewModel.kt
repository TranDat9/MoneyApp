package com.example.moneymanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.moneymanager.data.MonthlyTransactionListRepository
import com.example.moneymanager.data.Transaction
import com.example.moneymanager.data.expense


class MonthlyTransactionListViewModel(application: Application): AndroidViewModel(application) {
    private val repo: MonthlyTransactionListRepository = MonthlyTransactionListRepository(application)

    private val _transactionMonthYear = MutableLiveData<Long>(0)
    private val _date = MutableLiveData<String>()

//    val transactionMonthYear: LiveData<Long>
//        get() = _transactionMonthYear

//    val transactionByMonth: LiveData<List<Transaction>> = Transformations.switchMap(_transactionMonthYear){ id ->
//        repo.getTransactionByMonth(id)
//    }

    val transactionByMonth:  LiveData<List<Transaction>> = MediatorLiveData<List<Transaction>>().apply {
        addSource(_transactionMonthYear) { id ->
            id?.let {
                addSource(repo.getTransactionByMonth(it)) { task ->
                    value = task
                }
            }
        }
    }

//    val sumByMonth: LiveData<Float> = Transformations.switchMap(_transactionMonthYear){ id ->
//        repo.getSumByMonth(id)
//    }
//    val amountByMonth: LiveData<List<expense>> = Transformations.switchMap(_date){ id ->
//        repo.getAmountByMonth(id)
//    }

    val sumByMonth: LiveData<Float> = MediatorLiveData<Float>().apply {
        addSource(_transactionMonthYear) { id ->
            id?.let {
                addSource(repo.getSumByMonth(it)) { task ->
                    value = task
                }
            }
        }
    }
    val amountByMonth: LiveData<List<expense>> =MediatorLiveData<List<expense>>().apply {
        addSource(_date) { id ->
            id?.let {
                addSource(repo.getAmountByMonth(it)) { task ->
                    value = task
                }
            }
        }
    }
//    fun getAmount(date: String): List<expense>{
//        return repo.getAmountByMonth(date)
//    }

    fun setMonthYear(monthYear: Long){
        _transactionMonthYear.value = monthYear
    }
    fun setDate(date: String){
        _date.value = date
    }

}