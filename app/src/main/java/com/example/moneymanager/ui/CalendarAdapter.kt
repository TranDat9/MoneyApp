package com.example.moneymanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.data.expense
import com.example.moneymanager.databinding.CardlistItemBinding
import com.example.moneymanager.databinding.ListItemBinding


class CalendarAdapter(private val listener: (String) -> Unit):
    ListAdapter<expense, CalendarAdapter.ViewHolder>(
        DiffCallback3()
    ){

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding = CardlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder (private val binding: CardlistItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        init{
        binding.root.setOnClickListener {
                listener.invoke(getItem(adapterPosition).date)
            }
        }

        fun bind(transaction: expense){
            with(transaction){
                binding.itemName.text = transaction.name
                binding.itemPrice.text = transaction.amount.toString()

            }
        }
    }
}

class DiffCallback3 : DiffUtil.ItemCallback<expense>() {
    override fun areItemsTheSame(oldItem: expense, newItem: expense): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: expense, newItem: expense): Boolean {
        return oldItem == newItem
    }
}