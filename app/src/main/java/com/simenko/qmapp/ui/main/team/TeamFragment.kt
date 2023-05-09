package com.simenko.qmapp.ui.main.team

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentTeamBinding
import com.simenko.qmapp.other.RandomTeamMembers.getAnyTeamMember
import com.simenko.qmapp.ui.theme.Primary900
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TeamFragment : Fragment() {

    private lateinit var viewModel: TeamViewModel

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[TeamViewModel::class.java]

        val binding: FragmentTeamBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment____team,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                QMAppTheme {
                    val context = LocalContext.current
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    viewModel.insertRecord(getAnyTeamMember[(getAnyTeamMember.indices).random()])
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Primary900
                                    )
                                }
                            )
                        },
                        floatingActionButtonPosition = FabPosition.End,
                        content = { padding ->

                            val observerLoadingProcess by viewModel.isLoadingInProgress.observeAsState()
                            val observerIsNetworkError by viewModel.isNetworkError.observeAsState()

                            val pullRefreshState = rememberPullRefreshState(
                                refreshing = observerLoadingProcess!!,
                                onRefresh = { viewModel.syncTeam() }
                            )

                            Box(
                                Modifier
                                    .pullRefresh(pullRefreshState)
                                    .padding()
                            ) {
                                TeamComposition(appModel = viewModel)
                            }

                            if (observerIsNetworkError == true) {
                                Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show()
                                viewModel.onNetworkErrorShown()
                            }
                        }
                    )
                }
            }

            return binding.root
        }
    }
}

