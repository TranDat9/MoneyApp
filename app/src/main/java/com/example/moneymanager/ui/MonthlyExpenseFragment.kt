package com.example.moneymanager.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentHomeBinding
import com.example.moneymanager.databinding.FragmentMonthlyExpenseBinding
import com.google.android.material.appbar.MaterialToolbar

class MonthlyExpenseFragment : Fragment() {

    private lateinit var viewModel: MonthlyTransactionListViewModel

    private var _binding: FragmentMonthlyExpenseBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MonthlyTransactionListViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentMonthlyExpenseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Adding back button
        val toolbar : MaterialToolbar = requireActivity().findViewById(R.id.monthAppBar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        setMonthlyBalance()

        val monthYear = MonthlyExpenseFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setMonthYear(monthYear)

        // Transaction List
        with(binding.monthlyTransactionList){
            layoutManager = LinearLayoutManager(activity)
            adapter = TransactionAdapter {
                findNavController().navigate(
                    MonthlyExpenseFragmentDirections.actionMonthlyExpenseFragmentToAddTransactionFragment(
                        it
                    )
                )
            }
        }

        viewModel.transactionByMonth.observe(viewLifecycleOwner, Observer{
            (binding.monthlyTransactionList.adapter as TransactionAdapter).submitList(it)
        })

        binding.addTransaction.setOnClickListener {
            findNavController().navigate(MonthlyExpenseFragmentDirections.actionMonthlyExpenseFragmentToAddTransactionFragment(0))
        }


    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun setMonthlyBalance(){
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        var monthlyBalance = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()

        viewModel.sumByMonth.observe(viewLifecycleOwner, Observer {
            var monthBalance = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)
            if(it!=null) {
                monthBalance += it
                binding.monthBalance.text = monthBalance.toString()
                updateProgressBar(sharedPreferences, monthBalance)
                binding.amountSaved.text = (monthBalance).toString()
                binding.amountSpent.text = (it*-1).toString()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateProgressBar(sharedPreferences: SharedPreferences, balance: Float) {
        var monthBalance = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)

        var progress = 100-(balance/monthBalance)*100

        if(progress>100){
            binding.pb.progress = 100
        }else {
            binding.pb.progress = progress.toInt()
        }

    }



}