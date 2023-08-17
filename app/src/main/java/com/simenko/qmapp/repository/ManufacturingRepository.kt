package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.retrofit.entities.*
import com.simenko.qmapp.retrofit.implementation.ManufacturingService
import com.simenko.qmapp.room.entities.*
import com.simenko.qmapp.room.implementation.dao.ManufacturingDao
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
import javax.inject.Singleton

private const val TAG = "ManufacturingRepository"

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class ManufacturingRepository @Inject constructor(
    private val manufacturingDao: ManufacturingDao,
    private val manufacturingService: ManufacturingService
) {
    /**
     * Update Manufacturing from the network
     */
    suspend fun refreshPositionLevels() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getPositionLevels()
            manufacturingDao.insertPositionLevelsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshPositionLevels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshTeamMembers() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getTeamMembers()
            manufacturingDao.insertTeamMembersAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshTeamMembers: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun insertRecord(coroutineScope: CoroutineScope, record: DomainTeamMember) =
        coroutineScope.produce {
            val response = manufacturingService.insertTeamMember(
                record.toDatabaseModel().toNetworkModel()
            )

            if (response.isSuccessful) {
                response.body()?.let { manufacturingDao.insertTeamMember(it.toDatabaseModel()) }
                response.body()?.toDatabaseModel()?.let { send(it.toDomainModel()) }
            } else {
                Log.d(TAG, "insertRecord: ${response.errorBody()}")
            }
        }


    suspend fun deleteRecord(coroutineScope: CoroutineScope, record: DomainTeamMember) =
        coroutineScope.produce {
            val response = manufacturingService.deleteTeamMember(record.id)
            if (response.isSuccessful) {
                manufacturingDao.deleteTeamMember(record.toDatabaseModel())
            }
            send(response)
        }

    fun updateRecord(coroutineScope: CoroutineScope, record: DomainTeamMember) =
        coroutineScope.produce {
            val response = manufacturingService
                .updateTeamMember(record.id, record.toDatabaseModel().toNetworkModel()).body()
                ?.toDatabaseModel()

            response?.let { manufacturingDao.updateTeamMember(it) }
            response?.let { send(it.toDomainModel()) }
        }

    suspend fun refreshCompanies() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getCompanies()
            manufacturingDao.insertCompaniesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshCompanies: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshDepartments() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getDepartments()
            manufacturingDao.insertDepartmentsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshSubDepartments() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getSubDepartments()
            manufacturingDao.insertSubDepartmentsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshSubDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshManufacturingChannels() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getManufacturingChannels()
            manufacturingDao.insertManufacturingChannelsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshManufacturingChannels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshManufacturingLines() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getManufacturingLines()
            manufacturingDao.insertManufacturingLinesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshManufacturingLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshManufacturingOperations() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getManufacturingOperations()
            manufacturingDao.insertManufacturingOperationsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshManufacturingOperations: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshOperationsFlows() {
        withContext(Dispatchers.IO) {
            val list = manufacturingService.getOperationsFlows()
            manufacturingDao.insertOperationsFlowsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshOperationsFlows: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    /**
     * Connecting with LiveData for ViewModel
     */
    val teamMembers: LiveData<List<DomainTeamMember>> =
        manufacturingDao.getTeamMembers().map { list ->
            list.map { it.toDomainModel() }
        }

    fun teamCompleteList(): Flow<List<DomainTeamMemberComplete>> =
        manufacturingDao.getTeamDetailedList().map { list ->
            list.map { it.toDomainModel() }
        }


    val departments: LiveData<List<DomainDepartment>> =
        manufacturingDao.getDepartments().map {list ->
            list.map { it.toDomainModel() }
        }

    val subDepartments: LiveData<List<DomainSubDepartment>> =
        manufacturingDao.getSubDepartments().map {list ->
            list.map { it.toDomainModel() }
        }

    val channels: LiveData<List<DomainManufacturingChannel>> =
        manufacturingDao.getManufacturingChannels().map {list ->
            list.map { it.toDomainModel() }
        }

    val lines: LiveData<List<DomainManufacturingLine>> =
        manufacturingDao.getManufacturingLines().map {list ->
            list.map { it.toDomainModel() }
        }

    val operations: LiveData<List<DomainManufacturingOperation>> =
        manufacturingDao.getManufacturingOperations().map {list ->
            list.map { it.toDomainModel() }
        }

    val operationsFlows: LiveData<List<DomainOperationsFlow>> =
        manufacturingDao.getOperationsFlows().map {list ->
            list.map { it.toDomainModel() }
        }

    val departmentsDetailed: LiveData<List<DomainDepartmentComplete>> =
        manufacturingDao.getDepartmentsDetailed().map { list ->
            list.map { it.toDomainModel() }
        }
}