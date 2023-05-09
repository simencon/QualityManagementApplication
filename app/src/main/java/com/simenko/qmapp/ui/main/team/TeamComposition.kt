package com.simenko.qmapp.ui.main.team


import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.R
import com.simenko.qmapp.domain.DomainTeamMemberComplete
import com.simenko.qmapp.utils.StringUtils

private const val TAG = "TeamComposition"

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeamComposition(
    modifier: Modifier = Modifier,
    appModel: TeamViewModel
) {
    Log.d(TAG, "TeamMembersLiveData: Parent is build!")

    val context = LocalContext.current

    val observerLoadingProcess by appModel.isLoadingInProgress.observeAsState()
    val observerIsNetworkError by appModel.isNetworkError.observeAsState()

    val items by appModel.teamSF.collectAsState(initial = listOf())

    val onClickDetailsLambda: (Int) -> Unit = {
        appModel.changeCurrentTeamMember(it)
    }
    val onDoubleClickLambda = remember<(DomainTeamMember) -> Unit> {
        {
            appModel.deleteRecord(it)
        }
    }

    val listState = rememberLazyListState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = observerLoadingProcess!!,
        onRefresh = { appModel.syncTeam() }
    )

    Box(
        Modifier
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            state = listState,
            modifier = modifier.padding(vertical = 4.dp)
        ) {
            items(items = items, key = { it.teamMember.id }
            ) { teamMember ->
                TeamMemberCard(
                    teamMember = teamMember,
                    onClickDetails = { onClickDetailsLambda(it) },
                    onDoubleClick = {
                        onDoubleClickLambda(it.teamMember)
                    }
                )
            }
        }

        PullRefreshIndicator(
            observerLoadingProcess!!,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter),
            contentColor = ProgressIndicatorDefaults.circularColor
        )
    }

    if (observerIsNetworkError == true) {
        Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show()
        appModel.onNetworkErrorShown()
    }
}

@Composable
fun TeamMemberCard(
    teamMember: DomainTeamMemberComplete,
    onClickDetails: (Int) -> Unit,
    onDoubleClick: (DomainTeamMemberComplete) -> Unit
) {
    Log.d(TAG, "TeamMemberCard: ${teamMember.teamMember.fullName}")
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleClick(teamMember) }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(0.3f),
        ),

        ) {
        TeamMember(
            id = teamMember.teamMember.id,
            fullName = teamMember.teamMember.fullName,
            email = teamMember.teamMember.email,
            department = teamMember.department.depName ?: "-",
            jobRole = teamMember.teamMember.jobRole,
            detailsVisibility = teamMember.detailsVisibility,
            onClickDetails = {
                onClickDetails(it)
            }
        )
    }
}

private const val columnOneWeight = 0.25f
private const val columnSecondWeight = 0.75f

@Composable
fun TeamMember(
    id: Int,
    fullName: String,
    email: String?,
    department: String,
    jobRole: String,
    detailsVisibility: Boolean,
    onClickDetails: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Log.d(TAG, "TeamMember: $fullName")

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )

            IconButton(onClick = { if (detailsVisibility) onClickDetails(-1) else onClickDetails(id) }) {
                Icon(
                    imageVector = if (detailsVisibility) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (detailsVisibility) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }
                )
            }
        }

        if (detailsVisibility) {

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Email:",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnOneWeight)
                        .padding(start = 8.dp)
                )
                Text(
                    text = StringUtils.getMail(email),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnSecondWeight)
                        .padding(start = 16.dp)
                )
            }

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Підрозділ:",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnOneWeight)
                        .padding(start = 8.dp)
                )
                Text(
                    text = department,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnSecondWeight)
                        .padding(start = 16.dp)
                )
            }

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Роль/посада:",
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnOneWeight)
                        .padding(start = 8.dp, bottom = 16.dp)
                )
                Text(
                    text = jobRole,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnSecondWeight)
                        .padding(start = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun MyAppPreview() {
    QMAppTheme {
    }
}

fun getTeamMembers() = List(30) { i ->

    when (i) {
        0 -> {
            DomainTeamMember(
                id = 0,
                departmentId = 1,
                department = "Quality",
                email = "roman.semenyshyn@skf.com",
                fullName = "Роман Семенишин",
                jobRole = "Заступник начальника УЯк",
                roleLevelId = 5,
                passWord = "13050513",
                companyId = 1
            )
        }
        else -> {
            DomainTeamMember(
                id = i,
                departmentId = i + 1,
                department = "Department num. $i",
                email = "mail_$i@skf.com",
                fullName = "NameSurname_$i",
                jobRole = "Job role $i",
                roleLevelId = (0..5).random(),
                passWord = (1000..9999).random().toString(),
                companyId = 1
            )
        }
    }
}