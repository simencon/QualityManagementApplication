package com.simenko.qmapp.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.simenko.qmapp.domain.DomainBaseModel
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.retrofit.NetworkBaseModel
import com.simenko.qmapp.room.contract.DatabaseBaseModel

@Entity
data class NotificationRegisterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = NoRecord.num.toInt(),
    val email: String
) : DatabaseBaseModel<NotificationRegisterResponseStub, NotificationRegisterDomainModelStub> {
    override fun getRecordId() = id
    override fun toNetworkModel() = NotificationRegisterResponseStub(email)
    override fun toDomainModel() = NotificationRegisterDomainModelStub(email)
}

data class NotificationRegisterDomainModelStub(
    val email: String
) : DomainBaseModel<NotificationRegisterEntity>() {
    override fun getRecordId() = email
    override fun getParentId() = NoRecord.num
    override fun setIsSelected(value: Boolean) {}
    override fun toDatabaseModel() = NotificationRegisterEntity(NoRecord.num.toInt(), email)
}

data class NotificationRegisterResponseStub(
    val email: String
): NetworkBaseModel<NotificationRegisterEntity> {
    override fun getRecordId() = email
    override fun toDatabaseModel() = NotificationRegisterEntity(NoRecord.num.toInt(), email)
}