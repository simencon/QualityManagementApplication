package com.simenko.qmapp.data.cache.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simenko.qmapp.data.cache.db.contract.DatabaseBaseModel
import com.simenko.qmapp.domain.ID
import com.simenko.qmapp.domain.entities.DomainUser
import com.simenko.qmapp.domain.entities.DomainUserRole
import com.simenko.qmapp.data.remote.entities.NetworkUser
import com.simenko.qmapp.data.remote.entities.NetworkUserRole
import com.simenko.qmapp.utils.ObjectTransformer

@Entity(
    tableName = "user_roles",
    primaryKeys = [
        "function",
        "roleLevel",
        "accessLevel"
    ]
)
data class DatabaseUserRole(
    val function: String,
    val roleLevel: String,
    val accessLevel: String
) : DatabaseBaseModel<NetworkUserRole, DomainUserRole, String, String> {
    override fun getRecordId(): String = "${this.function}:${this.roleLevel}:${this.accessLevel}"
    override fun toNetworkModel(): NetworkUserRole = ObjectTransformer(DatabaseUserRole::class, NetworkUserRole::class).transform(this)
    override fun toDomainModel(): DomainUserRole = ObjectTransformer(DatabaseUserRole::class, DomainUserRole::class).transform(this)
}

@Entity(tableName = "users")
data class DatabaseUser(
    @PrimaryKey(autoGenerate = false)
    val email: String,
    @ColumnInfo(index = true)
    val teamMemberId: Long,
    val phoneNumber: Long?,
    val fullName: String?,
    val company: String?,
    @ColumnInfo(index = true)
    val companyId: ID,
    val department: String?,
    @ColumnInfo(index = true)
    val departmentId: Long,
    val subDepartment: String?,
    @ColumnInfo(index = true)
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
) : DatabaseBaseModel<NetworkUser, DomainUser, String, ID> {
    override fun getRecordId(): String = this.email
    override fun toNetworkModel(): NetworkUser = ObjectTransformer(DatabaseUser::class, NetworkUser::class).transform(this)
    override fun toDomainModel(): DomainUser = ObjectTransformer(DatabaseUser::class, DomainUser::class).transform(this)
}