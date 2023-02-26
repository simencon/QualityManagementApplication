package com.simenko.qmapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.ui.theme.QMAppTheme


@Composable
    fun VItem(text: String) {
        Text(modifier = Modifier.padding(40.dp), text = text)
        Divider(color = Color.Black)
    }

    @Composable
    fun HItem(content: @Composable BoxScope.() -> Unit) {
        Box {
            content()
            Divider(
                color = Color.Red, modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(1.dp)
            )
        }
    }

    @Composable
    fun CreateLazyColumn(pos: String, countItem: Int) {
        LazyColumn {
            items(count = countItem, itemContent = { index ->
                VItem("$pos.$index")
            })
        }
    }

    @Composable
    fun Home() {
        LazyColumn {
            item { VItem("Vertical item 1") }
            item { VItem("Vertical item 2") }
            item { VItem("Vertical item 3") }
            item { VItem("Vertical item 4") }
            item {
                LazyRow(modifier = Modifier.height(150.dp)) {
                    item {
                        HItem {
                            CreateLazyColumn("Horizontal item 5.1", 6)
                        }
                    }
                    item {
                        HItem {
                            CreateLazyColumn("Horizontal item 5.2", 10)
                        }
                    }
                    item {
                        HItem {
                            Text(
                                modifier = Modifier.padding(40.dp),
                                text = "Horizontal item 5.3"
                            )
                        }
                    }
                    item {
                        HItem {
                            Text(
                                modifier = Modifier.padding(40.dp),
                                text = "Horizontal item 5.4"
                            )
                        }
                    }
                    item {
                        HItem {
                            CreateLazyColumn("Horizontal 5.5", 6)
                        }
                    }
                    item {
                        HItem {
                            Text(
                                modifier = Modifier.padding(40.dp),
                                text = "Horizontal item 5.6"
                            )
                        }
                    }
                    item {
                        HItem {
                            Text(
                                modifier = Modifier.padding(40.dp),
                                text = "Horizontal item 5.7"
                            )
                        }
                    }
                    item {
                        HItem {
                            Text(
                                modifier = Modifier.padding(40.dp),
                                text = "Horizontal item 5.8"
                            )
                        }
                    }
                }
                Divider(color = Color.Black)
            }
            item { VItem("Vertical item 6") }
            item { VItem("Vertical item 7") }
            item { VItem("Vertical item 8") }
            item { VItem("Vertical item 9") }
            item { VItem("Vertical item 10") }
            item { VItem("Vertical item 11") }
            item { VItem("Vertical item 12") }
        }
    }

@Preview(name = "Light Mode Order", showBackground = true, widthDp = 409)
@Composable
fun MyOrderPreview() {
    QMAppTheme {
        Home()
    }
}

