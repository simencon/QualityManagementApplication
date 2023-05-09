package com.simenko.qmapp.ui.main.team

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentTeamBinding
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TeamFragment : Fragment() {

    private lateinit var viewModel: TeamViewModel

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
                    TeamMembersLiveData(Modifier.fillMaxSize(), viewModel)
                }
            }
        }

        return binding.root
    }
}

