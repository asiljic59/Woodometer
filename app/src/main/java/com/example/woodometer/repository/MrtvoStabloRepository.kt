package com.example.woodometer.repository

import com.example.woodometer.Woodometer
import com.example.woodometer.model.MrtvoStablo
import java.util.UUID

class MrtvoStabloRepository : Repository<MrtvoStablo,UUID> {
    private val mrtvoStabloDao = Woodometer.database.mrtvoStabloDao()
    override suspend fun add(vararg item: MrtvoStablo) {
        mrtvoStabloDao.add(*item)
    }

    override suspend fun upsert(vararg item: MrtvoStablo) {
        mrtvoStabloDao.upsert(*item)
    }

    override suspend fun delete(item: MrtvoStablo) {
        mrtvoStabloDao.delete(item)
    }

    override suspend fun getById(id: UUID): MrtvoStablo? {
        TODO("Not yet implemented")
    }

    fun getByKrug(krugId : UUID) : MutableList<MrtvoStablo>{
        return mrtvoStabloDao.getByKrug(krugId)
    }

}