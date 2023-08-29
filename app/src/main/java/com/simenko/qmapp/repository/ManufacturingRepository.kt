package com.simenko.qmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.simenko.qmapp.domain.entities.*
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.other.Resource
import com.simenko.qmapp.repository.contract.CrudeOperations
import com.simenko.qmapp.retrofit.implementation.ManufacturingService
import com.simenko.qmapp.room.implementation.QualityManagementDB
import com.simenko.qmapp.room.implementation.dao.ManufacturingDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ManufacturingRepository"
@Singleton
class ManufacturingRepository @Inject constructor(
    private val database: QualityManagementDB,
    private val manufacturingDao: ManufacturingDao,
    private val manufacturingService: ManufacturingService,
    private val userRepository: UserRepository,
    private val crudeOperations: CrudeOperations
) {
    /**
     * Update Manufacturing from the network
     */
    suspend fun refreshPositionLevels() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = manufacturingService.getPositionLevels()
            manufacturingDao.insertPositionLevelsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshPositionLevels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun syncTeamMembers() = crudeOperations.syncRecordsAll(
        database.teamMemberDao
    ) { manufacturingService.getTeamMembers() }

    fun CoroutineScope.deleteTeamMember(orderId: Int): ReceiveChannel<Event<Resource<DomainTeamMember>>> = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { manufacturingService.deleteTeamMember(orderId) }
        ) { r -> database.teamMemberDao.deleteRecord(r) }
    }
    fun CoroutineScope.insertTeamMember(record: DomainTeamMember) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { manufacturingService.insertTeamMember(record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.teamMemberDao.insertRecord(r) }
    }
    fun CoroutineScope.updateTeamMember(record: DomainTeamMember) = crudeOperations.run {
        responseHandlerForSingleRecord(
            taskExecutor = { manufacturingService.editTeamMember(record.id, record.toDatabaseModel().toNetworkModel()) }
        ) { r -> database.teamMemberDao.updateRecord(r) }
    }

    suspend fun refreshCompanies() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = manufacturingService.getCompanies()
            manufacturingDao.insertCompaniesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshCompanies: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun syncDepartments() = crudeOperations.syncRecordsAll(
        database.departmentDao
    ) { manufacturingService.getDepartments() }

    suspend fun refreshSubDepartments() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = manufacturingService.getSubDepartments()
            manufacturingDao.insertSubDepartmentsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshSubDepartments: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshManufacturingChannels() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = manufacturingService.getManufacturingChannels()
            manufacturingDao.insertManufacturingChannelsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshManufacturingChannels: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshManufacturingLines() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = manufacturingService.getManufacturingLines()
            manufacturingDao.insertManufacturingLinesAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshManufacturingLines: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshManufacturingOperations() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
            val list = manufacturingService.getManufacturingOperations()
            manufacturingDao.insertManufacturingOperationsAll(
                list.map { it.toDatabaseModel() }
            )
            Log.d(TAG, "refreshManufacturingOperations: ${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}")
        }
    }

    suspend fun refreshOperationsFlows() {
        withContext(Dispatchers.IO) {
            userRepository.refreshTokenIfNecessary()
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
    val getTeamMembers: Flow<List<DomainTeamMember>> =
        database.teamMemberDao.getRecordsFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }

    fun teamCompleteList(): Flow<List<DomainTeamMemberComplete>> =
        database.teamMemberDao.getRecordsCompleteFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }


    val getDepartments: Flow<List<DomainDepartment>> =
        database.departmentDao.getRecordsFlowForUI().map {list ->
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
        database.departmentDao.getRecordsDetailedFlowForUI().map { list ->
            list.map { it.toDomainModel() }
        }
}