package com.simenko.qmapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.simenko.qmapp.database.asDomainModel
import com.simenko.qmapp.database.asDomainModelTm
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.domain.DomainDepartmentsDetailed
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.network.QualityManagementNetwork
import com.simenko.qmapp.network.asDatabaseModel
import com.simenko.qmapp.network.asDatabaseModelCm
import com.simenko.qmapp.network.asDatabaseModelTm
import com.simenko.qmapp.room.DatabaseDepartmentsDetailed
import com.simenko.qmapp.room.QualityManagementDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QualityManagementRepository(private val database: QualityManagementDB){
    companion object {
        private const val TAG = "Repositories"
    }
    /**
     * Update [departments] from the network
     */
    suspend fun refreshDepartments() {
        withContext(Dispatchers.IO) {
            val departments = QualityManagementNetwork.serviceholder.getDepartments();
            database.qualityManagementDao.insertDepartmentsAll(departments.asDatabaseModel())
        }
    }

    val departments: LiveData<List<DomainDepartment>> = Transformations.map(database.qualityManagementDao.getDepartments()) {
        it.asDomainModel()
    }

    suspend fun refreshTeamMembers() {
        withContext(Dispatchers.IO) {
            val teamMembers = QualityManagementNetwork.serviceholder.getTeamMembers();
            database.qualityManagementDao.insertTeamMembersAll(teamMembers.asDatabaseModelTm())
        }
    }
    suspend fun refreshCompanies() {
        withContext(Dispatchers.IO) {
            val companies = QualityManagementNetwork.serviceholder.getCompanies();
            database.qualityManagementDao.insertCompaniesAll(companies.asDatabaseModelCm())
        }
    }

    val teamMembers: LiveData<List<DomainTeamMember>> = Transformations.map(database.qualityManagementDao.getTeamMembers()) {
        it.asDomainModelTm()
    }

    val departmentsDetailed: LiveData<List<DatabaseDepartmentsDetailed>> = database.qualityManagementDao.getDepartmentsDetailed()

}