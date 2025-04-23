package com.example.woodometer.repository

import com.example.woodometer.Woodometer
import com.example.woodometer.model.Stablo
import java.util.UUID

class StabloRepository : Repository<Stablo, UUID> {
    private val stabloDao = Woodometer.database.stabloDao()
    override suspend fun add(vararg item: Stablo){
        return stabloDao.add(*item)
    }

    override suspend fun getAll(): List<Stablo> {
        TODO("Not yet implemented")
    }

    override suspend fun update(vararg item: Stablo) {
        stabloDao.update(*item)
    }

    override suspend fun delete(item: Stablo) {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: UUID): Stablo? {
        TODO("Not yet implemented")
    }
    fun getByKrug (id : UUID) : MutableList<Stablo>{
        return stabloDao.getByKrug(id)
    }
    fun upsert(vararg stablo : Stablo){
        stabloDao.upsert(*stablo)
    }
}