package com.simenko.qmapp.domain.entities

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.room.entities.DatabaseUser
import com.simenko.qmapp.room.entities.DatabaseUserRole
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainUserRole(
    val function: String,
    val roleLevel: String,
    val accessLevel: String
) : DomainBaseModel<DatabaseUserRole>() {
    override fun getRecordId(): Any = "${this.function}:${this.roleLevel}:${this.accessLevel}"
    override fun getParentId(): Int = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseUserRole = ObjectTransformer(DomainUserRole::class, DatabaseUserRole::class).transform(this)
}

data class DomainUser(
    var email: String,
    var teamMemberId: Long,
    var phoneNumber: Long?,
    var fullName: String?,
    var company: String?,
    var companyId: Long,
    var department: String?,
    var departmentId: Long,
    var subDepartment: String?,
    var subDepartmentId: Long,
    var jobRole: String?,
    var restApiUrl: String?,
    var userUid: String?,
    var isEmailVerified: Boolean,
    var roles: Set<String>?,
    var accountNonExpired: Boolean,
    var accountNonLocked: Boolean,
    var credentialsNonExpired: Boolean,
    var enabled: Boolean,

    var isSelected: Boolean = false,
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseUser>() {
    override fun getRecordId(): Any = this.email
    override fun getParentId(): Int = this.companyId.toInt()
    override fun setIsSelected(value: Boolean) {
        this.isSelected = value
    }
    override fun toDatabaseModel(): DatabaseUser = ObjectTransformer(DomainUser::class, DatabaseUser::class).transform(this)
}