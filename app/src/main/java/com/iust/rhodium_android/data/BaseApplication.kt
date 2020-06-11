package com.iust.rhodium_android.data

import android.app.Application
import androidx.room.Room


class BaseApplication : Application() {
    public var appDatabase: AppDatabase? = null
    override fun onCreate() {
        super.onCreate()
        setupRoomDatabase()
    }

    private fun setupRoomDatabase() {
        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "signal_strength")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }fun getdb(): AppDatabase? {
        return appDatabase
    }
}