package com.example.moneymanager.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.MaterialSpinnerAdapter
import com.example.moneymanager.R
import com.example.moneymanager.data.Transaction
import com.example.moneymanager.data.TransactionMode
import com.example.moneymanager.data.Type
import com.example.moneymanager.databinding.FragmentAddTransactionBinding
import com.example.moneymanager.databinding.FragmentHomeBinding
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionFragment : Fragment() {

    private lateinit var viewModel: TransactionDetailViewModel

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransactionDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Adding back button
        val toolbar: MaterialToolbar = requireActivity().findViewById(R.id.addAppBar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        // Implementation of DatePicker to set valid dates
        binding.transactionDateLayout.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy")
        binding.transactionDateLayout.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", Date())

        binding.recurringFromDate.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy")
        binding.recurringFromDate.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", Date())

        binding.recurringToDate.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy")
        binding.recurringToDate.editText?.transformIntoDatePicker(requireContext(), "dd/MM/yyyy", Date())

        // Setting up drop down
        val type = mutableListOf<String>()
        TransactionMode.values().forEach { type.add(it.name) }
        val adapter = MaterialSpinnerAdapter(requireActivity(), R.layout.spinner_item, type)
        (binding.transactionTypeSpinnerLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.transactionTypeSpinnerLayout.editText?.setText("Cash")
        //

        binding.recurringTransaction.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                binding.recurringFromDate.isEnabled = true
                binding.recurringToDate.isEnabled = true
            }else{
                binding.recurringFromDate.isEnabled = false
                binding.recurringToDate.isEnabled = false
                binding.recurringFromDate.editText?.setText("")
                binding.recurringToDate.editText?.setText("")

            }
        }

        val id = AddTransactionFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setTaskId(id)
        if(!(id == 0L)){
            disableFields()
            binding.addAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        // Handle edit icon press
                        enableFields()
                        true
                    }
                    R.id.delete -> {
                        // Handle delete icon press
                        deleteTransaction()
                        true
                    }
                    else -> false
                }
            }
        }

        viewModel.transaction.observe(viewLifecycleOwner, Observer {
            it?.let{ setData(it) }
        })
        binding.expenseButton.setOnClickListener {
            val isNull = checkNullValues()
            if(isNull)
                saveTask(Type.EXPENSE)
        }
        binding.incomeButton.setOnClickListener {
            val isNull = checkNullValues()
            if(isNull)
                saveTask(Type.INCOME)
        }


    }

    // Checking for null values
    private fun checkNullValues(): Boolean {
        val name = binding.transactionName.editText?.text.toString()
        val amount = binding.transactionAmountAdd.editText?.text.toString()
        val type = binding.transactionTypeSpinnerLayout.editText?.text.toString()
        val date = binding.transactionDateLayout.editText?.text.toString()

        if(name==""||amount==""||type==""||date==""){
            Toast.makeText(
                context,
                "Please fill all the mandatory fields",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    // Enabling fields
    private fun enableFields() {
        binding.transactionName.isEnabled = true
        binding.transactionAmountAdd.isEnabled = true
        binding.transactionTypeSpinnerLayout.isEnabled = true
        binding.recurringToDate.isEnabled = true
        binding.recurringFromDate.isEnabled = true
        binding.recurringTransaction.isEnabled = true
        binding.categorySpinnerLayout.isEnabled = true
        binding.transactionTypeSpinnerLayout.isEnabled = true
        binding.comments.isEnabled = true
        binding.expenseButton.isEnabled = true
        binding.incomeButton.isEnabled = true
    }

    // Disabling fields
    private fun disableFields() {
        binding.transactionName.isEnabled = false
        binding.transactionAmountAdd.isEnabled = false
        binding.transactionDateLayout.isEnabled = false
        binding.recurringToDate.isEnabled = false
        binding.recurringFromDate.isEnabled = false
        binding.recurringTransaction.isEnabled = false
        binding.categorySpinnerLayout.isEnabled = false
        binding.transactionTypeSpinnerLayout.isEnabled = false
        binding.comments.isEnabled = false
        binding.expenseButton.isEnabled = false
        binding.incomeButton.isEnabled = false

    }

    // Setting up data
    private fun setData(transaction: Transaction){
        binding.transactionName.editText?.setText(transaction.name)
        binding.transactionAmountAdd.editText?.setText((transaction.amount *(-1)).toString())

        var date = transaction.day.toString() +"/"+transaction.month+"/"+transaction.year
        if(transaction.month<10)
            date = transaction.day.toString() +"/0"+transaction.month+"/"+transaction.year

        binding.transactionDateLayout.editText?.setText(date)
        binding.transactionTypeSpinnerLayout.editText?.setText(transaction.transaction_type)
        binding.categorySpinnerLayout.editText?.setText(transaction.category)
        binding.comments.editText?.setText(transaction.comments)
    }

    // Saving task
    private fun <E : Enum<E>> saveTask(mode: E){
        var checkType = true

        val name = binding.transactionName.editText?.text.toString()
        val amount = binding.transactionAmountAdd.editText?.text.toString()
        val category = binding.categorySpinnerLayout.editText?.text.toString()

        //Converting amount to float
        var finalAmt = amount.toFloat()
        if(mode == Type.EXPENSE){
            finalAmt *= -1
        }

        var date = binding.transactionDateLayout.editText?.text.toString()

        // Storing date as day,month and year
        val month = Integer.parseInt(date.substring(3,5))
        val year = Integer.parseInt(date.substring(6))
        val day = Integer.parseInt(date.substring(0,2))

        if(month<10)
            date = "$year-0$month-$day"
        else
            date = "$year-$month-$day"

        // Setting date picker
        val datePicker: Date = Date(year,month,day)
        Log.d("Add Transaction","date: "+datePicker)
        val monthYear = (""+month+year).toLong()

        val type = binding.transactionTypeSpinnerLayout.editText?.text.toString()
        if(type!="Cash" && type!="Bank" && type!= "Credit"){
            Toast.makeText(
                context,
                "Invalid Type! Please select from the given types",
                Toast.LENGTH_LONG
            ).show()
            checkType=false
        }

        val comments = binding.comments.editText?.text.toString()

        var recurringFrom = binding.recurringFromDate.editText?.text.toString()
        var recurringTo = binding.recurringToDate.editText?.text.toString()

        val checkBalance = checkPossibility(type,finalAmt)

        // Checking before saving
        if(checkBalance && checkType) {
            val transaction = Transaction(
                viewModel.transactionId.value!!,
                name,
                finalAmt,
                date,
                category,
                type,
                comments,
                month,
                year,
                day,
                datePicker,
                monthYear,
                mode.toString(),
                recurringFrom,
                recurringTo
            )
            viewModel.saveTask(transaction)
            requireActivity().onBackPressed()
        }

    }

    private fun checkPossibility(type: String, finalAmt: Float): Boolean {
        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        var cash = sharedPreferences.getFloat(getString(R.string.CASH), 0f)
        var credit = sharedPreferences.getFloat(getString(R.string.CREDIT), 0f)
        var bank = sharedPreferences.getFloat(getString(R.string.BANK), 0f)
        val flag = sharedPreferences.getBoolean(getString(R.string.FLAG),false)
        val yearly = sharedPreferences.getFloat(getString(R.string.YearlyBudget),0f)

        // To check if info is added or not
        if(flag) {
            if (type == "Cash" && cash + finalAmt < 0) {
                Toast.makeText(
                    requireContext(),
                    "Transaction not possible as Cash amount is insufficient",
                    Toast.LENGTH_LONG
                ).show()
                return false
            } else if (type == "Credit" && credit + finalAmt < 0) {
                Toast.makeText(
                    context,
                    "Transaction not possible as Credit amount is insufficient",
                    Toast.LENGTH_LONG
                ).show()
                return false
            } else if (type == "Bank" && bank + finalAmt < 0) {
                Toast.makeText(
                    context,
                    "Transaction not possible as Bank amount is insufficient",
                    Toast.LENGTH_LONG
                ).show()
                return false
            }
        }else if(yearly+finalAmt<0){
            Toast.makeText(
                context,
                "Transaction not possible as Balance amount is insufficient",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true

    }


    // Setting up Calendar for DatePicker
    fun EditText.transformIntoDatePicker(context: Context, format: String, maxDate: Date? = null) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
//                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }

    fun deleteTransaction() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Alert!")

        builder.setMessage("Do you want to delete item?")

        builder.setPositiveButton("delete") { dialogInterface, which ->
            viewModel.deleteTask()
            requireActivity().onBackPressed()
        }
        builder.setNegativeButton("cancel") { dialogInterface, which ->

        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}