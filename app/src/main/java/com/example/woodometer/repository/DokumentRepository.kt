package com.example.woodometer.repository

import com.example.woodometer.Woodometer
import com.example.woodometer.model.Dokument
import java.util.UUID

class DokumentRepository : Repository<Dokument, UUID> {
    private val dokumentDao = Woodometer.database.dokumentDao()
    override suspend fun add(vararg item: Dokument) {
        return dokumentDao.add(*item)
    }

    override suspend fun getAll(): MutableList<Dokument> {
        return dokumentDao.getAll()
    }

    override suspend fun update(vararg item: Dokument) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(item: Dokument) {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: UUID): Dokument? {
        TODO("Not yet implemented")
    }

    fun getEarliest() : Dokument?{
        return dokumentDao.getEarliest()
    }

    fun exists(id : UUID) : Boolean{
        return dokumentDao.exists(id)
    }
}