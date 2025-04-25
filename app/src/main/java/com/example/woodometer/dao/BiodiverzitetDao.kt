package com.example.woodometer.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.woodometer.model.Biodiverzitet
import com.example.woodometer.model.Krug
import java.util.UUID
@Dao
interface BiodiverzitetDao {
    @Upsert
    fun upsert(vararg item : Biodiverzitet)

    @Query("SELECT * FROM biodiverzitet WHERE krugId = :krugId")
    fun getByKrug(krugId : UUID) : Biodiverzitet?
}