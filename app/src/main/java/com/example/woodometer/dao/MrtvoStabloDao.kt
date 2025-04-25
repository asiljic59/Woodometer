package com.example.woodometer.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.woodometer.model.MrtvoStablo
import java.util.UUID

@Dao
interface MrtvoStabloDao {
    @Query("SELECT * FROM MrtvoStablo WHERE id = :id")
    fun getById(id : UUID) : MrtvoStablo?

    @Insert
    fun add(vararg stabla : MrtvoStablo)

    @Delete
    fun delete(stablo : MrtvoStablo)

    @Query("SELECT * FROM MrtvoStablo WHERE krugId = :krugId")
    fun getByKrug(krugId : UUID) : MutableList<MrtvoStablo>

    @Update
    fun update(vararg stabla: MrtvoStablo)

    @Upsert
    fun upsert(vararg stabla : MrtvoStablo)
}