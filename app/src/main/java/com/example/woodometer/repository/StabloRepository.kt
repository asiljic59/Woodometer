package com.example.woodometer.repository

import com.example.woodometer.Woodometer
import com.example.woodometer.model.Stablo
import java.util.UUID

class StabloRepository : Repository<Stablo, UUID> {
    private val stabloDao = Woodometer.database.stabloDao()
    override suspend fun add(vararg item: Stablo){
        return stabloDao.add(*item)
    }

    override suspend fun upsert(vararg item: Stablo) {
        stabloDao.upsert(*item)
    }

    override suspend fun delete(item: Stablo) {
        stabloDao.delete(item)
    }

    override suspend fun getById(id: UUID): Stablo? {
        TODO("Not yet implemented")
    }
    fun getByKrug (id : UUID) : MutableList<Stablo>{
        return stabloDao.getByKrug(id)
    }
}