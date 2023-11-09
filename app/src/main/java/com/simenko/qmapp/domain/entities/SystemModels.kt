package com.simenko.qmapp.domain.entities

import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoRecordStr
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.room.entities.DatabaseUser
import com.simenko.qmapp.room.entities.DatabaseUserRole
import com.simenko.qmapp.utils.ObjectTransformer

data class DomainUserRole(
    val function: String = NoString.str,
    val roleLevel: String = NoString.str,
    val accessLevel: String = NoString.str,
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseUserRole>() {
    override fun getRecordId() = "${this.function}:${this.roleLevel}:${this.accessLevel}"
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = ObjectTransformer(DomainUserRole::class, DatabaseUserRole::class).transform(this)
}

data class DomainUser(
    var email: String = NoRecordStr.str,
    var teamMemberId: ID = NoRecord.num,
    var phoneNumber: Long? = null,
    var fullName: String? = null,
    var company: String? = null,
    var companyId: ID = NoRecord.num,
    var department: String? = null,
    var departmentId: ID = NoRecord.num,
    var subDepartment: String? = null,
    var subDepartmentId: ID = NoRecord.num,
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
    override var detailsVisibility: Boolean = false,
    override var isExpanded: Boolean = false
) : DomainBaseModel<DatabaseUser>() {
    fun rolesAsUserRoles(): List<DomainUserRole> {
        val list = mutableListOf<DomainUserRole>()
        roles?.let { set ->
            set.forEach { roleStr ->
                println("DomainUser - $roleStr")
                println("DomainUser - ${roleStr.split(":")}")
                println("DomainUser - ${roleStr.split(":").size}")
                roleStr.split(":").let { if (it.size == 3) list.add(DomainUserRole(it[0], it[1], it[2])) }
            }
        }
        return list.toList()
    }

    override fun getRecordId() = this.email
    override fun getParentId() = this.companyId
    override fun getName() = this.fullName ?: "Has no name"

    override fun setIsSelected(value: Boolean) {
        this.isSelected = value
    }

    override fun toDatabaseModel(): DatabaseUser = ObjectTransformer(DomainUser::class, DatabaseUser::class).transform(this)
}