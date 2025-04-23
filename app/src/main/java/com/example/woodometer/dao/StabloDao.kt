package com.example.woodometer.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.woodometer.model.Stablo
import java.util.UUID

@Dao
interface StabloDao {
    @Query("SELECT * FROM Stablo")
    fun getAll() : List<Stablo>

    @Query("SELECT * FROM Stablo WHERE id = :id")
    fun getById(id : UUID) : Stablo?

    @Insert
    fun add(vararg stabla : Stablo)

    @Delete
    fun delete(stablo : Stablo)

    @Query("SELECT * FROM Stablo WHERE krugId = :krugId")
    fun getByKrug(krugId : UUID) : MutableList<Stablo>

    @Update
    fun update(vararg stabla: Stablo)

    @Upsert
    fun upsert(vararg stabla : Stablo)

}