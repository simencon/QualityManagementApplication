package com.simenko.qmapp.ui.main.products.kinds.list.forms.product.new_product

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.ui.common.RecordFieldItem
import java.util.Locale

@Composable
fun ProductBaseDialog(
    productBaseName: String,
    errorMsg: String,
    onChangeProductBaseName: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            val (descriptionFR) = FocusRequester.createRefs()
            val keyboardController = LocalSoftwareKeyboardController.current
            Column(
                Modifier
                    .wrapContentSize()
                    .background(MaterialTheme.colorScheme.onPrimary),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ADD PRODUCT BASE".uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 18.sp),
                    color = Color.Unspecified,
                    modifier = Modifier
                        .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Start)
                )
                RecordFieldItem(
                    modifier = Modifier.width(300.dp),
                    valueParam = Triple(productBaseName, errorMsg.isNotEmpty()) { onChangeProductBaseName(it) },
                    keyboardNavigation = Pair(descriptionFR) { keyboardController?.hide() },
                    keyBoardTypeAction = Pair(KeyboardType.Text, ImeAction.Done),
                    contentDescription = Triple(Icons.Outlined.Info, "Product base name", "Enter name"),
                    isMandatoryField = true,
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height((Constants.DEFAULT_SPACE).dp))
                if (errorMsg.isNotEmpty())
                    Text(
                        modifier = Modifier
                            .padding(all = 5.dp)
                            .width(290.dp),
                        text = errorMsg,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                        textAlign = TextAlign.Center
                    )
                Row(
                    Modifier
                        .height(IntrinsicSize.Min)
                        .width(300.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    ) {
                        Text(
                            "Cancel",
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    TextButton(
                        enabled = productBaseName.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        onClick = { onAddClick() },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    ) {
                        Text(
                            "Add",
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ProductBaseDialogPreview() {
    ProductBaseDialog(
        productBaseName = EmptyString.str,
        errorMsg = "Such product base already exists",
        onChangeProductBaseName = {},
        onDismiss = {},
        onAddClick = {}
    )
}