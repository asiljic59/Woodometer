package com.example.woodometer.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.woodometer.model.Krug
import java.util.UUID

@Dao
interface KrugDao {
    @Query("SELECT * FROM Krug")
    fun getAll() : List<Krug>

    @Query("SELECT * FROM Krug WHERE id = :id")
    fun getById(id : UUID) : Krug?

    @Insert
    fun add(vararg krugovi : Krug)

    @Delete
    fun delete(krug : Krug)
}