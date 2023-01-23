package com.simenko.qmapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.simenko.qmapp.database.asDomainModel
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.network.QualityManagementNetwork
import com.simenko.qmapp.network.asDatabaseModel
import com.simenko.qmapp.room.QualityManagementDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DepartmentsRepository(private val database: QualityManagementDB){
    suspend fun refreshDepartments() {
        withContext(Dispatchers.IO) {
            val departments = QualityManagementNetwork.serviceholder.getDepartments();
            database.departmentDao.insertDepartmentsAll(departments.asDatabaseModel())
        }
    }

    val departments: LiveData<List<DomainDepartment>> = Transformations.map(database.departmentDao.getDepartments()) {
        it.asDomainModel()
    }
}