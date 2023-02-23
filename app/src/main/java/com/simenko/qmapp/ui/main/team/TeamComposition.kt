package com.simenko.qmapp.ui.main.team

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.ui.main.team.ui.theme.QMAppTheme
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.utils.StringUtils

//import androidx.compose.runtime.R

private const val TAG = "TeamComposition"

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


@Composable
fun TeamMembersLiveData(
    modifier: Modifier = Modifier,
    appModel: QualityManagementViewModel
) {
    val observeTeam by appModel.teamMembers.observeAsState(initial = null)

    if (observeTeam != null) {
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
            items(items = observeTeam!!) { teamMember ->
                var result = teamMember
                TeamMemberCard(
                    onClickDetails = { it ->
                        result = appModel.changeTeamMembersDetailsVisibility(it)
                    },
                    teamMember = result
                )
            }
        }
    }
}

@Composable
fun TeamMemberCard(teamMember: DomainTeamMember, onClickDetails: (DomainTeamMember) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        TeamMember(
            fullName = teamMember.fullName,
            email = teamMember.email,
            department = teamMember.department,
            jobRole = teamMember.jobRole,
            detailsVisibility = teamMember.detailsVisibility,
            onClickDetails = { onClickDetails(teamMember) }
        )
    }
}

private const val columnOneWeight = 0.25f
private const val columnSecondWeight = 0.75f

@Composable
fun TeamMember(
    fullName: String,
    email: String?,
    department: String,
    jobRole: String,
    detailsVisibility: Boolean,
    onClickDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {

        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            /*Text(
                text = "Ім'я:",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(columnOneWeight)
                    .padding(start = 8.dp)
            )*/
            Text(
                text = fullName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )

            IconButton(onClick = onClickDetails) {
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

                    text = StringUtils.getMail(email ?: ""),
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
                        .padding(start = 8.dp)
                )
                Text(
                    text = jobRole,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(columnSecondWeight)
                        .padding(start = 16.dp)
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