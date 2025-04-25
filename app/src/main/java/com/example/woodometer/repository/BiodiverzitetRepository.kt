package com.example.woodometer.repository

import com.example.woodometer.Woodometer
import com.example.woodometer.model.Biodiverzitet
import java.util.UUID

class BiodiverzitetRepository {
    private val biodiverzitetDao = Woodometer.database.biodiverzitetDao()

    fun upsert(vararg item: Biodiverzitet){
        biodiverzitetDao.upsert(*item)
    }

    fun getByKrug(id : UUID) : Biodiverzitet?{
        return biodiverzitetDao.getByKrug(id)
    }
}