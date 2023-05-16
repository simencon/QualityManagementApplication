package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.ManufacturingService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.ManufacturingDao
import com.simenko.qmapp.utils.ListTransformer
import com.simenko.qmapp.utils.ObjectTransformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.reflect.KClass

private const val TAG = "ManufacturingRepository"

@OptIn(ExperimentalCoroutinesApi::class)
class ManufacturingRepository @Inject constructor(
    private val manufacturingDao: ManufacturingDao,
    public val manufacturingService: ManufacturingService
) {
    /**
     * Update Manufacturing from the network
     */
    suspend fun refreshPositionLevels() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getPositionLevels()
            manufacturingDao.insertPositionLevelsAll(
                ListTransformer(
                    list,
                    NetworkPositionLevel::class,
                    DatabasePositionLevel::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshPositionLevels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshTeamMembers() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getTeamMembers()
            manufacturingDao.insertTeamMembersAll(
                ListTransformer(
                    list,
                    NetworkTeamMember::class,
                    DatabaseTeamMember::class
                ).generateList()
            )
            Log.d(TAG, "refreshTeamMembers: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun insertRecord(coroutineScope: CoroutineScope, record: DomainTeamMember) =
        coroutineScope.produce {
            val response = manufacturingService.insertTeamMember(
                record.toNetworkWithoutId()
            )

            if (response.isSuccessful) {
                response.body()?.let { manufacturingDao.insertTeamMember(it.toDatabase()) }
                response.body()?.toDatabase()?.let { send(it.toDomainTeamMember()) }
            } else {
                Log.d(TAG, "insertRecord: ${response.errorBody()}")
            }
        }


    suspend fun deleteRecord(coroutineScope: CoroutineScope, record: DomainTeamMember) =
        coroutineScope.produce {
            val response = manufacturingService.deleteTeamMember(record.id)
            if (response.isSuccessful) {
                manufacturingDao.deleteTeamMember(record.toDatabase())
            }
            send(response)
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainTeamMember) =
        coroutineScope.produce {
            val response = manufacturingService
                .updateTeamMember(record.id, record.toNetworkWithId()).body()
                ?.toDatabase()

            response?.let { manufacturingDao.updateTeamMember(it) }
            response?.let { send(it.toDomainTeamMember()) }
        }

    suspend fun refreshCompanies() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getCompanies()
            manufacturingDao.insertCompaniesAll(
                ListTransformer(
                    list,
                    NetworkCompany::class,
                    DatabaseCompany::class
                ).generateList()
            )
            Log.d(TAG, "refreshCompanies: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshDepartments() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getDepartments()
            manufacturingDao.insertDepartmentsAll(
                ListTransformer(
                    list,
                    NetworkDepartment::class,
                    DatabaseDepartment::class
                ).generateList()
            )
            Log.d(TAG, "refreshDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubDepartments() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getSubDepartments()
            manufacturingDao.insertSubDepartmentsAll(
                ListTransformer(
                    list,
                    NetworkSubDepartment::class,
                    DatabaseSubDepartment::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshSubDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshManufacturingChannels() {
        withContext(Dispatchers.IO) {
            val list =
                manufacturingService.getManufacturingChannels()
            manufacturingDao.insertManufacturingChannelsAll(
                ListTransformer(
                    list,
                    NetworkManufacturingChannel::class,
                    DatabaseManufacturingChannel::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshManufacturingChannels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshManufacturingLines() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getManufacturingLines()
            manufacturingDao.insertManufacturingLinesAll(
                ListTransformer(
                    list,
                    NetworkManufacturingLine::class,
                    DatabaseManufacturingLine::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshManufacturingLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshManufacturingOperations() {
        withContext(Dispatchers.IO) {
            val list =
                manufacturingService.getManufacturingOperations()
            manufacturingDao.insertManufacturingOperationsAll(
                ListTransformer(
                    list,
                    NetworkManufacturingOperation::class,
                    DatabaseManufacturingOperation::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshManufacturingOperations: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    suspend fun refreshOperationsFlows() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getOperationsFlows()
            manufacturingDao.insertOperationsFlowsAll(
                ListTransformer(
                    list,
                    NetworkOperationsFlow::class,
                    DatabaseOperationsFlow::class
                ).generateList()
            )
            Log.d(
                TAG,
                "refreshOperationsFlows: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}"
            )
        }
    }

    /**
     * Connecting with LiveData for ViewModel
     */
    val teamMembers: LiveData<List<DomainTeamMember>> =
        manufacturingDao.getTeamMembers().map {
            ListTransformer(
                it,
                DatabaseTeamMember::class,
                DomainTeamMember::class
            ).generateList()
        }

    suspend fun teamCompleteList(): List<DomainTeamMemberComplete> =
        manufacturingDao.getTeamDetailedList().asTeamCompleteDomainModel()

    suspend fun teamCompleteByDepartment(depId: Int): List<DomainTeamMemberComplete> =
        manufacturingDao.getTeamDetailedList()
            .asTeamCompleteDomainModel()
            .filter { itd -> itd.teamMember.departmentId == depId }

    val departments: LiveData<List<DomainDepartment>> =
        manufacturingDao.getDepartments().map {
            ListTransformer(
                it,
                DatabaseDepartment::class,
                DomainDepartment::class
            ).generateList()
        }

    val subDepartments: LiveData<List<DomainSubDepartment>> =
        manufacturingDao.getSubDepartments().map {
            ListTransformer(
                it,
                DatabaseSubDepartment::class,
                DomainSubDepartment::class
            ).generateList()
        }

    val channels: LiveData<List<DomainManufacturingChannel>> =
        manufacturingDao.getManufacturingChannels().map {
            ListTransformer(
                it,
                DatabaseManufacturingChannel::class,
                DomainManufacturingChannel::class
            ).generateList()
        }

    val lines: LiveData<List<DomainManufacturingLine>> =
        manufacturingDao.getManufacturingLines().map {
            ListTransformer(
                it,
                DatabaseManufacturingLine::class,
                DomainManufacturingLine::class
            ).generateList()
        }

    val operations: LiveData<List<DomainManufacturingOperation>> =
        manufacturingDao.getManufacturingOperations().map {
            ListTransformer(
                it,
                DatabaseManufacturingOperation::class,
                DomainManufacturingOperation::class
            ).generateList()
        }

    val operationsFlows: LiveData<List<DomainOperationsFlow>> =
        manufacturingDao.getOperationsFlows().map {
            ListTransformer(
                it,
                DatabaseOperationsFlow::class,
                DomainOperationsFlow::class
            ).generateList()
        }

    val departmentsDetailed: LiveData<List<DomainDepartmentComplete>> =
        manufacturingDao.getDepartmentsDetailed().map {
            it.asDepartmentsDetailedDomainModel()
        }
}