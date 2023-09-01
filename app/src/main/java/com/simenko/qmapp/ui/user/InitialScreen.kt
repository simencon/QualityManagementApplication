package com.simenko.qmapp.ui.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.theme.QMAppTheme

@Composable
fun InitialScreen(logo: Painter = painterResource(id = R.drawable.ic_launcher_round)) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = logo,
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(name = "Lite Mode InitialScreen", showBackground = true, widthDp = 360)
@Composable
fun InitialScreenPreview() {
    QMAppTheme {
        InitialScreen()
    }
}