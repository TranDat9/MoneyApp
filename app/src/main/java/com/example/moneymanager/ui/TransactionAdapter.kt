package com.example.moneymanager.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.data.Transaction
import com.example.moneymanager.data.Type
import com.example.moneymanager.databinding.ListItemBinding

class TransactionAdapter(private val listener: (Long) -> Unit) :
    ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.invoke(getItem(adapterPosition).id)
            }
        }

        fun bind(transaction: Transaction) {
            with(binding) {
                transactionMode.text = transaction.transaction_type
                when (transaction.transaction_type) {
                    "Cash" -> {
                        transactionTypeView.setBackgroundResource(R.color.cash)
                    }
                    "Credit" -> {
                        transactionTypeView.setBackgroundResource(R.color.credit)
                    }
                    "Bank" -> {
                        transactionTypeView.setBackgroundResource(R.color.bank)
                    }
                }

                transactionName.text = transaction.name
                if (transaction.income_expense.equals(Type.EXPENSE.toString())) {
                    transactionAmount.text = "" + transaction.amount
                    transactionAmount.setTextColor(Color.RED)
                } else {
                    transactionAmount.text = "+" + transaction.amount
                    transactionAmount.setTextColor(Color.GREEN)
                }
                transactionDate.text = "" + transaction.date
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
    override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
        return oldItem == newItem
    }
}