package com.simenko.qmapp.ui.main.neworder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.simenko.qmapp.ui.theme.QMAppTheme

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QMAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QMAppTheme {
        Greeting("Android")
    }
}