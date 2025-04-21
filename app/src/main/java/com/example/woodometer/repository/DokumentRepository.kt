package com.example.woodometer.repository

import com.example.woodometer.model.Dokument
import kotlinx.coroutines.flow.Flow
import java.util.UUID
class DokumentRepository : Repository<Dokument, UUID> {
    override suspend fun add(item: Dokument): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun get(key: UUID): Dokument? {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): List<Dokument> {
        TODO("Not yet implemented")
    }

    override fun observe(): Flow<List<Dokument>> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(key: UUID): Boolean {
        TODO("Not yet implemented")
    }

}
