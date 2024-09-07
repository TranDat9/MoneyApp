package com.example.moneymanager.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentCalendarBinding
import com.example.moneymanager.databinding.FragmentHomeBinding
import com.google.android.material.appbar.MaterialToolbar
import java.text.DateFormat
import java.util.Calendar

class CalendarFragment : Fragment() {
   private  var _binding: FragmentCalendarBinding ?=null
    private val binding get() = _binding!!
    private lateinit var viewModel: MonthlyTransactionListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MonthlyTransactionListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Adding Back Button
        val toolbar: MaterialToolbar = requireActivity().findViewById(R.id.calendarAppBar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        // get a calendar instance
        val calendar = Calendar.getInstance()

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // set the calendar date as calendar view selected date
            calendar.set(year, month, dayOfMonth)
            var m = month+1

            var date:String = "$year-$m-$dayOfMonth"
            if(month<10)
                date = "$year-0$m-$dayOfMonth"

            viewModel.setDate(date)

            Log.d("TAG","yearmonth:"+date)

            // format the calendar selected date
            val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
            val formattedDate = dateFormatter.format(calendar.time)

            // show calendar view selected date on text view
            // medium format date
            binding.textView.text = formattedDate

            // Show the corresponding transaction on screen
            with(binding.calendarList){
                layoutManager = LinearLayoutManager(activity)
                adapter = CalendarAdapter {

                }

            }

            viewModel.amountByMonth.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
                (binding.calendarList.adapter as CalendarAdapter).submitList(it)
            })



        }
    }


}