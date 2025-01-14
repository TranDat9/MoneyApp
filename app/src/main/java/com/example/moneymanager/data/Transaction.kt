package com.example.moneymanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TransactionMode{
    Cash, Credit, Bank
}
enum class Type{
    INCOME, EXPENSE
}

@Entity(tableName = "transaction")
data class Transaction (@PrimaryKey(autoGenerate = true) val id: Long,
                        val name: String,
                        val amount: Float,
                        val date: String,
                        val category: String,
                        val transaction_type: String,
                        val comments: String,
                        val month: Int,
                        val year: Int,
                        val day: Int,
                        val datePicker: Date,
                        val monthYear: Long,
                        val income_expense : String,
                        val recurring_from : String,
                        val recurring_to : String
)