package com.simenko.qmapp.domain.usecase

import com.simenko.qmapp.data.cache.db.implementation.QualityManagementDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClearDbUseCase @Inject constructor(private val database: QualityManagementDB) {
    suspend fun execute() {
        coroutineScope {
            launch(Dispatchers.IO) {
                database.clearAllTables()
                database.close()
            }
        }
    }
}