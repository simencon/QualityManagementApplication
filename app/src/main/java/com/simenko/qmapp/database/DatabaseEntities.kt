package com.simenko.qmapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simenko.qmapp.domain.DomainDepartment

@Entity(tableName = "10_departments")
data class DatabaseDepartment constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val depAbbr: String?,
    val depName: String?,
//    @ForeignKey //Also possible to use here
    val depManager: Int?,
    val depOrganization: String?,
//    @ForeignKey //Also possible to use here
    val depOrder: Int?,
    val companyId: Int?
)

fun List<DatabaseDepartment>.asDomainModel(): List<DomainDepartment> {
    return map {
        DomainDepartment(
            id = it.id,
            depAbbr = it.depAbbr,
            depName = it.depName,
            depManager = it.depManager,
            depOrganization = it.depOrganization,
            depOrder = it.depOrder,
            companyId = it.companyId
        )
    }
}