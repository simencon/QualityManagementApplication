package com.simenko.qmapp.ui.main.structure.steps

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.other.Constants.ANIMATION_DURATION
import com.simenko.qmapp.ui.main.structure.CompanyStructureViewModel
import com.simenko.qmapp.utils.dp
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun CompanyStructure(
    mainScreenPadding: PaddingValues,
    viewModel: CompanyStructureViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp.dp - mainScreenPadding.calculateTopPadding()

    val channelVisibility by viewModel.channelVisibility.collectAsStateWithLifecycle()

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    /**
     * TotalScreenWidth, FirstColumnWidth, SecondColumnWidth
     * */
    var screenSizes: Triple<Dp, Dp, Dp> by remember {
        mutableStateOf(Triple(screenWidth.dp, screenWidth.dp, 0.dp))
    }

    val limitToResize = 720

    fun updateSizes(samplesFactor: Int) {
        screenSizes = Triple(
            when {
                screenWidth > limitToResize -> screenWidth.dp
                else -> (screenWidth * (1 + 0.88 * samplesFactor)).dp
            },
            when (samplesFactor) {
                0 -> screenWidth.dp
                else -> {
                    when {
                        screenWidth > limitToResize -> (screenWidth * 0.57).dp
                        else -> screenWidth.dp
                    }
                }
            },
            when {
                screenWidth > limitToResize -> (screenWidth * 0.43 * samplesFactor).dp
                else -> (screenWidth * 0.88 * samplesFactor).dp
            }
        )
    }

    suspend fun animateScroll(samplesFactor: Int) {
        horizontalScrollState.animateScrollTo(
            samplesFactor * horizontalScrollState.maxValue, tween(
                durationMillis = ANIMATION_DURATION,
                easing = LinearOutSlowInEasing
            )
        )
    }

    LaunchedEffect(Unit) { viewModel.mainPageHandler.setupMainPage(0, true) }

    LaunchedEffect(channelVisibility) {
        when (channelVisibility.first == NoRecord) {
            true -> {
                if (screenWidth <= limitToResize) animateScroll(0)
                updateSizes(0)
            }

            false -> {
                updateSizes(1)
//                ToDo find a way to run animation exactly when screen resized
                delay(50L)
                if (screenWidth <= limitToResize) animateScroll(1)
            }
        }
    }

    val context = LocalContext.current
    var direction = 0
    var (x, y) = Offset(0f, 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            /*.pointerInput(horizontalScrollState.value) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        x = dragAmount.x
                        y = dragAmount.y
                        if (abs(x) > abs(y)) {
                            when {
                                x > 0 -> direction = 0
                                x < 0 -> direction = 1
                            }
                        } else {
                            when {
                                y > 0 -> direction = 2
                                y < 0 -> direction = 3
                            }
                        }

                    },
                    onDragEnd = {
                        println("CompanyStructure - detectDragGestures: direction = $direction, x = ${x.dp()}, y = ${y.dp()}")
                        when (direction) {
                            0 -> if (abs(x.dp()) > 50) Toast.makeText(context, "Long right", Toast.LENGTH_LONG).show()
                            1 -> if (abs(x.dp()) > 50) Toast.makeText(context, "Long left", Toast.LENGTH_LONG).show()
                            2 -> if (abs(y.dp()) > 50) Toast.makeText(context, "Long down", Toast.LENGTH_LONG).show()
                            3 -> if (abs(y.dp()) > 50) Toast.makeText(context, "Long up", Toast.LENGTH_LONG).show()
                        }

                    }
                )
            }*/
    )
    {
        Row(
            Modifier
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState, channelVisibility.first != NoRecord)
                .width(screenSizes.first)
                .height(screenHeight)
        ) {
            Departments(modifier = Modifier.width(screenSizes.second), viewModel = viewModel)
//            if (channelVisibility.first != NoRecord)
//                Lines(modifier = Modifier.width(screenSizes.third), invModel = viewModel)
        }
    }
}
