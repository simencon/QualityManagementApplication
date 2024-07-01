package com.simenko.qmapp.room.implementation

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationUtil {
    val MIGRATION_1_2 = object :Migration(1,2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                alter table `1_1_inputForMeasurementRegister` drop COLUMN `ishCharId`;
            """.trimIndent())
        }
    }
}