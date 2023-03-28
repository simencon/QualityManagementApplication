package com.simenko.qmapp.ui.main.manufacturing

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.databinding.FragmentManufacturingBinding
import com.simenko.qmapp.ui.main.QualityManagementViewModel

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

        val binding: FragmentManufacturingBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_____manufacturing,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.departmentsDetailed.observe(viewLifecycleOwner, Observer { items ->
            items?.apply {
                rvAdapter.itemsList = items
            }
        })
    }

}

