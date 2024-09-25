package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.DatabaseUser
import com.simenko.qmapp.room.entities.DatabaseUserRole
import com.simenko.qmapp.utils.ObjectTransformer
import kotlinx.serialization.Serializable

@Serializable
data class NetworkUserRole(
    val function: String,
    val roleLevel: String,
    val accessLevel: String
) : NetworkBaseModel<DatabaseUserRole> {
    override fun getRecordId(): Any = "${this.function}:${this.roleLevel}:${this.accessLevel}"
    override fun toDatabaseModel(): DatabaseUserRole = ObjectTransformer(NetworkUserRole::class, DatabaseUserRole::class).transform(this)
}

@Serializable
data class NetworkUser(
    val email: String,
    val teamMemberId: Long,
    val phoneNumber: Long? = null,
    val fullName: String? = null,
    val company: String? = null,
    val companyId: Long,
    val department: String? = null,
    val departmentId: Long,
    val subDepartment: String? = null,
    val subDepartmentId: Long,
    val jobRole: String? = null,
    val restApiUrl: String? = null,
    val userUid: String? = null,
    val isEmailVerified: Boolean,
    val roles: Set<String>? = null,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean,
    val enabled: Boolean
) : NetworkBaseModel<DatabaseUser> {
    override fun getRecordId(): Any = this.email
    override fun toDatabaseModel(): DatabaseUser = ObjectTransformer(NetworkUser::class, DatabaseUser::class).transform(this)
}

@Serializable
data class NetworkErrorBody(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val exception: String? = null
)