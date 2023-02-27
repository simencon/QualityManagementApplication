package com.simenko.qmapp.ui.neworder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainInputForOrder
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

enum class NewItemType() {
    NEW_INVESTIGATION,
    NEW_PROCESS_INVESTIGATION,
    NEW_CHARACTERISTIC
}

internal const val KEY_ARG_NEW_ITEM_TYPE = "KEY_ARG_NEW_ITEM_TYPE"

fun launchNewItemActivity(context: Context, orderType: NewItemType) {
    context.startActivity(createNewItemActivityIntent(context, orderType))
}

fun createNewItemActivityIntent(context: Context, orderType: NewItemType): Intent {
    val intent = Intent(context, NewItemActivity::class.java)
    intent.putExtra(KEY_ARG_NEW_ITEM_TYPE, orderType.name)
    return intent
}

class NewItemActivity : ComponentActivity() {

    lateinit var viewModel: NewItemViewModel

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.newItemComponent().create().inject(this)
        viewModel = ViewModelProvider(this, providerFactory)[NewItemViewModel::class.java]

        super.onCreate(savedInstanceState)
        setContent {

            QMAppTheme {
                Scaffold(
                    bottomBar = {}
                ) { padding ->

                    HomeScreen(
                        modifier = Modifier.padding(padding),
                        viewModel = viewModel,
                        parentId = 0
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: NewItemViewModel,
    parentId: Int
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))
        SearchBar(Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(16.dp))
        FavoriteCollectionsGrid(
            modifier = modifier,
            appModel = viewModel,
            parentId = parentId
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    androidx.compose.material.TextField(
        value = "",
        onValueChange = {},
        leadingIcon = {
            androidx.compose.material.Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        ),
        placeholder = {
            androidx.compose.material.Text(stringResource(R.string.placeholder_search))
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

@Composable
fun FavoriteCollectionsGrid(
    modifier: Modifier = Modifier,
    appModel: NewItemViewModel,
    parentId: Int
) {
    val observeInputForOrder by appModel.inputForOrderMediator.observeAsState()
    var litOfInput = arrayListOf<DomainInputForOrder>()

    observeInputForOrder?.apply {
        if (observeInputForOrder!!.first != null) {
            litOfInput.clear()

            observeInputForOrder!!.first!!.filter { it.id > parentId }.forEach { input ->
                if (litOfInput.find { it.recordId == input.recordId } == null) {
                    litOfInput.add(input)
                }
            }
        }
    }

    LazyHorizontalGrid(
//        ToDo for products list 2 rows and 120dp height
        rows = GridCells.Fixed(4),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.height(240.dp)
    ) {
        items(litOfInput.size) { item ->
            FavoriteCollectionCard(litOfInput[item], modifier)
        }
    }
}

@Composable
fun FavoriteCollectionCard(
    inputForOrder: DomainInputForOrder,
    modifier: Modifier = Modifier
) {

    var checked by rememberSaveable { mutableStateOf(false) }

    val btnBackgroundColor = if (checked) Primary900 else StatusBar400
    val btnContentColor = if (checked) Color.White else Color.Black

    val btnColors = ButtonDefaults.buttonColors(
        contentColor = btnContentColor,
        containerColor = btnBackgroundColor
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            colors = btnColors,
            modifier = Modifier
                .width(224.dp)
                .height(56.dp),
            onClick = {
                checked = !checked
            }
        ) {
            Text(
                text = StringUtils.concatTwoStrings3(
                    inputForOrder.itemKey,
                    inputForOrder.itemDesignation
                )
            )
        }
    }

}

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun FavoriteCollectionCardPreview() {
    QMAppTheme {
        /*  FavoriteCollectionCard(
              text = R.string.fc2_nature_meditations,
              drawable = R.drawable.fc2_nature_meditations,
              modifier = Modifier.padding(8.dp)
          )*/
    }
}
