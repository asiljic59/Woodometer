package com.example.woodometer.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.woodometer.model.Dokument
import java.util.UUID

@Dao
interface DokumentDao {

    @Query("SELECT * FROM dokument WHERE timestamp = (SELECT MIN(timestamp) FROM dokument)")
    fun getEarliest() : Dokument?

    @Query("SELECT * FROM Dokument")
    fun getAll() : MutableList<Dokument>

    @Query("SELECT EXISTS(SELECT 1 FROM Dokument WHERE id = :id)")
    fun exists(id : UUID) : Boolean

    @Insert
    fun add (vararg dokument: Dokument)
}