package com.example.moneymanager.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import com.example.moneymanager.data.Transaction
import com.example.moneymanager.data.TransactionDetailRepository
import kotlinx.coroutines.launch



class TransactionDetailViewModel(application: Application): AndroidViewModel(application) {
    private val repo: TransactionDetailRepository = TransactionDetailRepository(application)

    private val _transactionId = MutableLiveData<Long>(0)

    val transactionId: LiveData<Long>
        get() = _transactionId

//    val transaction: LiveData<Transaction> = Transformations.switchMap(_transactionId) { id ->
//        repo.getTask(id)
//    }

    val transaction: LiveData<Transaction> = MediatorLiveData<Transaction>().apply {
        addSource(_transactionId) { id ->
            id?.let {
                // Gọi repo.getTask và cập nhật giá trị của MediatorLiveData
                addSource(repo.getTask(it)) { task ->
                    value = task
                }
            }
        }
}

        fun setTaskId(id: Long) {
            if (_transactionId.value != id) {
                _transactionId.value = id
            }
        }

        fun saveTask(transaction: Transaction) {
            viewModelScope.launch {
                if (_transactionId.value == 0L) {
                    _transactionId.value = repo.insertTask(transaction)
                } else {
                    repo.updateTask(transaction)
                }
            }
        }

        fun deleteTask() {
            viewModelScope.launch {
                transaction.value?.let {
                    repo.deleteTask(it)
                }
            }
        }
}
