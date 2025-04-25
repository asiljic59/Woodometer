package com.example.woodometer.repository

import com.example.woodometer.Woodometer
import com.example.woodometer.model.Dokument
import java.util.UUID

class DokumentRepository  {
    private val dokumentDao = Woodometer.database.dokumentDao()
    fun add(vararg item: Dokument) {
        return dokumentDao.add(*item)
    }

    fun getAll(): MutableList<Dokument> {
        return dokumentDao.getAll()
    }
    fun getEarliest() : Dokument?{
        return dokumentDao.getEarliest()
    }

    fun exists(id : UUID) : Boolean{
        return dokumentDao.exists(id)
    }
}