package com.example.woodometer.repository

import com.example.woodometer.Woodometer
import com.example.woodometer.model.Dokument
import com.example.woodometer.model.Krug
import java.util.UUID

class KrugRepository : Repository<Krug, UUID>{
    private val krugDao = Woodometer.database.krugDao()
    override suspend fun add(vararg item: Krug) {
        krugDao.add(*item)
    }
    override suspend fun upsert(vararg item: Krug) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(item: Krug) {
        krugDao.delete(item)
    }

    override suspend fun getById(id: UUID): Krug? {
       return  krugDao.getById(id)
    }
    fun getByDokument(dokumentId : UUID) : MutableList<Krug>{
        return krugDao.getByDokument(dokumentId)
    }

}