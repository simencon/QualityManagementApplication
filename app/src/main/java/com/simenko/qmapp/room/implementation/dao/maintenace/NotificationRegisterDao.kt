package com.simenko.qmapp.room.implementation.dao.maintenace

import androidx.room.Dao
import com.simenko.qmapp.room.contract.DaoBaseModel
import com.simenko.qmapp.room.entities.NotificationRegisterEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NotificationRegisterDao : DaoBaseModel<Int, Any, NotificationRegisterEntity> {
    override fun getRecords(): List<NotificationRegisterEntity> {
        TODO("Not yet implemented")
    }

    override fun getRecordsByParentId(parentId: Any): List<NotificationRegisterEntity> {
        TODO("Not yet implemented")
    }

    override fun getRecordById(id: Int): NotificationRegisterEntity? {
        TODO("Not yet implemented")
    }

    override fun getRecordsForUI(): Flow<List<NotificationRegisterEntity>> {
        TODO("Not yet implemented")
    }
}