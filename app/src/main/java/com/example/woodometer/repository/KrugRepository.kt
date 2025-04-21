package com.example.woodometer.repository

import com.example.woodometer.model.Krug
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class KrugRepository : Repository<Krug, UUID> {
    override suspend fun add(item: Krug): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun get(key: UUID): Krug? {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): List<Krug> {
        TODO("Not yet implemented")
    }

    override fun observe(): Flow<List<Krug>> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(key: UUID): Boolean {
        TODO("Not yet implemented")
    }

}