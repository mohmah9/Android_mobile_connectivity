package com.iust.rhodium_android.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.iust.rhodium_android.data.entity.CellPowerDao
import com.iust.rhodium_android.data.model.CellPower


@Database(entities = arrayOf(CellPower::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cellPowerDao(): CellPowerDao
}