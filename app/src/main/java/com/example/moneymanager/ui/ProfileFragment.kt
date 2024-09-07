package com.example.moneymanager.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentHomeBinding
import com.example.moneymanager.databinding.FragmentOnboardingBinding
import com.example.moneymanager.databinding.FragmentProfileBinding
import com.google.android.material.appbar.MaterialToolbar


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Adding back button
        val toolbar : MaterialToolbar = requireActivity().findViewById(R.id.profileTopAppBar)
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference", Context.MODE_PRIVATE)
        var monthlyBudget = sharedPreferences.getFloat(getString(R.string.FinalMonthBudget),0f)
        var name = sharedPreferences.getString(getString(R.string.Name),null)
        var income = sharedPreferences.getFloat(getString(R.string.Income),0f)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()

        disableFields()
        if(name!=null){
            setValues(name,monthlyBudget,income)
        }
        appBar()


//        saveButton.isEnabled = !(newName=="" || newMonthlyBudget=="")

        binding.saveButton.setOnClickListener {
            var newName = binding.profileName.editText?.text.toString()
            var newMonthlyBudget = binding.monthlyBudget.editText?.text.toString()
            var newIncome = binding.monthlyIncome.editText?.text.toString()
            if(!newMonthlyBudget.equals(monthlyBudget)) {
                if (newName != "" && newMonthlyBudget != "")
                    updateDetails(editor, newName, newMonthlyBudget, newIncome)
            }
            requireActivity().onBackPressed()

        }

    }

    private fun setValues(name: String, monthlyBudget: Float, income: Float) {
        binding.profileName.editText?.setText(name)
        binding.monthlyBudget.editText?.setText(monthlyBudget.toString())
        if(income!=0f)
            binding.monthlyIncome.editText?.setText(income.toString())
    }

    private fun appBar() {
        binding.profileTopAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.editProfile -> {
                    enableFields()
                    true
                }
                else -> false
            }
        }
    }

    private fun updateDetails(editor: SharedPreferences.Editor, newName: String, newMonthlyBudget: String, newIncome: String) {
        editor.putString(getString(R.string.Name),newName)
        editor.putFloat(getString(R.string.FinalMonthBudget),newMonthlyBudget.toFloat())
        editor.putFloat(getString(R.string.netBalance),newMonthlyBudget.toFloat())
        editor.putFloat(getString(R.string.YearlyBudget),newMonthlyBudget.toFloat()*12)
        if(newIncome!="")
            editor.putFloat(getString(R.string.Income),newIncome.toFloat())

        // reset cash,credit,debit info
        editor.putFloat(getString(R.string.CASH),0f)
        editor.putFloat(getString(R.string.CREDIT),0f)
        editor.putFloat(getString(R.string.BANK),0f)
        editor.putBoolean(getString(R.string.FLAG),false)

        editor.commit()
    }

    private fun enableFields() {
        binding.profileName.isEnabled = true
        binding.monthlyBudget.isEnabled = true
        binding.monthlyIncome.isEnabled = true
        binding.saveButton.isEnabled = true
    }

    private fun disableFields() {
        binding.profileName.isEnabled = false
        binding.monthlyBudget.isEnabled = false
        binding.monthlyIncome.isEnabled = false
        binding.saveButton.isEnabled = false
    }


}