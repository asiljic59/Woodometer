package com.example.woodometer

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.woodometer.model.Dokument
import com.example.woodometer.repository.DokumentDao

@Database(entities = [Dokument::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dokumentDao(): DokumentDao
}