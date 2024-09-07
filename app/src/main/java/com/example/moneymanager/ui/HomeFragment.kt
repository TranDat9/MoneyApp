package com.example.moneymanager.ui

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentHomeBinding
import com.google.android.material.appbar.MaterialToolbar
import org.eazegraph.lib.models.PieModel

class HomeFragment : Fragment() {

    private lateinit var viewModel: TransactionListViewModel
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransactionListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onResume() {
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        setNetBalance(sharedPreferences,editor)
        refreshInfo(sharedPreferences,editor)
        super.onResume()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Adding Profile Button
        val toolbar : MaterialToolbar = requireActivity().findViewById(R.id.topAppBar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_person_48)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }
        appBar()
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        setNetBalance(sharedPreferences, editor)

        updatePieChart()

       binding.setBalanceInfo.setOnClickListener {
            showDialog()
        }

        // Transaction List
        with(binding.transactionList){
            layoutManager = LinearLayoutManager(activity)
            adapter = TransactionAdapter {
                val direction = HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(it)
                findNavController().navigate(
                   direction
                )


            }
        }

        binding.addTransaction.setOnClickListener{
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToAddTransactionFragment(
                    0L
                )
            )
        }

        // Month Card List
        with(binding.monthlyCardList){
            layoutManager = LinearLayoutManager(activity)
            adapter = MonthlyCardAdapter({
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToMonthlyExpenseFragment(
                        it
                    )
                )
            },requireContext())

        }

        viewModel.month.observe(viewLifecycleOwner, Observer{
            (binding.monthlyCardList.adapter as MonthlyCardAdapter).submitList(it)
        })


        viewModel.transactions.observe(viewLifecycleOwner, Observer{
            (binding.transactionList.adapter as TransactionAdapter).submitList(it)
        })

        refreshInfo(sharedPreferences, editor)
    }

    private fun refreshInfo(sharedPreferences: SharedPreferences, editor: SharedPreferences.Editor) {
        var cash = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
        var credit = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
        var bank = sharedPreferences.getFloat(getString(R.string.CASH), 0f)


        viewModel.cash.observe(viewLifecycleOwner, Observer {
            if (cash != 0f && it != null) {
                cash = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
                cash += it
                editor.putFloat(getString(R.string.CASH), cash)
                binding.cashAmount.text = cash.toString()
            }
        })
        viewModel.credit.observe(viewLifecycleOwner, Observer {
            if (credit != 0f && it != null) {
                credit = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
                credit += it
                editor.putFloat(getString(R.string.CREDIT), credit)
                binding.creditAmount.text = credit.toString()
            }
        })
        viewModel.bank.observe(viewLifecycleOwner, Observer {
            if (bank != 0f && it != null) {
                bank = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
                Log.d("TAG", "BANK: " + it)
                Log.d("TAG", "BANK S:" + bank)
                bank += it
                editor.putFloat(getString(R.string.BANK), bank)
                binding.debitAmount.text = bank.toString()
            }
        })
        updatePieChart()
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.set_balance_info, null)
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Set Details")
        val mAlertDialog = mBuilder.show()

        checkValues(dialogView)

        val setInfoButton = dialogView.findViewById<Button>(R.id.set_info)
        setInfoButton.setOnClickListener {
            mAlertDialog.dismiss()
            val cashAmount = dialogView.findViewById<EditText>(R.id.Cash).text.toString()
            val bankAmount = dialogView.findViewById<EditText>(R.id.Bank).text.toString()
            setBalanceInfo(cashAmount.toFloat(), bankAmount.toFloat(), dialogView)
        }

        val cancelButton = dialogView.findViewById<Button>(R.id.cancel)
        cancelButton.setOnClickListener { mAlertDialog.dismiss() }
        mAlertDialog.show()
    }

    private fun checkValues(dialogView: View) {
        val sharedPreferences: SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        val yearlyBudget = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget), 0f) * 12

        val cashEditText = dialogView.findViewById<EditText>(R.id.Cash)
        val bankEditText = dialogView.findViewById<EditText>(R.id.Bank)
        val setInfoButton = dialogView.findViewById<Button>(R.id.set_info)
        val creditEditText = dialogView.findViewById<EditText>(R.id.Credit)

        val boardingTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val bankAmount = bankEditText.text.toString()
                val cashAmount = cashEditText.text.toString()

                when {
                    cashAmount.isEmpty() || bankAmount.isEmpty() -> {
                        setInfoButton.isEnabled = false
                    }
                    cashAmount.toFloat() > yearlyBudget -> {
                        setInfoButton.isEnabled = false
                        cashEditText.error = "Greater than net balance available"
                    }
                    bankAmount.toFloat() > yearlyBudget || cashAmount.toFloat() + bankAmount.toFloat() > yearlyBudget -> {
                        setInfoButton.isEnabled = false
                        bankEditText.error = "Greater than net balance available"
                    }
                    else -> {
                        cashEditText.error = null
                        setInfoButton.isEnabled = true
                        val creditAmount = yearlyBudget - (cashAmount.toFloat() + bankAmount.toFloat())
                        creditEditText.setText(creditAmount.toString())
                    }
                }
            }
        }
        cashEditText.addTextChangedListener(boardingTextWatcher)
        bankEditText.addTextChangedListener(boardingTextWatcher)
    }
    private fun setBalanceInfo(cashAmount: Float, bankAmount: Float, dialog: View) {
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)

        var yearlyBudget = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)*12
        val creditAmount = (yearlyBudget) - (cashAmount + bankAmount)

        val creditEditText = dialog.findViewById<EditText>(R.id.Credit)
        creditEditText.setText(creditAmount.toString())

        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putFloat(getString(R.string.CASH),cashAmount)
        editor.putFloat(getString(R.string.BANK),bankAmount)
        editor.putFloat(getString(R.string.CREDIT),creditAmount)
        editor.putBoolean(getString(R.string.FLAG),true)
        editor.apply()


        // UpdatePieChart
        updatePieChart()

        // Refresh Fragment
        refreshFragment()

    }

    private fun setNetBalance(sharedPreferences: SharedPreferences, editor: SharedPreferences.Editor) {
        // Getting data to set up name and monthly budget in the home screen
        var yearlyBudget = sharedPreferences.getFloat(getString(R.string.YearlyBudget),0f)

        binding.netBalance.text = yearlyBudget.toString()
        viewModel.expense.observe(viewLifecycleOwner, Observer{
            if(it!=null){
                yearlyBudget = sharedPreferences.getFloat(getString(R.string.YearlyBudget),0f)
                yearlyBudget += it
                editor.putFloat(getString(R.string.YearlyBudget),yearlyBudget)
                binding.netBalance.text = yearlyBudget.toString()
            }
        })
    }

    private fun appBar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
//                R.id.profile -> {
//                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
//                    true
//                }
                R.id.calendar -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCalendarFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun updatePieChart(){

        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        var mcash = sharedPreferences.getFloat("CashAmount",0f)
        var mbank = sharedPreferences.getFloat("BankAmount",0f)
        var mcredit = sharedPreferences.getFloat("CreditAmount",0f)

        binding.cashAmount.text = ""+mcash
        binding.debitAmount.text = ""+mbank
        binding.creditAmount.text = ""+mcredit

        var cash = (mcash / (mcash+mbank+mcredit))*100
        var credit = (mcredit / (mcash+mbank+mcredit))*100
        var bank = (mbank / (mcash+mbank+mcredit))*100

        binding.piechart.clearChart()
        // Adding pie chart
        binding.piechart?.addPieSlice(
            PieModel("Cash", cash, Color.parseColor("#FFA726"))
        )
        binding.piechart?.addPieSlice(
            PieModel("Credit", credit, Color.parseColor("#EF5350"))
        )
        binding.piechart?.addPieSlice(
            PieModel("Bank", bank, Color.parseColor("#66BB6A"))
        )

        binding.piechart?.startAnimation();

    }

    @Suppress("DEPRECATION")
    fun refreshFragment() {
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }

}