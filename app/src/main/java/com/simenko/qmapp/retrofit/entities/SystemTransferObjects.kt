package com.simenko.qmapp.retrofit.entities

import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.entities.DatabaseUser
import com.simenko.qmapp.room.entities.DatabaseUserRole
import com.simenko.qmapp.utils.ObjectTransformer
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkUserRole(
    val function: String,
    val roleLevel: String,
    val accessLevel: String
) : NetworkBaseModel<DatabaseUserRole> {
    override fun getRecordId(): Any = "${this.function}:${this.roleLevel}:${this.accessLevel}"
    override fun toDatabaseModel(): DatabaseUserRole = ObjectTransformer(NetworkUserRole::class, DatabaseUserRole::class).transform(this)
}

@JsonClass(generateAdapter = true)
data class NetworkUser(
    val email: String,
    val teamMemberId: Long,
    val phoneNumber: Long?,
    val fullName: String?,
    val company: String?,
    val companyId: Long,
    val department: String?,
    val departmentId: Long,
    val subDepartment: String?,
    val subDepartmentId: Long,
    val jobRole: String?,
    val restApiUrl: String?,
    val userUid: String?,
    val isEmailVerified: Boolean,
    val roles: Set<String>?,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean,
    val credentialsNonExpired: Boolean,
    val enabled: Boolean
) : NetworkBaseModel<DatabaseUser> {
    override fun getRecordId(): Any = this.email
    override fun toDatabaseModel(): DatabaseUser = ObjectTransformer(NetworkUser::class, DatabaseUser::class).transform(this)
}

data class NetworkErrorBody(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val exception: String?
)