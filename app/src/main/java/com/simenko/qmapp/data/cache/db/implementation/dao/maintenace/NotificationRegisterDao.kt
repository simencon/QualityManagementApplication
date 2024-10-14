package com.simenko.qmapp.data.cache.db.implementation.dao.maintenace

import androidx.room.Dao
import com.simenko.qmapp.data.cache.db.contract.DaoBaseModel
import com.simenko.qmapp.data.cache.db.entities.NotificationRegisterEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NotificationRegisterDao : DaoBaseModel<Int, Any, NotificationRegisterEntity> {
    override fun getRecords(): List<NotificationRegisterEntity> {
        TODO("Not yet implemented")
    }

    override fun getRecordsForUI(): Flow<List<NotificationRegisterEntity>> {
        TODO("Not yet implemented")
    }
    fun getRecordsByParentId(parentId: Any): List<NotificationRegisterEntity> {
        TODO("Not yet implemented")
    }

    fun getRecordById(id: Int): NotificationRegisterEntity? {
        TODO("Not yet implemented")
    }
}