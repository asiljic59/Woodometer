package com.example.woodometer

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import com.example.woodometer.model.Stablo
import com.example.woodometer.dao.DokumentDao
import com.example.woodometer.dao.KrugDao
import com.example.woodometer.dao.StabloDao

@Database(entities = [Dokument::class,Krug::class,Stablo::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dokumentDao(): DokumentDao
    abstract fun krugDao() : KrugDao
    abstract fun stabloDao() : StabloDao
}