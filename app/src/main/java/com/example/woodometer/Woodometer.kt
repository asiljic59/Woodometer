package com.example.woodometer

import android.app.Application
import androidx.room.Room


class Woodometer : Application() {
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "woodometer"
            ).fallbackToDestructiveMigration(true)
        .build()
    }
}