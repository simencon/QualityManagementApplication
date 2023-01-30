package com.simenko.qmapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentOrderBinding

class _____OrderFragment: Fragment() {

    private var binding: FragmentOrderBinding? = null

    val departmentsList = arrayOf("mTRB", "sTRB", "Roller Shop", "Turning", "Forging")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment___order,
            container,
            false
        )

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner = binding!!.root.findViewById<Spinner>(R.id.spinner_department)

        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, departmentsList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = aa
    }
}