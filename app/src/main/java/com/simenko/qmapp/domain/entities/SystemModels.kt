package com.simenko.qmapp.domain.entities

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.room.entities.DatabaseUser
import com.simenko.qmapp.room.entities.DatabaseUserRole
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainUserRole(
    val function: String = NoString.str,
    val roleLevel: String = NoString.str,
    val accessLevel: String = NoString.str,

    var detailsVisibility: Boolean = false
) : DomainBaseModel<DatabaseUserRole>() {
    override fun getRecordId(): Any = "${this.function}:${this.roleLevel}:${this.accessLevel}"
    override fun getParentId(): Int = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel(): DatabaseUserRole = ObjectTransformer(DomainUserRole::class, DatabaseUserRole::class).transform(this)
}

data class DomainUser(
    var email: String = EmptyString.str,
    var teamMemberId: Long = NoRecord.num.toLong(),
    var phoneNumber: Long? = null,
    var fullName: String? = null,
    var company: String? = null,
    var companyId: Long = NoRecord.num.toLong(),
    var department: String? = null,
    var departmentId: Long = NoRecord.num.toLong(),
    var subDepartment: String? = null,
    var subDepartmentId: Long = NoRecord.num.toLong(),
    var jobRole: String? = null,
    var restApiUrl: String? = null,
    var userUid: String? = null,
    var isEmailVerified: Boolean = false,
    var roles: Set<String>? = null,
    var accountNonExpired: Boolean = false,
    var accountNonLocked: Boolean = false,
    var credentialsNonExpired: Boolean = false,
    var enabled: Boolean = false,

    var isSelected: Boolean = false,
    var detailsVisibility: Boolean = false,
    var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseUser>() {
    override fun getRecordId(): Any = this.email
    override fun getParentId(): Int = this.companyId.toInt()

    override fun getName() = this.fullName ?: "Has no name"

    override fun setIsSelected(value: Boolean) {
        this.isSelected = value
    }

    override fun toDatabaseModel(): DatabaseUser = ObjectTransformer(DomainUser::class, DatabaseUser::class).transform(this)
}