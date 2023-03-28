package com.simenko.qmapp.ui.main.investigations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentComposeBinding
import com.simenko.qmapp.ui.main.CreatedRecord
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition

class _InvestigationsFragment(
    private val createdRecord: CreatedRecord? = null,
    private var title: String = ""
) :
    Fragment() {

    private val viewModel: QualityManagementViewModel by lazy {
        (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentComposeBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment____compose,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        requireContext().theme

        viewModel.itemVersionsComplete.observe(viewLifecycleOwner) {
            viewModel.investigationStatuses.observe(viewLifecycleOwner) {
                viewModel.productTolerances.observe(viewLifecycleOwner) {
                    viewModel.componentTolerances.observe(viewLifecycleOwner) {
                        viewModel.componentInStageTolerances.observe(viewLifecycleOwner) {
                            viewModel.itemsTolerances.observe(viewLifecycleOwner) {
                                viewModel.metrixes.observe(viewLifecycleOwner) {
                                    viewModel.teamMembers.observe(viewLifecycleOwner) {
                                        viewModel.completeTasks.observe(viewLifecycleOwner) {
                                            binding.composeView.apply {
                                                setViewCompositionStrategy(
                                                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                                                )
                                                setContent {
                                                    InvestigationsMainComposition(
                                                        modifier = Modifier
                                                            .padding(
                                                                vertical = 2.dp,
                                                                horizontal = 2.dp
                                                            ),
                                                        appModel = viewModel,
                                                        context = context,
                                                        createdRecord = createdRecord
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return binding.root
    }
}