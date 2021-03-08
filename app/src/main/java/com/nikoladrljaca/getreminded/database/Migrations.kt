package com.nikoladrljaca.getreminded.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `deleted_reminder_table` (`date` INTEGER NOT NULL, `note` TEXT NOT NULL, `title` TEXT NOT NULL,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `deleted_reminder_table` (`date` INTEGER NOT NULL, `note` TEXT NOT NULL, `title` TEXT NOT NULL,`id` INTEGER NOT NULL, PRIMARY KEY(`id`))")
    }
}

val MIGRATION_3_4 = object : Migration(3,4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE reminder_table ADD COLUMN color INTEGER DEFAULT 0 NOT NULL")
    }
}