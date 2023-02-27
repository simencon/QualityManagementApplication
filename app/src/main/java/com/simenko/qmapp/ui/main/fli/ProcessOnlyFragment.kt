package com.simenko.qmapp.ui.main.fli

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentRvOnlyBinding
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.main.investigations.orders.InvestigationsAll
import com.simenko.qmapp.ui.main.investigations.orders.OrdersLiveData
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class ProcessOnlyFragment : Fragment() {

    private lateinit var viewModel: QualityManagementViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

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

        binding.composeView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        )
        binding.recyclerView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                0.0f
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel


        viewModel.eventNetworkError.observe(
            viewLifecycleOwner, Observer { isNetworkError ->
                if (isNetworkError) onNetworkError()
            }
        )

        binding.composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                InvestigationsAll(
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = 2.dp, horizontal = 4.dp), viewModel
                )
            }
        }

        return binding.root
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}

