package com.example.moneymanager.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentHomeBinding
import com.example.moneymanager.databinding.FragmentOnboardingBinding
import com.google.android.material.appbar.MaterialToolbar

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPreferences : SharedPreferences = requireActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
        var openedFirstTime: String? = sharedPreferences.getString("FirstTimeInstall","")

        if(openedFirstTime.equals("Yes")){
            findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToHomeFragment())
        }else{

            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("FirstTimeInstall","Yes")
            editor.apply()

        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.profileName.editText?.addTextChangedListener(boardingTextWatcher)
        binding.monthlyBudget.editText?.addTextChangedListener(boardingTextWatcher)
        binding.monthlyIncome.editText?.addTextChangedListener(boardingTextWatcher)

        val sharedPreferences : SharedPreferences = this.requireActivity().getSharedPreferences("Preference",Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()

        val income = binding.monthlyIncome.editText?.text.toString()
        if(income!=""){
            editor.putFloat(getString(R.string.Income),income.toFloat())
        }

        binding.continueButton.setOnClickListener {
            val name = binding.profileName.editText?.text.toString()
            val monthlyBudget = binding.monthlyBudget.editText?.text.toString()

            editor.putString(getString(R.string.Name),name)
            editor.putFloat(getString(R.string.netBalance),monthlyBudget.toFloat())
            editor.putFloat(getString(R.string.YearlyBudget),monthlyBudget.toFloat()*12)
            editor.putFloat(getString(R.string.FinalMonthBudget),monthlyBudget.toFloat())
            editor.apply()

            findNavController().navigate(
                OnboardingFragmentDirections.actionOnboardingFragmentToHomeFragment(name,monthlyBudget.toFloat())
            )
        }
    }

    private val boardingTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            val name = binding.profileName.editText?.text.toString()
            val monthlyBudget = binding.monthlyBudget.editText?.text.toString()

//            continueButton.isEnabled = !name.isEmpty() && !monthlyBudget.isEmpty()

            if(name.isEmpty()){
                binding.continueButton.isEnabled = false
                binding.profileName.error = "This field cannot be empty"
                binding.profileName.isEndIconVisible = true

            }
            if(monthlyBudget.isEmpty()){
                binding.continueButton.isEnabled = false
                binding.monthlyBudget.error = "This field cannot be empty"
                binding.monthlyBudget.isEndIconVisible = true
            }
            else{
                binding.continueButton.isEnabled = true
                binding.monthlyBudget.isEndIconVisible = false
               binding.profileName.isEndIconVisible = false
                binding.monthlyBudget.error = null
                binding.profileName.error = null
            }

        }

    }

}