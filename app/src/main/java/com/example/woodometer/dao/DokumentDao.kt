package com.example.woodometer.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.woodometer.model.Dokument
import java.util.UUID

@Dao
interface DokumentDao {

    @Query("SELECT * FROM dokument WHERE timestamp = (SELECT MAX(timestamp) FROM dokument)")
    fun getNewest() : Dokument?

    @Query("SELECT * FROM Dokument")
    fun getAll() : MutableList<Dokument>

    @Query("SELECT EXISTS(SELECT 1 FROM Dokument WHERE id = :id )")
    fun exists(id : UUID) : Boolean

    @Upsert
    fun add (vararg dokument: Dokument)

    @Delete
    fun delete(dokument: Dokument)

    @Query("SELECT * FROM Dokument WHERE id = :id")
    fun get(id : UUID) : Dokument

    @Query("SELECT COUNT(*) FROM dokument")
    fun count(): Int

    fun isEmpty(): Boolean {
        return count() == 0
    }
}