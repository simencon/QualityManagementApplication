package com.simenko.qmapp.ui.manufacturing

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.MainActivity
import com.simenko.qmapp.databinding.FragmentRvOnlyBinding
import com.simenko.qmapp.ui.QualityManagementViewModel

class ManufacturingFragment : Fragment() {

    private val viewModel: QualityManagementViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        val model = (activity as MainActivity).viewModel
        model
    }

    private val rvAdapter by lazy {
        Adapter__________Department(
            DepartmentClick { item, position ->
                item.departmentDetailsVisibility = !item.departmentDetailsVisibility
                updateOneRvItem(position)
            }, viewModel, viewLifecycleOwner
        )
    }

    private fun updateOneRvItem(position: Int) {
        rvAdapter?.notifyItemChanged(position)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentRvOnlyBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_____rv_only,
            container,
            false
        )

        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }
        )

        return binding.root
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.departmentsDetailed.observe(viewLifecycleOwner, Observer { items ->
            items?.apply {
                (rvAdapter as Adapter__________Department).itemsList = items
            }
        })
    }

}

