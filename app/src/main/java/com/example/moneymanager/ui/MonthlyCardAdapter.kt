package com.example.moneymanager.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.data.MonthlyTransactions
import com.example.moneymanager.databinding.ListItemBinding
import com.example.moneymanager.databinding.MonthCardBinding
import com.example.moneymanager.selectMonth

class MonthlyCardAdapter(private val listener: (Long) -> Unit, val context: Context):
    ListAdapter<MonthlyTransactions, MonthlyCardAdapter.ViewHolder>(
        DiffCallback2()
    ){

    private var viewPool : RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding = MonthCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: MonthCardBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener{
                listener.invoke(getItem(adapterPosition).monthYear)
            }

        }


        @SuppressLint("WrongConstant")
        fun bind(monthlyTransactions: MonthlyTransactions){
            val sharedPreferences : SharedPreferences = context.getSharedPreferences("Preference", Context.MODE_PRIVATE)
            var monthlyBudget = sharedPreferences.getFloat("Budget",0f)
            with(binding) {
                monthName.text = selectMonth(monthlyTransactions.month)
                yearName.text = " " + monthlyTransactions.year.toString()
                Log.d("MonthlyCard", "aug: " + monthlyTransactions.sum + " " + monthlyBudget)
                if ((monthlyTransactions.sum * (-1)) > monthlyBudget) {
                    budgetExceeded.text = "Budget Exceeded"
                    budgetExceeded.error = "Budget Exceeded"
                } else {
                    budgetExceeded.text = ""
                    budgetExceeded.error = null
                }

                val childLayoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                childLayoutManager.initialPrefetchItemCount = 4
                monthcardList.apply {
                    layoutManager = childLayoutManager
                    adapter = MonthCardAdapter(monthlyTransactions.children)
                    setRecycledViewPool(viewPool)
                }

            }


        }
    }

}


class DiffCallback2 : DiffUtil.ItemCallback<MonthlyTransactions>() {
    override fun areItemsTheSame(oldItem: MonthlyTransactions, newItem: MonthlyTransactions): Boolean {
        return oldItem.monthYear == newItem.monthYear
    }

    override fun areContentsTheSame(oldItem: MonthlyTransactions, newItem: MonthlyTransactions): Boolean {
        return oldItem == newItem
    }
}