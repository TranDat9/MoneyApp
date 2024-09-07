package com.example.moneymanager.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.data.expense


class MonthCardAdapter(private val children : List<expense>)
    : RecyclerView.Adapter<MonthCardAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {

        val v =  LayoutInflater.from(parent.context)
            .inflate(R.layout.cardlist_item,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: ViewHolder,
                                  position: Int) {
        val child = children[position]
        holder.itemName.text = child.name
        if(child.amount<0) {
            holder.itemAmount.text = child.amount.toString()
            holder.itemAmount.setTextColor(Color.RED)
        }else{
            holder.itemAmount.text = "+"+child.amount.toString()
            holder.itemAmount.setTextColor(Color.GREEN)
        }
    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val itemName : TextView = itemView.findViewById(R.id.item_name)
        val itemAmount: TextView = itemView.findViewById(R.id.item_price)

    }
}

