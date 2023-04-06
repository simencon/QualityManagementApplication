package com.simenko.qmapp.ui.main.team

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentTeamBinding
import com.simenko.qmapp.ui.main.CreatedRecord
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.main.investigations.steps.statusDialog
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class TeamFragment : Fragment() {

    private lateinit var viewModel: QualityManagementViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as BaseApplication).appComponent.mainComponent().create()
            .inject(this)
        viewModel = ViewModelProvider(this, providerFactory)[QualityManagementViewModel::class.java]

        val binding: FragmentTeamBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment____team,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

//        viewModel.team.observe(viewLifecycleOwner) {
//            viewModel.addTeamToSnapShot(it)
//        }

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                QMAppTheme {
//                    TeamMembersLiveData(Modifier.fillMaxSize(), viewModel)
                    OrdersComposition(
                        modifier = Modifier.fillMaxSize(),
                        appModel = viewModel,
                        onListEnd = {  },
                        createdRecord = CreatedRecord(),
                        showStatusDialog = { a, b, c -> statusDialog(a, b, c) }
                    )
                }
            }
        }

        return binding.root
    }
}

