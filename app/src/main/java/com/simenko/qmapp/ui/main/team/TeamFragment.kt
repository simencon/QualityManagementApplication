package com.simenko.qmapp.ui.main.team

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentRvOnlyBinding
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.main.team.ui.theme.QMAppTheme
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class TeamFragment : Fragment() {


    private lateinit var viewModel: QualityManagementViewModel
    private lateinit var composition: ComposeView

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    private val rvAdapter by lazy {
        Adapter___________TeamMember(
            TeamMemberClick {
                Toast.makeText(context, it.selectedRecord(), Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity?.application as BaseApplication).appComponent.mainComponent().create()
            .inject(this)
        viewModel = ViewModelProvider(this, providerFactory)[QualityManagementViewModel::class.java]

        val binding: FragmentRvOnlyBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_____rv_only,
            container,
            false
        )
        composition = binding.composeView

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }
        )

        composition.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            /*setContent {
                QMAppTheme {
                    MyApp(Modifier.fillMaxSize())
                }
            }*/
        }

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

        viewModel.teamMembers.observe(viewLifecycleOwner, Observer { items ->
            items?.apply {
                rvAdapter.itemsList = items

                composition.apply {
                    // Dispose of the Composition when the view's LifecycleOwner
                    // is destroyed
//                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        QMAppTheme {
                            TeamMembers(Modifier.fillMaxSize(), items)
                        }
                    }
                }

            }
        })
    }

}

