package com.example.storesmvvm.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.storesmvvm.common.entities.StoreEntity

/**
 * Define the database
 */
@Database(entities = arrayOf(StoreEntity::class), version = 2)
abstract class StoreDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
}