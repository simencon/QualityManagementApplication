package com.simenko.qmapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.simenko.qmapp.domain.*
import com.simenko.qmapp.room_entities.*
import com.simenko.qmapp.retrofit_entities.*
import com.simenko.qmapp.room_implementation.QualityManagementDB
import com.simenko.qmapp.utils.ListTransformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QualityManagementRepository(private val database: QualityManagementDB){
    /**
     * Update [departments] from the network
     */
    suspend fun refreshDepartments() {
        withContext(Dispatchers.IO) {
            val departments = QualityManagementNetwork.serviceholder.getDepartments();
            database.qualityManagementDao.insertDepartmentsAll(
                ListTransformer(departments,NetworkDepartment::class,DatabaseDepartment::class).generateList()
            )
        }
    }

    suspend fun refreshTeamMembers() {
        withContext(Dispatchers.IO) {
            val teamMembers = QualityManagementNetwork.serviceholder.getTeamMembers();
            database.qualityManagementDao.insertTeamMembersAll(
                ListTransformer(teamMembers,NetworkTeamMembers::class,DatabaseTeamMember::class).generateList()
            )
        }
    }
    suspend fun refreshCompanies() {
        withContext(Dispatchers.IO) {
            val companies = QualityManagementNetwork.serviceholder.getCompanies();
            database.qualityManagementDao.insertCompaniesAll(
                ListTransformer(companies,NetworkCompanies::class,DatabaseCompanies::class).generateList()
            )
        }
    }

    suspend fun refreshInputForOrder() {
        withContext(Dispatchers.IO) {
            val inputForOrder = QualityManagementNetwork.serviceholder.getInputForOrder();
            database.qualityManagementDao.insertInputForOrderAll(
                ListTransformer(inputForOrder,NetworkInputForOrder::class,DatabaseInputForOrder::class).generateList()
            )
        }
    }

    val teamMembers: LiveData<List<DomainTeamMember>> = Transformations.map(database.qualityManagementDao.getTeamMembers()) {
        ListTransformer(it,DatabaseTeamMember::class,DomainTeamMember::class).generateList()
    }

    val departments: LiveData<List<DomainDepartment>> = Transformations.map(database.qualityManagementDao.getDepartments()) {
        ListTransformer(it,DatabaseDepartment::class,DomainDepartment::class).generateList()
    }

    val departmentsDetailed: LiveData<List<DomainDepartmentsDetailed>> = Transformations.map(database.qualityManagementDao.getDepartmentsDetailed()) {
        it.asDepartmentsDetailedDomainModel()
    }

    val inputForOrder: LiveData<List<DomainInputForOrder>> = Transformations.map(database.qualityManagementDao.getInputForOrder()) {
        ListTransformer(it,DatabaseInputForOrder::class,DomainInputForOrder::class).generateList()
    }

}